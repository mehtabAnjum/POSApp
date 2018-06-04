package com.nagarro.pos.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nagarro.pos.constant.OrderStatus;
import com.nagarro.pos.constant.PaymentType;
import com.nagarro.pos.dao.CartProductMapperDao;
import com.nagarro.pos.dao.CustomerDao;
import com.nagarro.pos.dao.EmployeeDao;
import com.nagarro.pos.dao.OrderDao;
import com.nagarro.pos.dao.OrdersProductMapperDao;
import com.nagarro.pos.exception.CustomException;
import com.nagarro.pos.model.CartProductMapper;
import com.nagarro.pos.model.CashDrawer;
import com.nagarro.pos.model.Customer;
import com.nagarro.pos.model.Employee;
import com.nagarro.pos.model.Orders;
import com.nagarro.pos.model.OrdersProductMapper;

@Service
public class OrderService {

	@Autowired
	CustomerDao customerDao;

	@Autowired
	OrderDao orderDao;

	@Autowired
	CartService cartService;

	@Autowired
	CartProductMapperDao cartProductMapperDao;

	@Autowired
	OrdersProductMapperDao orderProductMapperDao;

	@Autowired
	EmployeeDao employeeDao;

	final Logger logger = Logger.getLogger(OrderService.class);

	@Transactional(rollbackFor = Exception.class)
	public Orders saveOrder(PaymentType paymentType, OrderStatus orderStatus, int custId, int empId, String orderId)
			throws CustomException {
		float cartTotalPrice = 0;
		final Customer customer = customerDao.getCustomerById(custId);
		if (customer == null) {
			throw new CustomException("Customer does not exist!");
		}
		final Employee employee = employeeDao.getEmployeeById(empId);
		Orders orders = null;
		if (!orderId.equals("")) {
			orders = getSavedOrderById(orderId, empId);
			if (orders == null) {
				throw new CustomException("Order id does not exist!");
			}
		} else {
			orders = new Orders();
			orders.setCustomer(customer);
			orders.setEmployee(employee);
		}
		orders.setCreated(new Date());
		orders.setUpdated(new Date());
		orders.setOrderDate(new Date());
		orders.setPaymentType(paymentType);
		orders.setOrderStatus(orderStatus);
		if (customer.getCart() == null) {
			throw new CustomException("Cart is Empty!");
		}
		for (final CartProductMapper cartProductMapper : customer.getCart().getCartProductMapper()) {
			if (orderStatus.equals(OrderStatus.COMPLETE)) {
				final int updatedStock = cartProductMapper.getProduct().getStock() - cartProductMapper.getQuantity();
				if (updatedStock >= 0) {
					cartProductMapper.getProduct().setStock(updatedStock);
				} else {
					throw new CustomException(
							"stock is not avaible for product : " + cartProductMapper.getProduct().getName());
				}
			}
			final OrdersProductMapper newOrdersProductMapper = new OrdersProductMapper(orders,
					cartProductMapper.getProduct(), cartProductMapper.getQuantity());
			orders.getOrdersProductMappers().add(newOrdersProductMapper);
			cartProductMapper.getProduct().getOrdersProductMappers().add(newOrdersProductMapper);
			orderDao.addOrdersProductMappers(newOrdersProductMapper);
			if (orderStatus.equals(OrderStatus.COMPLETE)) {
				cartTotalPrice += cartProductMapper.getProduct().getPrice();
			}
		}
		if (orderId.equals("")) {
			final Orders createdOrder = orderDao.addOrder(orders);
			employee.getOrder().add(createdOrder);
			customer.getOrder().add(createdOrder);
		}
		cartService.removeCustomerCart(custId);
		final CashDrawer cashDrawer = employee.getCashDrawer().get(employee.getCashDrawer().size() - 1);
		if (orderStatus.equals(OrderStatus.COMPLETE)) {
			cashDrawer.setEndBal(cashDrawer.getEndBal() + cartTotalPrice);
		}
		return orders;
	}

	@Transactional
	public Map<String, List<Orders>> getSavedOrder(int empId) throws CustomException {

		final Employee currEmp = employeeDao.getEmployeeById(empId);
		if (currEmp.getOrder().isEmpty()) {
			throw new CustomException("No Orders exist!");
		}

		final Map<String, List<Orders>> sortedSavedOrders = new TreeMap<>();
		final List<Orders> ordersList = currEmp.getOrder();

		final List<Orders> savedOrderList = new ArrayList<>();

		for (Orders order : ordersList) {
			if (order.getOrderStatus().equals(OrderStatus.PENDING)) {
				savedOrderList.add(order);
			}
		}

		for (final Orders currOrder : savedOrderList) {
			if (!currOrder.getOrdersProductMappers().isEmpty()) {
				final String date = currOrder.getOrderDate().toString().split(" ")[0];
				if (sortedSavedOrders.containsKey(date)) {
					sortedSavedOrders.get(date).add(currOrder);
				} else {
					sortedSavedOrders.put(date, new ArrayList<Orders>());
					sortedSavedOrders.get(date).add(currOrder);
				}
			}
		}

		return sortedSavedOrders;
	}

	@Transactional
	public List<Orders> getCompleteOrder(int empId) throws CustomException {
		final Employee currEmp = employeeDao.getEmployeeById(empId);
		if (currEmp.getOrder().isEmpty()) {
			throw new CustomException("No Orders exist!");
		}
		final List<Orders> completeOrders = new ArrayList<>();
		final List<Orders> allOrders = currEmp.getOrder();
		for (final Orders currOrder : allOrders) {
			if (currOrder.getOrderStatus().equals(OrderStatus.COMPLETE)) {
				completeOrders.add(currOrder);
			}
		}
		return completeOrders;
	}

	@Transactional
	public Orders getSavedOrderById(String orderId, int empId) throws CustomException {
		final Employee currEmp = employeeDao.getEmployeeById(empId);
		Orders order = null;
		if (currEmp.getOrder() == null) {
			throw new CustomException("No Orders exist!");
		}
		final List<Orders> allOrders = currEmp.getOrder();
		for (final Orders currOrder : allOrders) {
			if (currOrder.getOrderStatus().equals(OrderStatus.PENDING)
					&& currOrder.getOrderId().equalsIgnoreCase(orderId)) {
				order = currOrder;
				break;
			}
		}
		return order;
	}

	@Transactional
	public Map<String, List<Orders>> getAllOrders(int empId) throws CustomException {
		final Employee currEmp = employeeDao.getEmployeeById(empId);
		if (currEmp.getOrder().isEmpty()) {
			throw new CustomException("No Orders exist!");
		}
		final Map<String, List<Orders>> sortedOrders = new TreeMap<>();
		final List<Orders> ordersList = currEmp.getOrder();
		for (final Orders currOrder : ordersList) {
			if (!currOrder.getOrdersProductMappers().isEmpty()) {
				final String date = currOrder.getOrderDate().toString().split(" ")[0];
				if (sortedOrders.containsKey(date)) {
					sortedOrders.get(date).add(currOrder);
				} else {
					sortedOrders.put(date, new ArrayList<Orders>());
					sortedOrders.get(date).add(currOrder);
				}
			}
		}

		return sortedOrders;
	}

	@Transactional
	public Orders reloadSavedOrder(String orderId, int empId) throws CustomException {
		final Orders savedOrder = getSavedOrderById(orderId, empId);
		if (savedOrder == null) {
			throw new CustomException("No Order exist!");
		}
		for (final OrdersProductMapper ordersProductMapper : savedOrder.getOrdersProductMappers()) {
			cartService.addProductToCart(ordersProductMapper.getProduct().getId(), savedOrder.getCustomer().getId(),
					ordersProductMapper.getQuantity());
			orderProductMapperDao.removeOrderProductMapper(ordersProductMapper);

		}
		return savedOrder;

	}

	@Transactional
	public Orders getOrderById(String orderId, int empId) throws CustomException {
		final Employee currEmp = employeeDao.getEmployeeById(empId);
		final List<Orders> ordersList = currEmp.getOrder();
		for (final Orders currOrder : ordersList) {
			if (currOrder.getOrderId().equalsIgnoreCase(orderId)) {
				return currOrder;
			}
		}
		return null;
	}

}

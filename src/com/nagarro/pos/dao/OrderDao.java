package com.nagarro.pos.dao;

import java.util.List;

import com.nagarro.pos.exception.CustomException;
import com.nagarro.pos.model.Orders;
import com.nagarro.pos.model.OrdersProductMapper;

public interface OrderDao {

	public Orders addOrder(Orders orders);

	List<Orders> getCompleteOrder(int empId) throws CustomException;

	Orders getOrderById(String orderId, int empId) throws CustomException;

	List<Orders> getAllOrders() throws CustomException;

	boolean addOrdersProductMappers(OrdersProductMapper ordersProductMapper);

	public boolean deleteOrder(Orders savedOrder);

}

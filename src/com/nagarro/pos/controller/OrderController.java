package com.nagarro.pos.controller;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nagarro.pos.constant.Constant;
import com.nagarro.pos.constant.OrderStatus;
import com.nagarro.pos.constant.PaymentType;
import com.nagarro.pos.dto.ErrorMessageResponseDto;
import com.nagarro.pos.dto.OrderResponseDto;
import com.nagarro.pos.dto.OrderListResponseDto;
import com.nagarro.pos.exception.CustomException;
import com.nagarro.pos.model.Employee;
import com.nagarro.pos.model.Orders;
import com.nagarro.pos.service.OrderService;
import com.nagarro.pos.utilities.UserProperties;
import com.nagarro.pos.validator.Validator;

/**
 * @author mehtabanjum
 *
 */
/**
 * @author mehtabanjum
 *
 */
@Controller
@RequestMapping(value = "/orders")
public class OrderController {

	final Logger logger = Logger.getLogger(OrderController.class);
	static Properties prop = UserProperties.getProperties();
	@Autowired
	OrderService ordersService;

	/**
	 * @param request
	 * @param response
	 * @param session
	 * @param paymode
	 * @param status
	 * @param custId
	 * @param orderId
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Object> postOrder(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, @RequestParam("paymode") String paymode, @RequestParam("status") String status,
			@RequestParam("custId") String custId, @RequestParam("orderId") String orderId) {

		ResponseEntity<Object> responseEntity = null;
		try {
			Validator.validateFieldNumber(custId);
			Validator.validatePaymode(paymode);
			Validator.validateOrderStatus(status);

		} catch (final CustomException e2) {
			logger.error(e2);
			return ErrorMessageResponseDto.errorMessage(e2.getMessage());
		}
		try {
			final Orders order = ordersService.saveOrder(PaymentType.valueOf(paymode.toUpperCase()),
					OrderStatus.valueOf(status.toUpperCase()), Integer.parseInt(custId),
					((Employee) session.getAttribute(Constant.EMPLOYEE)).getId(), orderId);
			if (order == null) {
				responseEntity = ErrorMessageResponseDto.errorMessage(prop.getProperty("ORDER_EXCEP"));
			} else {
				responseEntity = ResponseEntity.ok()
						.body(new OrderResponseDto(order.getOrderId(), order.getOrderDate(),
								order.getOrderStatus().toString(), order.getPaymentType().toString(),
								order.getCustomer().getFirstName(), order.getCustomer().getId(),
								order.getOrdersProductMappers(), true));
			}
		} catch (final CustomException e) {
			responseEntity = ErrorMessageResponseDto.errorMessage(e.getMessage());
		}
		return responseEntity;
	}

	/**
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Object> getAllOrders(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {

		final Employee currEmp = (Employee) session.getAttribute("emp");
		Map<String, List<Orders>> allOrdersList = new TreeMap<>();
		try {
			allOrdersList = ordersService.getAllOrders(currEmp.getId());
		} catch (final CustomException e) {
			logger.error(e);
			return ErrorMessageResponseDto.errorMessage(e.getMessage());
		}
		return ResponseEntity.ok().body(new OrderListResponseDto(allOrdersList));

	}

	/**
	 * @param request
	 * @param response
	 * @param session
	 * @param orderId
	 * @return
	 */
	@RequestMapping(value = "/{oid}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Object> getOrderById(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, @PathVariable("oid") String orderId) {
		Orders order = new Orders();
		final Employee employee = (Employee) session.getAttribute("emp");
		try {
			order = ordersService.getOrderById(orderId, employee.getId());
		} catch (final CustomException e) {
			logger.error(e);
			return ErrorMessageResponseDto.errorMessage(e.getMessage());
		}
		return ResponseEntity.ok()
				.body(new OrderResponseDto(order.getOrderId(), order.getOrderDate(), order.getOrderStatus().toString(),
						order.getPaymentType().toString(), order.getCustomer().getFirstName(),
						order.getCustomer().getId(), order.getOrdersProductMappers(), true));
	}

	/**
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/savedorder", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Object> getSavedOrder(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {

		Map<String, List<Orders>> savedOrderList = new TreeMap<>();
		final Employee employee = (Employee) session.getAttribute("emp");
		try {
			savedOrderList = ordersService.getSavedOrder(employee.getId());
		} catch (final CustomException e) {
			logger.error(e);
			return ErrorMessageResponseDto.errorMessage(e.getMessage());
		}
		return ResponseEntity.ok().body(new OrderListResponseDto(savedOrderList));
	}

	/**
	 * @param request
	 * @param response
	 * @param session
	 * @param orderId
	 * @return
	 */
	@RequestMapping(value = "/savedorder/{oid}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Object> getSavedOrderById(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, @PathVariable("oid") String orderId) {
		try {
			Validator.validateField(orderId);
		} catch (final CustomException e2) {
			logger.error(e2);
			return ErrorMessageResponseDto.errorMessage(e2.getMessage());
		}
		Orders savedOrder = null;
		final Employee employee = (Employee) session.getAttribute("emp");

		try {
			savedOrder = ordersService.getSavedOrderById(orderId, employee.getId());
		} catch (final CustomException e) {
			logger.error(e);
			return ErrorMessageResponseDto.errorMessage(e.getMessage());
		}
		return ResponseEntity.ok()
				.body(new OrderResponseDto(savedOrder.getOrderId(), savedOrder.getOrderDate(),
						savedOrder.getOrderStatus().toString(), savedOrder.getPaymentType().toString(),
						savedOrder.getCustomer().getFirstName(), savedOrder.getCustomer().getId(),
						savedOrder.getOrdersProductMappers(), true));
	}

	/**
	 * @param request
	 * @param response
	 * @param session
	 * @param orderId
	 * @return
	 */
	@RequestMapping(value = "/savedorder/{oid}/reload", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Object> reloadSavedOrder(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, @PathVariable("oid") String orderId) {
		try {
			Validator.validateField(orderId);
		} catch (final CustomException e2) {
			logger.error(e2);
			return ErrorMessageResponseDto.errorMessage(e2.getMessage());
		}
		Orders savedOrder = null;
		final Employee employee = (Employee) session.getAttribute("emp");

		try {
			savedOrder = ordersService.reloadSavedOrder(orderId, employee.getId());
		} catch (final CustomException e) {
			logger.error(e);
			return ErrorMessageResponseDto.errorMessage(e.getMessage());
		}
		return ResponseEntity.ok()
				.body(new OrderResponseDto(savedOrder.getOrderId(), savedOrder.getOrderDate(),
						savedOrder.getOrderStatus().toString(), savedOrder.getPaymentType().toString(),
						savedOrder.getCustomer().getFirstName(), savedOrder.getCustomer().getId(),
						savedOrder.getOrdersProductMappers(), true));
	}

}

package com.nagarro.pos.dto;

import java.util.List;
import java.util.Map;

import com.nagarro.pos.model.Orders;

public class OrderListResponseDto {

	Map<String, List<Orders>> orders;
	boolean status = true;

	public Map<String, List<Orders>> getOrders() {
		return orders;
	}

	public void setOrders(Map<String, List<Orders>> orders) {
		this.orders = orders;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public OrderListResponseDto(Map<String, List<Orders>> orders) {
		super();
		this.orders = orders;
	}

}

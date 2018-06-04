package com.nagarro.pos.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nagarro.pos.constant.OrderStatus;
import com.nagarro.pos.constant.PaymentType;

@Entity
public class Orders extends AbstractTimestampEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1530339338969797035L;

	@Id
	@GenericGenerator(name = "order_id", strategy = "com.nagarro.pos.utilities.GenerateOrderId")
	@GeneratedValue(generator = "order_id")
	private String orderId;
	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM dd,yyyy#hh:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	private Date orderDate;
	@Enumerated(EnumType.STRING)
	private PaymentType paymentType;

	@JsonProperty("items")
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "orders", cascade = CascadeType.DETACH)
	private final Set<OrdersProductMapper> ordersProductMappers = new HashSet<>();

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL)
	private Employee employee;

	// @JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL)
	private Customer customer;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public Set<OrdersProductMapper> getOrdersProductMappers() {
		return ordersProductMappers;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Orders() {
		this.setCreated(new Date());
		this.setUpdated(this.getCreated());
	}

}

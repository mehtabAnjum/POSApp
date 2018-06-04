package com.nagarro.pos.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Cart extends AbstractTimestampEntity implements Serializable {

	private static final long serialVersionUID = 1136712193763685030L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@JsonProperty("cartItems")
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "cart", cascade = CascadeType.DETACH)
	private Set<CartProductMapper> cartProductMapper = new HashSet<>();

	@JsonIgnore
	@OneToOne(cascade = CascadeType.DETACH)
	private Customer customer;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Set<CartProductMapper> getCartProductMapper() {
		return cartProductMapper;
	}

	public void setCartProductMapper(Set<CartProductMapper> cartProductMapper) {
		this.cartProductMapper = cartProductMapper;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Cart() {
		this.setCreated(new Date());
		this.setUpdated(this.getCreated());
	}

}

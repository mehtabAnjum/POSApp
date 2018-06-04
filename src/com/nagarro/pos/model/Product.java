package com.nagarro.pos.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Product implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5094159898920442975L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String pcode;
	private String name;
	private int stock;
	private String description;
	private float price;

	@JsonIgnore
	@OneToMany(mappedBy = "product")
	private Set<CartProductMapper> cartProductMapper = new HashSet<>();

	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "product")
	private final Set<OrdersProductMapper> ordersProductMappers = new HashSet<>();

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPcode() {
		return pcode;
	}

	public void setPcode(String pcode) {
		this.pcode = pcode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<CartProductMapper> getCartProductMapper() {
		return cartProductMapper;
	}

	public void setCartProductMapper(Set<CartProductMapper> cartProductMapper) {
		this.cartProductMapper = cartProductMapper;
	}

	public Set<OrdersProductMapper> getOrdersProductMappers() {
		return ordersProductMappers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Product other = (Product) obj;
		return id != other.id;
	}

}

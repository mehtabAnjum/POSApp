package com.nagarro.pos.dao;

import java.util.List;

import com.nagarro.pos.exception.CustomException;
import com.nagarro.pos.model.Customer;

public interface CustomerDao {

	public Customer getCustomerById(int custId) throws CustomException;

	List<Customer> getCustomer(String toSearch);

}

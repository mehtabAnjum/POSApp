package com.nagarro.pos.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nagarro.pos.dao.CustomerDao;
import com.nagarro.pos.dto.CustomerSearchDto;
import com.nagarro.pos.exception.CustomException;
import com.nagarro.pos.model.Customer;

@Service
public class CustomerService {
	@Autowired
	CustomerDao customerDao;

	@Transactional
	public Customer getCustomerById(int custId) throws CustomException {
		return customerDao.getCustomerById(custId);
	}

	@Transactional
	public List<CustomerSearchDto> getCustomers(String toSearch) {

		final List<CustomerSearchDto> customerDtoList = new ArrayList<>();
		for (final Customer c : customerDao.getCustomer(toSearch)) {
			final CustomerSearchDto customerSearchDto = new CustomerSearchDto(c.getId(), c.getFirstName(),
					c.getLastName(), c.getEmail(), c.getMobile());
			customerDtoList.add(customerSearchDto);
		}
		return customerDtoList;
	}
}

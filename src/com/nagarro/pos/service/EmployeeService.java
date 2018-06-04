package com.nagarro.pos.service;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nagarro.pos.dao.EmployeeDao;
import com.nagarro.pos.exception.CustomException;
import com.nagarro.pos.model.CashDrawer;
import com.nagarro.pos.model.Employee;
import com.nagarro.pos.model.UserSecret;

@Service
public class EmployeeService {

	final Logger logger = Logger.getLogger(EmployeeService.class);

	@Autowired
	EmployeeDao employeeDao;

	@Transactional(readOnly = true)
	public Employee checkUser(String email, String password) throws CustomException {
		Employee emp = null;
		try {
			final Employee currEmp = employeeDao.getEmp(email);
			final UserSecret userSecret = employeeDao.getPass(currEmp.getId());
			if (currEmp != null && userSecret != null) {
				if (userSecret.getPass().equals(password)) {
					emp = currEmp;
				}
			}
		} catch (final CustomException e) {
			e.printStackTrace();
			throw new CustomException(e.getMessage());
		}

		return emp;
	}

	@Transactional
	public boolean addCashDrawer(int startBal, Employee emp) throws CustomException {

		boolean isAdded = false;
		final CashDrawer cashDrawer = new CashDrawer();
		cashDrawer.setEmployee(emp);
		cashDrawer.setStartBal(startBal);
		cashDrawer.setEndBal(startBal);
		cashDrawer.setDate(new Date());
		cashDrawer.setCreated(new Date());
		cashDrawer.setUpdated(new Date());
		emp.getCashDrawer().add(cashDrawer);
		emp.setCreated(new Date());
		emp.setUpdated(new Date());
		try {
			employeeDao.addCashDrawer(cashDrawer);
			employeeDao.updateEmployee(emp);
			isAdded = true;
		} catch (final CustomException e) {
			e.printStackTrace();
			throw new CustomException(e.getMessage());
		}
		return isAdded;

	}

	@Transactional
	public Employee getEmployeeById(int empId) throws CustomException {
		return employeeDao.getEmployeeById(empId);
	}

	@Transactional
	public List<Employee> getAllEmployees() throws CustomException {

		return employeeDao.getAllEmployees();
	}

}

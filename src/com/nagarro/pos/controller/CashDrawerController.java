package com.nagarro.pos.controller;

import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nagarro.pos.dto.CashDrawerDto;
import com.nagarro.pos.model.CashDrawer;
import com.nagarro.pos.model.Employee;
import com.nagarro.pos.service.EmployeeService;
import com.nagarro.pos.utilities.UserProperties;

@Controller
@RequestMapping(value = "/cashdrawer")
public class CashDrawerController {

	final Logger logger = Logger.getLogger(CashDrawerController.class);
	static Properties prop = UserProperties.getProperties();

	@Autowired
	EmployeeService employeeService;

	/**
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Object> getCashDrawer(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		final Employee emp = (Employee) session.getAttribute("emp");
		List<CashDrawer> cashDrawer = emp.getCashDrawer();
		return ResponseEntity.ok().body(new CashDrawerDto(cashDrawer, true));
	}

}

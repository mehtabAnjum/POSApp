package com.nagarro.pos.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nagarro.pos.constant.Constant;
import com.nagarro.pos.dto.EmployeeLoginDto;
import com.nagarro.pos.dto.ErrorMessageResponseDto;
import com.nagarro.pos.exception.CustomException;
import com.nagarro.pos.model.CashDrawer;
import com.nagarro.pos.model.Employee;
import com.nagarro.pos.service.EmployeeService;
import com.nagarro.pos.utilities.UserProperties;
import com.nagarro.pos.validator.Validator;

@Controller
@RequestMapping(value = "/employees")
public class EmployeeController {
	static Properties prop = UserProperties.getProperties();
	final Logger logger = Logger.getLogger(EmployeeController.class);

	@Autowired
	EmployeeService employeeService;

	/**
	 * @param request
	 * @param response
	 * @param session
	 * @param employee
	 * @param startBal
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Object> empLogin(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, @ModelAttribute("employee") Employee employee,
			@RequestParam("startbal") String startBal) {
		try {
			Validator.validateField(employee.getEmail());
			Validator.validateField(employee.getUserSecret().getPass());
			Validator.validateFieldNumber(startBal);
		} catch (final CustomException e2) {
			logger.error(e2);
			return ErrorMessageResponseDto.errorMessage(e2.getMessage());
		}
		Employee emp = null;
		try {
			emp = employeeService.checkUser(employee.getEmail(), employee.getUserSecret().getPass());
		} catch (final CustomException e) {
			logger.error(e);
			return ErrorMessageResponseDto.errorMessage(e.getMessage());
		}
		if (emp != null) {
			try {
				employeeService.addCashDrawer(Integer.parseInt(startBal), emp);
			} catch (final CustomException e) {
				logger.error(e);
				return ErrorMessageResponseDto.errorMessage(e.getMessage());
			}
			session.setAttribute(Constant.EMPLOYEE, emp);
			final CashDrawer cashDrawer = emp.getCashDrawer().get(emp.getCashDrawer().size() - 1);
			final EmployeeLoginDto employeeLoginDto = new EmployeeLoginDto(emp.getId(), emp.getFirstName(),
					emp.getLastName(), emp.getEmail(), emp.getMobile(), cashDrawer.getId());
			return ResponseEntity.ok().body(employeeLoginDto);
		}
		return ErrorMessageResponseDto.errorMessage(prop.getProperty("WRONG_CREDENTIALS"));
	}

	/**
	 * @param request
	 * @param response
	 * @param session
	 * @param cashDrawerId
	 * @return
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Object> empLogout(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, @RequestParam("cashdrawerid") String cashDrawerId) {
		Employee emp;
		CashDrawer cashDrawer;
		try {
			Validator.validateField(cashDrawerId);
		} catch (final CustomException e2) {
			logger.error(e2);
			return ErrorMessageResponseDto.errorMessage(e2.getMessage());
		}
		try {
			emp = employeeService.getEmployeeById(((Employee) session.getAttribute(Constant.EMPLOYEE)).getId());

			cashDrawer = emp.getCashDrawer().stream().filter(p -> p.getId() == Integer.parseInt(cashDrawerId)).findAny()
					.orElse(null);
		} catch (final CustomException e) {
			return ErrorMessageResponseDto.errorMessage((e.getMessage()));
		}
		session.invalidate();
		return ResponseEntity.ok().body(cashDrawer);

	}

	/**
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "nosession", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Object> sessionError(HttpServletRequest request, HttpServletResponse response) {
		return ErrorMessageResponseDto.errorMessage(prop.getProperty("LOGIN_ERROR"));
	}

	/**
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Object> getAllEmployees(HttpServletRequest request, HttpServletResponse response) {

		List<Employee> empList = new ArrayList<>();

		try {
			empList = employeeService.getAllEmployees();
		} catch (final CustomException e) {
			return ErrorMessageResponseDto.errorMessage((e.getMessage()));
		}

		return ResponseEntity.ok().body(empList);
	}

}

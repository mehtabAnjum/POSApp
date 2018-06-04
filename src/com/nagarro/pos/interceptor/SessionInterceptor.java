package com.nagarro.pos.interceptor;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.nagarro.pos.constant.Constant;
import com.nagarro.pos.model.Employee;

public class SessionInterceptor extends HandlerInterceptorAdapter {

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws IOException {
		if (!request.getRequestURI().equals("/POSApplication/employees")) {

			Employee employee = (Employee) session.getAttribute(Constant.EMPLOYEE);

			if (employee == null) {
				response.sendRedirect("employees");
				return false;
			}
		}
		return true;
	}

}

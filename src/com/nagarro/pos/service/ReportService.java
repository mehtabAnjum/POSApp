package com.nagarro.pos.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ServletContextAware;

import com.nagarro.pos.constant.Constant;
import com.nagarro.pos.dao.EmployeeDao;
import com.nagarro.pos.dao.OrderDao;
import com.nagarro.pos.exception.CustomException;
import com.nagarro.pos.model.Orders;
import com.nagarro.pos.model.OrdersProductMapper;

@Service
public class ReportService implements ServletContextAware {

	@Autowired
	EmployeeDao employeeDao;

	@Autowired
	OrderDao orderDao;

	final Logger logger = Logger.getLogger(ReportService.class);

	ServletContext ctx;

	@Transactional
	public void excelGenerator(Date start, Date end, int empId) throws CustomException, IOException, ParseException {

		List<Orders> allOrders = new ArrayList<>();

		if (empId == -1) {
			allOrders = orderDao.getAllOrders();
			System.out.println("orerds length: " + allOrders.size());
		} else {
			allOrders = employeeDao.getEmployeeById(empId).getOrder();
			System.out.println("orerds length: " + allOrders.size());
		}

		FileOutputStream fileOut = null;
		String absolutePath = Constant.ABSOLUTE_PATH;

		final String filename = absolutePath + "\\order.xlsx";
		try (FileOutputStream file = new FileOutputStream(new File(filename))) {

			final XSSFWorkbook hwb = new XSSFWorkbook();
			final XSSFSheet sheet = hwb.createSheet("new");
			final Row rowhead = sheet.createRow((short) 0);
			rowhead.createCell((short) 0).setCellValue("empId");
			rowhead.createCell((short) 1).setCellValue("empName");
			rowhead.createCell((short) 2).setCellValue("email");
			rowhead.createCell((short) 3).setCellValue("mobile");
			rowhead.createCell((short) 4).setCellValue("orderId");
			rowhead.createCell((short) 5).setCellValue("orderStatus");
			rowhead.createCell((short) 6).setCellValue("paymentType");
			rowhead.createCell((short) 7).setCellValue("payment");

			int i = 1;
			long total = 0;

			for (final Orders order : allOrders) {
				int sum = 0;
				final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				final String orderDate = dateFormat.format(order.getOrderDate());
				final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				final Date dateOfOrder = sdf.parse(orderDate);
				for (final OrdersProductMapper ordersProductMapper : order.getOrdersProductMappers()) {
					sum += (ordersProductMapper.getProduct().getPrice() * ordersProductMapper.getQuantity());
				}
				if (dateOfOrder.compareTo(end) <= 0 && dateOfOrder.compareTo(start) >= 0) {
					final Row row = sheet.createRow(i++);
					row.createCell(0).setCellValue(order.getEmployee().getId());
					row.createCell(1)
							.setCellValue(order.getEmployee().getFirstName().concat(order.getEmployee().getLastName()));
					row.createCell(2).setCellValue(order.getEmployee().getEmail());
					row.createCell(3).setCellValue(order.getEmployee().getMobile());
					row.createCell(4).setCellValue(order.getOrderId());
					row.createCell(5).setCellValue(order.getOrderStatus().toString());
					row.createCell(6).setCellValue(order.getPaymentType().toString());
					row.createCell(7).setCellValue(sum);
					total += sum;
				}
			}
			final Row row = sheet.createRow(++i);
			row.createCell(6).setCellValue("Total");
			row.createCell(7).setCellValue(total);
			fileOut = new FileOutputStream(filename);
			hwb.write(file);
			fileOut.close();
		}
	}

	@Override
	public void setServletContext(ServletContext arg0) {
		this.ctx = arg0;

	}
}
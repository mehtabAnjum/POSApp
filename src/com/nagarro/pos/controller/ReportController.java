package com.nagarro.pos.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;

import com.nagarro.pos.constant.Constant;
import com.nagarro.pos.dto.ErrorMessageResponseDto;
import com.nagarro.pos.exception.CustomException;
import com.nagarro.pos.service.ReportService;
import com.nagarro.pos.validator.Validator;

@Controller
@RequestMapping("/reports")
public class ReportController implements ServletContextAware {

	final Logger logger = Logger.getLogger(ReportController.class);

	@Autowired
	ReportService reportService;

	ServletContext ctx;

	/**
	 * @param request
	 * @param response
	 * @param session
	 * @param startDate
	 * @param endDate
	 * @param empId
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Object> generateReport(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, @RequestParam(value = "startDate", required = true) String startDate,
			@RequestParam(value = "endDate", required = true) String endDate,
			@RequestParam(value = "empId", required = true) String empId) throws IOException, ParseException {

		Date start;
		Date end;
		try {
			Validator.validateField(endDate);
			Validator.validateField(startDate);
		} catch (final CustomException e2) {
			logger.error(e2);
			return ErrorMessageResponseDto.errorMessage(e2.getMessage());
		}
		try {
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			start = sdf.parse(startDate);
			end = sdf.parse(endDate);
		} catch (final Exception e2) {
			logger.error(e2);
			return ErrorMessageResponseDto.errorMessage(e2.getMessage());
		}

//		String absolutePath =  ctx.getRealPath("WEB-INF\\resources");
		final File file = new File(Constant.ABSOLUTE_PATH + "\\order.xlsx");
		final InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

		try {
			reportService.excelGenerator(start, end, Integer.valueOf(empId));

		} catch (final CustomException e) {
			logger.error(e);
			return ErrorMessageResponseDto.errorMessage(e.getMessage());
		}

		return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=\"testEmployee.xlsx\"")
				.contentType(MediaType.parseMediaType("application/vnd.ms-excel")).contentLength(file.length())
				.body(resource);
	}

	@Override
	public void setServletContext(ServletContext arg0) {
		this.ctx = arg0;
	}

}
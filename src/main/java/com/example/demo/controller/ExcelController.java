package com.example.demo.controller;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.TUser;
import com.example.demo.formbean.OrderFormBean;
import com.example.demo.service.UserService;

@RestController
@RequestMapping(value = "/excel")
public class ExcelController {

	@Autowired
	private UserService userService;

	@Value("${activity.file.path}")
	private String tempFilePath;
	
	// 创建表头
	private void createTitle(HSSFWorkbook workbook, HSSFSheet sheet) {
		HSSFRow row = sheet.createRow(0);
		// 设置列宽，setColumnWidth的第二个参数要乘以256，这个参数的单位是1/256个字符宽度
		sheet.setColumnWidth(1, 12 * 256);
		sheet.setColumnWidth(3, 17 * 256);

		// 设置为居中加粗
		HSSFCellStyle style = workbook.createCellStyle();
		HSSFFont font = workbook.createFont();
		font.setBold(true);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFont(font);

		HSSFCell cell;
		cell = row.createCell(0);
		cell.setCellValue("參加人姓名");
		cell.setCellStyle(style);

		cell = row.createCell(1);
		cell.setCellValue("參加人電話	");
		cell.setCellStyle(style);

		cell = row.createCell(2);
		cell.setCellValue("參加人信箱	");
		cell.setCellStyle(style);

		cell = row.createCell(3);
		cell.setCellValue("票卷數量");
		cell.setCellStyle(style);

		cell = row.createCell(4);
		cell.setCellValue("票卷價格");
		cell.setCellStyle(style);

		cell = row.createCell(5);
		cell.setCellValue("總金額	");
		cell.setCellStyle(style);

		cell = row.createCell(6);
		cell.setCellValue("狀態");
		cell.setCellStyle(style);

	}

	// 生成user表excel
	@GetMapping(value = "/download")
	public String getUser(HttpServletResponse response, Model model, HttpSession session) throws Exception {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("统计表");
		createTitle(workbook, sheet);
		List<TUser> rows = userService.selectAll();
		@SuppressWarnings("unchecked")
		List<OrderFormBean> userList = (List<OrderFormBean>) session.getAttribute("userList");

		int rowNum = 1;
		for (OrderFormBean bean : userList) {
			HSSFRow row = sheet.createRow(rowNum);
			row.createCell(0).setCellValue(bean.getUserName());
			row.createCell(1).setCellValue(bean.getMobile());
			row.createCell(2).setCellValue(bean.getEmail());
			row.createCell(3).setCellValue(bean.getBuyTicket());
			row.createCell(4).setCellValue(bean.getTicketPrice());
			row.createCell(5).setCellValue(bean.getTotalPrice());
			String status = null;
			if (bean.getOrderStatus() == 0)
				status = "未付款";
			else if (bean.getOrderStatus() == 2)
				status = "已付款";
			else
				status = "取消";
			row.createCell(6).setCellValue(status);

			rowNum++;
		}

		String fileName = "memberlist.xls";

		// 生成excel文件
		buildExcelFile(fileName, workbook);

		// 浏览器下载excel
		buildExcelDocument(fileName, workbook, response);

		return "activity/browser";
	}

	// 生成excel文件
	protected void buildExcelFile(String filename, HSSFWorkbook workbook) throws Exception {
		FileOutputStream fos = new FileOutputStream(tempFilePath + "/" + filename);
		workbook.write(fos);
		fos.flush();
		fos.close();
	}

	// 浏览器下载excel
	protected void buildExcelDocument(String filename, HSSFWorkbook workbook, HttpServletResponse response)
			throws Exception {
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "utf-8"));
		OutputStream outputStream = response.getOutputStream();
		workbook.write(outputStream);
		outputStream.flush();
		outputStream.close();
	}

}
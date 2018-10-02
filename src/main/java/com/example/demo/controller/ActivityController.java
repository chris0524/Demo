package com.example.demo.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.entity.TActivity;
import com.example.demo.entity.TOrder;
import com.example.demo.entity.TOrderDetail;
import com.example.demo.entity.TTicket;
import com.example.demo.formbean.ActivityFormBean;
import com.example.demo.formbean.OrderFormBean;
import com.example.demo.service.ActivityService;
import com.example.demo.service.OrderService;

@Controller
@RequestMapping(value = "/activity", method = RequestMethod.GET)
public class ActivityController {

	@Value("${activity.file.path}")
	private String imageFilePath;

	@Autowired
	private OrderService orderService;

	@Autowired
	private ActivityService activityService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Model model) {

		return "redirect:/activity/list";
	}

	// 時間格式
	DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

	@PreAuthorize("hasAnyAuthority('U','M','T','P')")
	@RequestMapping(value = "/select", method = RequestMethod.GET)
	public String home(Model model) {
		// model.addAttribute("typeList",activityService.getAllTActivityType());
		return "activity/selectCreate";
	}

	// 付費活動
	@PreAuthorize("hasAnyAuthority('U','M','T','P')")
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String toAdd(Model model) {
		model.addAttribute("typeList", activityService.getAllTActivityType());
		return "activity/create";
	}

	@PreAuthorize("hasAnyAuthority('U','M','T','P')")
	@RequestMapping(value = "/doAdd", method = RequestMethod.POST)
	public ModelAndView doAdd(@ModelAttribute ActivityFormBean activity,
			@RequestParam("imageFile") MultipartFile file) {

		TActivity entity = new TActivity();
		entity.setActivityName(activity.getActivityName());

		entity.setActivityDate(Timestamp.valueOf(activity.getActivityDate().replace("T", " ") + ":00"));
		entity.setActivityPlace(activity.getActivityPlace());
		entity.setActiivityDescription(activity.getActiivityDescription());
		entity.setHostTel(activity.getHostTel());
		entity.setHostMail(activity.getHostMail());
		entity.setActivityType(activity.getActivityType());
		entity.setActivityStatus("0");
		entity.setEndTime(Timestamp.valueOf(activity.getEndTime().replace("T", " ") + ":00"));
		// System.out.println("***************"+entity);
		TTicket ticketEntity = new TTicket();
		ticketEntity.setTicketId(activity.getTicketId());
		ticketEntity.setTicketName(activity.getTicketName());
		ticketEntity.setTicketPrice(activity.getTicketPrice());
		ticketEntity.setTicketQuantity(activity.getTicketQuantity());
		ticketEntity.setTicketType(activity.getTicketType());

		if (!file.isEmpty()) {
			try {
				activityService.createByImage(entity, ticketEntity, file);
			} catch (IllegalStateException | IOException e) {

			}

		} else {

			activityService.create(entity, ticketEntity);

		}

		return new ModelAndView("redirect:/activity/browser");
	}

	// 免費活動
	@PreAuthorize("hasAnyAuthority('U','M','T','P')")
	@RequestMapping(value = "/createfree", method = RequestMethod.GET)
	public String createfree(Model model) {
		model.addAttribute("typeList", activityService.getAllTActivityType());
		return "activity/createfree";
	}

	@PreAuthorize("hasAnyAuthority('U','M','T','P')")
	@RequestMapping(value = "/doFree", method = RequestMethod.POST)
	public ModelAndView doFree(@ModelAttribute ActivityFormBean activity,
			@RequestParam("imageFile") MultipartFile file) {

		TActivity entity = new TActivity();
		entity.setActivityName(activity.getActivityName());

		entity.setActivityDate(Timestamp.valueOf(activity.getActivityDate().replace("T", " ") + ":00"));
		entity.setActivityPlace(activity.getActivityPlace());
		entity.setActiivityDescription(activity.getActiivityDescription());
		entity.setHostTel(activity.getHostTel());
		entity.setHostMail(activity.getHostMail());
		entity.setActivityType(activity.getActivityType());
		entity.setActivityStatus("0");
		entity.setEndTime(Timestamp.valueOf(activity.getEndTime().replace("T", " ") + ":00"));
		// System.out.println("***************"+entity);
		TTicket ticketEntity = new TTicket();
		ticketEntity.setTicketId(activity.getTicketId());
		ticketEntity.setTicketName(activity.getTicketName());
		ticketEntity.setTicketPrice(activity.getTicketPrice());
		ticketEntity.setTicketQuantity(activity.getTicketQuantity());
		ticketEntity.setTicketType(activity.getTicketType());

		if (!file.isEmpty()) {
			try {
				activityService.createByImage(entity, ticketEntity, file);
			} catch (IllegalStateException | IOException e) {

			}

		} else {

			activityService.create(entity, ticketEntity);

		}

		return new ModelAndView("redirect:/activity/browser");
	}

	@RequestMapping("/{id}/image/{fileName}")
	public void showPicture(@PathVariable("fileName") String fileName, @PathVariable("id") String id,
			HttpServletResponse response) {
		File imgFile = new File(imageFilePath + "/" + id + "/" + fileName);
		responseFile(response, imgFile);
	}

	private void responseFile(HttpServletResponse response, File imgFile) {
		try (InputStream is = new FileInputStream(imgFile); OutputStream os = response.getOutputStream();) {
			byte[] buffer = new byte[1024];
			while (is.read(buffer) != -1) {
				os.write(buffer);
			}
			os.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView listActivity(ModelAndView model) throws IOException {
		List<TActivity> entityList = activityService.selectPass();
		List<ActivityFormBean> beanList = new ArrayList<ActivityFormBean>();

		for (TActivity entity : entityList) {
			ActivityFormBean bean = new ActivityFormBean();
			bean.setActivityId(entity.getActivityId());
			bean.setActivityName(entity.getActivityName());

			// bean.setActivityDate(CommonUtils.timestampToString(entity.getActivityDate()));
			bean.setActivityDate(sdf.format(entity.getActivityDate()));
			bean.setActivityPlace(entity.getActivityPlace());
			bean.setActiivityDescription(entity.getActiivityDescription());
			bean.setActivityImage(entity.getActivityImage());
			bean.setUpdateTime(entity.getUpdateTime());
			bean.setEndTime(sdf.format(entity.getEndTime()));
			beanList.add(bean);
		}

		model.addObject("beanList", beanList);
		model.setViewName("activity/list");

		return model;
	}
	
	@RequestMapping(value = "/list/{activityType}", method = RequestMethod.GET)
	public ModelAndView listTypeActivity(ModelAndView model, @PathVariable("activityType") String activityType) throws IOException {
		List<TActivity> entityList = activityService.selectPassByType(activityType);
		List<ActivityFormBean> beanList = new ArrayList<ActivityFormBean>();

		for (TActivity entity : entityList) {
			ActivityFormBean bean = new ActivityFormBean();
			bean.setActivityId(entity.getActivityId());
			bean.setActivityName(entity.getActivityName());

			// bean.setActivityDate(CommonUtils.timestampToString(entity.getActivityDate()));
			bean.setActivityDate(sdf.format(entity.getActivityDate()));
			bean.setActivityPlace(entity.getActivityPlace());
			bean.setActiivityDescription(entity.getActiivityDescription());
			bean.setActivityImage(entity.getActivityImage());
			bean.setUpdateTime(entity.getUpdateTime());
			bean.setEndTime(sdf.format(entity.getEndTime()));
			beanList.add(bean);
		}

		model.addObject("beanList", beanList);
		model.setViewName("activity/list");

		return model;
	}


	@RequestMapping(value = "/{id}/detail", method = RequestMethod.GET)
	public String detail(Model model, @PathVariable("id") int id) {
		TActivity entity = activityService.getActivityById(id);
		// ActivityFormBean beanList = new ActivityFormBean();

		ActivityFormBean bean = new ActivityFormBean();
		bean.setActivityId(entity.getActivityId());
		bean.setActivityName(entity.getActivityName());
		// bean.setActivityDate(CommonUtils.timestampToString(entity.getActivityDate()));
		bean.setActivityDate(sdf.format(entity.getActivityDate()));
		bean.setActivityPlace(entity.getActivityPlace());
		bean.setActiivityDescription(entity.getActiivityDescription());
		bean.setActivityImage(entity.getActivityImage());
		bean.setActivityHost(entity.getActivityHost());
		bean.setHostTel(entity.getHostTel());
		bean.setHostMail(entity.getHostMail());
		bean.setCreateUser(entity.getCreateUser());
		bean.setActivityStatus(entity.getActivityStatus());
		bean.setActivityType(entity.getActivityType());
		// bean.setEndTime(CommonUtils.timestampToString(entity.getEndTime()));
		bean.setEndTime(sdf.format(entity.getEndTime()));
		model.addAttribute("bean", bean);
		return "activity/detail";
	}

	@PreAuthorize("hasAnyAuthority('U','M','T','P')")
	@RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
	public String toEdit(Model model, @PathVariable("id") int id) {

		TActivity entity = activityService.getActivityByIdAndCurrentUser(id);

		if (entity != null) {
			ActivityFormBean bean = new ActivityFormBean();
			bean.setActivityId(entity.getActivityId());
			bean.setActivityName(entity.getActivityName());

			bean.setActivityDate(sdf.format(entity.getActivityDate()));
			// bean.setEndTime(CommonUtils.timestampToString(entity.getEndTime()));
			bean.setEndTime(sdf.format(entity.getEndTime()));
			bean.setActivityPlace(entity.getActivityPlace());
			bean.setActiivityDescription(entity.getActiivityDescription());
			bean.setHostTel(entity.getHostTel());
			bean.setHostMail(entity.getHostMail());
			bean.setActivityImage(entity.getActivityImage());
			bean.setActivityStatus(entity.getActivityStatus());
			model.addAttribute("activity", bean);
			return "activity/edit";
		}

		return "redirect:/activity/browser";

	}

	@PreAuthorize("hasAnyAuthority('U','M','T','P')")
	@RequestMapping(value = "/doEdit", method = RequestMethod.POST)
	public String doEdit(ActivityFormBean activity) {

		TActivity entity = activityService.getActivityById(activity.getActivityId());
		entity.setActivityName(activity.getActivityName());
		// String test = activity.getActivityDate().replace("T", " ") + ":00";
		entity.setActivityDate(Timestamp.valueOf(activity.getActivityDate().replace("T", " ") + ":00"));
		entity.setEndTime(Timestamp.valueOf(activity.getEndTime().replace("T", " ") + ":00"));
		// entity.setEndTime(CommonUtils.stringToTimestamp(activity.getEndTime()));
		entity.setActivityPlace(activity.getActivityPlace());
		entity.setActiivityDescription(activity.getActiivityDescription());
		entity.setHostTel(activity.getHostTel());
		entity.setHostMail(activity.getHostMail());
		entity.setCreateUser(activity.getCreateUser());
		entity.setActivityType(activity.getActivityType());
		entity.setActivityImage(activity.getActivityImage());

		activityService.update(entity);
		return "redirect:/activity/browser";
	}

	@PreAuthorize("hasAnyAuthority('U','M','T','P')")
	@RequestMapping(value = "/browser", method = RequestMethod.GET)
	public String toBrowser(Model model) {

		// 未審核
		List<TActivity> checkEntityList = activityService.selectCheck();
		List<ActivityFormBean> checkList = new ArrayList<ActivityFormBean>();

		for (TActivity entity : checkEntityList) {
			ActivityFormBean bean = new ActivityFormBean();
			bean.setActivityId(entity.getActivityId());
			bean.setActivityName(entity.getActivityName());
			bean.setActivityDate(sdf.format(entity.getActivityDate()));
			bean.setEndTime(sdf.format(entity.getEndTime()));
			// bean.setEndTime(CommonUtils.timestampToString(entity.getEndTime()));
			bean.setActivityPlace(entity.getActivityPlace());
			bean.setActiivityDescription(entity.getActiivityDescription());
			bean.setCreateUser(entity.getCreateUser());
			checkList.add(bean);
		}

		model.addAttribute("checkList", checkList);

		// 審核中
		List<TActivity> waitEntityList = activityService.selectWaitCheck();
		List<ActivityFormBean> waitcheckList = new ArrayList<ActivityFormBean>();

		for (TActivity entity : waitEntityList) {
			ActivityFormBean bean = new ActivityFormBean();
			bean.setActivityId(entity.getActivityId());
			bean.setActivityName(entity.getActivityName());
			bean.setActivityDate(sdf.format(entity.getActivityDate()));
			bean.setEndTime(sdf.format(entity.getEndTime()));
			bean.setActivityPlace(entity.getActivityPlace());
			bean.setActiivityDescription(entity.getActiivityDescription());
			bean.setCreateUser(entity.getCreateUser());
			waitcheckList.add(bean);
		}

		model.addAttribute("waitcheckList", waitcheckList);

		// 審核通過
		List<TActivity> passEntityList = activityService.selectPass();
		List<ActivityFormBean> passList = new ArrayList<ActivityFormBean>();

		for (TActivity entity : passEntityList) {
			ActivityFormBean bean = new ActivityFormBean();
			bean.setActivityId(entity.getActivityId());
			bean.setActivityName(entity.getActivityName());
			bean.setActivityDate(sdf.format(entity.getActivityDate()));
			bean.setEndTime(sdf.format(entity.getEndTime()));
			bean.setActivityPlace(entity.getActivityPlace());
			bean.setActiivityDescription(entity.getActiivityDescription());
			bean.setCreateUser(entity.getCreateUser());
			passList.add(bean);
		}

		model.addAttribute("passList", passList);

		// 取消
		List<TActivity> cancelEntityList = activityService.selectCancel();
		List<ActivityFormBean> cancelList = new ArrayList<ActivityFormBean>();

		for (TActivity entity : cancelEntityList) {
			ActivityFormBean bean = new ActivityFormBean();
			bean.setActivityId(entity.getActivityId());
			bean.setActivityName(entity.getActivityName());
			bean.setActivityDate(sdf.format(entity.getActivityDate()));
			bean.setEndTime(sdf.format(entity.getEndTime()));
			bean.setActivityPlace(entity.getActivityPlace());
			bean.setActiivityDescription(entity.getActiivityDescription());
			bean.setCreateUser(entity.getCreateUser());
			cancelList.add(bean);
		}

		model.addAttribute("cancelList", cancelList);

		// 審核未通過
		List<TActivity> faultEntityList = activityService.faultCheck();
		List<ActivityFormBean> faultList = new ArrayList<ActivityFormBean>();

		for (TActivity entity : faultEntityList) {
			ActivityFormBean bean = new ActivityFormBean();
			bean.setActivityId(entity.getActivityId());
			bean.setActivityName(entity.getActivityName());
			bean.setActivityDate(sdf.format(entity.getActivityDate()));
			bean.setEndTime(sdf.format(entity.getEndTime()));
			bean.setActivityPlace(entity.getActivityPlace());
			bean.setActiivityDescription(entity.getActiivityDescription());
			bean.setCreateUser(entity.getCreateUser());
			faultList.add(bean);
		}

		model.addAttribute("faultList", faultList);

		return "activity/activityBrowser";
	}

	
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String search(Model model, @RequestParam("searchValue") String searchValue) {

		List<TActivity> entityList = activityService.selectSearch(searchValue);
		List<ActivityFormBean> beanList = new ArrayList<ActivityFormBean>();

		for (TActivity entity : entityList) {
			ActivityFormBean bean = new ActivityFormBean();
			bean.setActivityId(entity.getActivityId());
			bean.setActivityName(entity.getActivityName());
			bean.setActivityDate(sdf.format(entity.getActivityDate()));
			bean.setEndTime(sdf.format(entity.getEndTime()));
			bean.setActivityPlace(entity.getActivityPlace());
			bean.setActiivityDescription(entity.getActiivityDescription());
			bean.setCreateUser(entity.getCreateUser());
			beanList.add(bean);
		}

		model.addAttribute("beanList", beanList);

		return "activity/searchactivity";
	}

	@RequestMapping(value = "/{id}/sendExamine", method = RequestMethod.GET)
	public String sendExamine(Model model, @PathVariable("id") int id) {

		// 送審
		activityService.updateSendExamine(id);

		return "redirect:/activity/browser";
	}

	@RequestMapping(value = "/{id}/cancel", method = RequestMethod.GET)
	public String cancel(Model model, @PathVariable("id") int id) {

		// 取消
		activityService.updateCancel(id);

		return "redirect:/activity/browser";
	}

	@PreAuthorize("hasAnyAuthority('U','M','T','P')")
	@RequestMapping(value = "/{id}/orderUserList", method = RequestMethod.GET)
	public String addList(Model model, @PathVariable("id") int id, HttpSession session) {
		List<TOrder> orderUserList = orderService.getOrderUserList(id);
		List<TOrderDetail> orderDetailUserList = orderService.getOrderDetailUserList(id);
		List<OrderFormBean> userList = new ArrayList<OrderFormBean>();
		for (int i = 0; i < orderUserList.size(); i++) {
			OrderFormBean bean = new OrderFormBean();
			bean.setOrderId(orderUserList.get(i).getOrderId());
			bean.setUserId(orderUserList.get(i).getUserId());
			bean.setUserName(orderUserList.get(i).getUserName());
			bean.setMobile(orderUserList.get(i).getMobile());
			bean.setEmail(orderUserList.get(i).getEmail());
			bean.setTotalPrice(orderUserList.get(i).getTotalPrice());
			bean.setOrderStatus(orderUserList.get(i).getOrderStatus());
			for (int j = 0; j <= orderDetailUserList.size(); j++) {
				bean.setBuyTicket(orderDetailUserList.get(i).getBuyTicket());
				bean.setTicketPrice(orderDetailUserList.get(i).getTicketPrice());
			}
			userList.add(bean);
		}

		session.setAttribute("userList", userList);
		model.addAttribute("userList", userList);
		return "activity/userList";
	}
}

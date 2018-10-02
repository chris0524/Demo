package com.example.demo.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.entity.TActivity;
import com.example.demo.entity.TActivityType;
import com.example.demo.entity.TMessage;
import com.example.demo.entity.TUser;
import com.example.demo.formbean.ActivityFormBean;
import com.example.demo.formbean.ActivityTypeFormBean;
import com.example.demo.formbean.MessageFormBean;
import com.example.demo.formbean.UserFormBean;
import com.example.demo.service.ActivityService;
import com.example.demo.service.MessageService;
import com.example.demo.service.UserService;
import com.example.demo.utils.CommonUtils;

@Controller
@PreAuthorize("hasAuthority('M')")
@RequestMapping(value = "/admin", method = RequestMethod.GET)
public class AdminController {

	@Autowired
	private ActivityService activityService;

	@Autowired
	private MessageService messageService;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Model model) {

		return "redirect:/admin/all";
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public String all() {

		return "admin/admin";
	}

	DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

	// 審核活動頁面
	@RequestMapping(value = "/examine", method = RequestMethod.GET)
	public String examineactivity(Model model) {
		List<TActivity> entityList = activityService.selectWaitCheck();
		List<ActivityFormBean> beanList = new ArrayList<ActivityFormBean>();

		for (TActivity entity : entityList) {
			ActivityFormBean bean = new ActivityFormBean();
			bean.setActivityId(entity.getActivityId());
			bean.setActivityName(entity.getActivityName());
			// bean.setActivityDate(CommonUtils.timestampToString(entity.getActivityDate()));
			bean.setActivityDate(sdf.format(entity.getActivityDate()));
			bean.setEndTime(sdf.format(entity.getEndTime()));
			bean.setActivityPlace(entity.getActivityPlace());
			bean.setActiivityDescription(entity.getActiivityDescription());
			bean.setCreateUser(entity.getCreateUser());
			// bean.setActivityHost(entity.getActivityHost());
			beanList.add(bean);
		}

		model.addAttribute("beanList", beanList);
		return "admin/examineactivity";
	}

	@RequestMapping(value = "/activity/{id}/examinePass", method = RequestMethod.GET)
	public String examinePass(Model model, @PathVariable("id") int id) {

		// 活動審核通過
		activityService.updateExaminePass(id);

		return "redirect:/admin/examine";
	}

	@RequestMapping(value = "/activity/{id}/examineFail", method = RequestMethod.GET)
	public String examineFail(Model model, @PathVariable("id") int id) {

		// 活動審核不通過
		activityService.updateExamineFail(id);

		return "redirect:/admin/examine";
	}

	// 審核會員頁面
	@RequestMapping(value = "/cuser", method = RequestMethod.GET)
	public String examineuser(Model model) {
		List<TUser> entityList = userService.temp();
		List<UserFormBean> beanList = new ArrayList<UserFormBean>();

		for (TUser entity : entityList) {
			UserFormBean bean = new UserFormBean();
			bean.setUserId(entity.getUserId());
			bean.setEmail(entity.getEmail());
			bean.setCreateTime(CommonUtils.timestampToString(entity.getCreateTime()));
			bean.setUserName(entity.getUserName());
			bean.setStatus(entity.getStatus());
			bean.setUserAddress(entity.getUserAddress());
			bean.setUserIdentity(entity.getUserIdentity());
			bean.setMobile(entity.getMobile());
			bean.setRole(entity.getRole());
			beanList.add(bean);
		}

		model.addAttribute("beanList", beanList);
		return "admin/examineuser";
	}

	@RequestMapping(value = "/member/{id}/examineOK", method = RequestMethod.GET)
	public String examineOK(Model model, @PathVariable("id") String userId) {

		// 審核通過
		userService.updateExaminePass(userId);
		return "redirect:/admin/cuser";
	}

	@RequestMapping(value = "/member/{id}/examineFault", method = RequestMethod.GET)
	public String examineFault(Model model, @PathVariable("id") String userId) {

		// 審核不通過
		userService.updateExamineFail(userId);

		return "redirect:/admin/cuser";
	}

	@PreAuthorize("hasAnyAuthority('M')")
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String toAdd(Model model) {
		model.addAttribute(messageService);
		return "admin/postMessage";
	}

	@PreAuthorize("hasAnyAuthority('M')")
	@RequestMapping(value = "/addmessage", method = RequestMethod.POST)
	public String addmessage(MessageFormBean message, BindingResult bindingResult) {

		TMessage entity = new TMessage();

		entity.setMessageId(message.getMessageId());
		entity.setMessageName(message.getMessageName());
		entity.setMessage(message.getMessage());

		messageService.create(entity);

		return "redirect:/admin/all";
	}
	//新增活動類型
	@PreAuthorize("hasAnyAuthority('M')")
	@RequestMapping(value = "/createtype", method = RequestMethod.GET)
	public String createtype(Model model) {
		
		model.addAttribute("activitytypeList", activityService.getActivityType());
		return "admin/addactivitytype";
	}
	@PreAuthorize("hasAnyAuthority('M')")
	@RequestMapping(value = "/addtype", method = RequestMethod.POST)
	public ModelAndView addtype(@ModelAttribute ActivityTypeFormBean type , BindingResult bindingResult) {

		TActivityType entity = new TActivityType();

		entity.setTypeId(type.getTypeId());
		entity.setTypeName(type.getTypeName());

		activityService.createtype(entity);

		return new ModelAndView("redirect:/admin/createtype");
	}
	
}

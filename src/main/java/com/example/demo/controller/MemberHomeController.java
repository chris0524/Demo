package com.example.demo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.entity.TActivity;
import com.example.demo.entity.TMessage;
import com.example.demo.entity.TUser;
import com.example.demo.formbean.ActivityFormBean;
import com.example.demo.formbean.MessageFormBean;
import com.example.demo.formbean.UserFormBean;
import com.example.demo.service.ActivityService;
import com.example.demo.service.MessageService;
import com.example.demo.service.UserService;
import com.example.demo.utils.CommonUtils;
import com.example.demo.utils.CurrentUser;


@Controller
@RequestMapping(value="/member", method = RequestMethod.GET)
public class MemberHomeController {
    
	@Autowired
	private UserService userService;
	
	@Autowired
	private ActivityService activityService;
	
	@Autowired
	private CurrentUser currentuser;
	
	@Autowired
	private MessageService messageService;
	
    @RequestMapping(value="/", method = RequestMethod.GET)
    public String index(Model model) {
    		
    	return "redirect:/member/home";
    }
    
    @RequestMapping(value="/home", method = RequestMethod.GET)
    public String home(Model model) {
    		
    	return "home";
    }
    
	@PreAuthorize("hasAnyAuthority('U','M','T','P')")
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String toEdit(Model model) {

		TUser entity = userService.getUserById(currentuser.getUserId());
	
		if(entity != null) {
			UserFormBean bean = new UserFormBean();
			bean.setEmail(entity.getEmail());
			bean.setMobile(entity.getMobile());
			bean.setUserAddress(entity.getUserAddress());
			bean.setUserBirth(CommonUtils.timestampToStr(entity.getUserBirth()));
			bean.setUserIdentity(entity.getUserIdentity());
			bean.setUserName(entity.getUserName());
			bean.setUserPwd(entity.getUserPwd());
			bean.setUserSex(entity.getUserSex());
			bean.setUserId(entity.getUserId());
			bean.setUserPhoto(entity.getUserPhoto());
//			System.out.println("*******************toEidt"+bean.getUserId());
			
			model.addAttribute("user", bean);
			return "user/userEdit";
		}
		
		return "redirect:/";
	
	}
	
	@PreAuthorize("hasAnyAuthority('U','M','T','P')")
	@RequestMapping(value = "/doEdit", method = RequestMethod.POST)
	public String doEdit(UserFormBean user) {
		
		TUser entity = userService.getUserById(user.getUserId());
//		System.out.println(entity);
		entity.setEmail(user.getEmail());
		entity.setMobile(user.getMobile());
		entity.setUserAddress(user.getUserAddress());
		entity.setUserIdentity(user.getUserIdentity());
		entity.setUserIntroduction(user.getUserIntroduction());
		entity.setUserName(user.getUserName());
		entity.setUserPwd(user.getUserPwd());
		entity.setUserSex(user.getUserSex());
		entity.setUserId(user.getUserId());
		entity.setUserBirth(CommonUtils.strToTimestamp(user.getUserBirth()));
		entity.setUserPhoto(user.getUserPhoto());
		userService.update(entity);
		return "redirect:/";
	}
	
	
	@PreAuthorize("hasAnyAuthority('U','M','T','P')")
	@RequestMapping(value = "/verify", method = RequestMethod.GET)
	public String varify(Model model) {

		TUser entity = userService.getUserById(currentuser.getUserId());
	
		if(entity != null) {
			UserFormBean bean = new UserFormBean();
			bean.setUserName(entity.getUserName());
			bean.setUserId(entity.getUserId());
			bean.setUserPhoto(entity.getUserPhoto());
			bean.setCreateTime(CommonUtils.timestampToString(entity.getCreateTime()));
			bean.setUpdateTime(CommonUtils.timestampToString(entity.getUpdateTime()));
			bean.setRole(entity.getRole());
			model.addAttribute("user", bean);
			return "user/accountverify";
		}
		
		return "redirect:/member/home";
	
	}
	
	@RequestMapping(value = "/{id}/sendExamine", method = RequestMethod.GET)
	public String sendExamine(Model model, @PathVariable("id") String userId) {
		
		//送審
		userService.updateSendExamine(userId);

		return "redirect:/member/verify";
	}
	
	@PreAuthorize("hasAnyAuthority('U','M','T','P')")
	@RequestMapping(value = "/myhomepage", method = RequestMethod.GET)
	public String myhomepage(Model model) {

		TUser entity = userService.getUserById(currentuser.getUserId());
	
		if(entity != null) {
			UserFormBean bean = new UserFormBean();
			bean.setEmail(entity.getEmail());
			bean.setMobile(entity.getMobile());
			bean.setUserAddress(entity.getUserAddress());
			bean.setUserBirth(CommonUtils.timestampToString(entity.getUserBirth()));
			bean.setUserIdentity(entity.getUserIdentity());
			bean.setUserName(entity.getUserName());
			bean.setUserPwd(entity.getUserPwd());
			bean.setUserSex(entity.getUserSex());
			bean.setUserId(entity.getUserId());
			bean.setUserPhoto(entity.getUserPhoto());
//			System.out.println("*******************toEidt"+bean.getUserId());
			
			model.addAttribute("user", bean);
			return "user/myhomepage";
		}
		
		return "redirect:/member/myhomepage";
	
	}
	
	@PreAuthorize("hasAnyAuthority('U','M','T','P')")
	@RequestMapping(value = "/systemmessage", method = RequestMethod.GET)
	public ModelAndView listMessage(ModelAndView model) throws IOException {
		List<TMessage> entityList = messageService.selectAll();
		List<MessageFormBean> beanList = new ArrayList<MessageFormBean>();
	
		for(TMessage entity :entityList) {
			MessageFormBean bean = new MessageFormBean();
			bean.setMessageId(entity.getMessageId());
			bean.setMessageName(entity.getMessageName());
			bean.setMessage(entity.getMessage());
			bean.setSendTime(CommonUtils.timestampToString(entity.getSendTime()));

			beanList.add(bean);
		}

		model.addObject("beanList", beanList);
		model.setViewName("user/systemmessage");
		
		return model;
	}
	
	
	@PreAuthorize("hasAnyAuthority('U','M','T','P')")
	@RequestMapping(value = "/joinactivity", method = RequestMethod.GET)
	public String joinactivity(Model model, String userId) {

		List<TActivity> checkEntityList = activityService.selectCheck();
		List<ActivityFormBean> checkList = new ArrayList<ActivityFormBean>();
//		List<TOrder> order = userService.getIdByActivity(userId);
		
		for(TActivity entity :checkEntityList) {
			ActivityFormBean bean = new ActivityFormBean();
			bean.setActivityId(entity.getActivityId());
			bean.setActivityName(entity.getActivityName());
			bean.setActivityDate(CommonUtils.timestampToString(entity.getActivityDate()));
			bean.setActivityPlace(entity.getActivityPlace());
			bean.setActiivityDescription(entity.getActiivityDescription());
			bean.setCreateUser(entity.getCreateUser());
			bean.setActivityHost(entity.getActivityHost());
			checkList.add(bean);
		}

 		
 		
		model.addAttribute("checkList",checkList);
		return "user/joinactivity";

	
	}
}

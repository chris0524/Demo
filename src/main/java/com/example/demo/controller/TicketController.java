package com.example.demo.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.TActivity;
import com.example.demo.entity.TOrder;
import com.example.demo.entity.TTicket;
import com.example.demo.formbean.ActivityFormBean;
import com.example.demo.formbean.OrderFormBean;
import com.example.demo.formbean.TicketFormBean;
import com.example.demo.repository.TicketDetailRepository;
import com.example.demo.service.ActivityService;
import com.example.demo.service.OrderService;
import com.example.demo.service.TicketService;

@Controller
@RequestMapping(value = "/ticket", method = RequestMethod.GET)
public class TicketController {

	@Autowired
	private TicketService ticketService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private ActivityService activityService;

	@Autowired
	TicketDetailRepository ticketDetailRepository;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Model model) {

		return "redirect:/ticket";
	}

	@PreAuthorize("hasAnyAuthority('U','M','T','P')")
	@RequestMapping(value = "/setticket", method = RequestMethod.GET)
	public String toAdd(Model model, @RequestParam("activityId") int id) {
		model.addAttribute("activityId", id);
		return "ticket/setticket";
	}

	DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	
	@PreAuthorize("hasAnyAuthority('U','M','T','P')")
	@RequestMapping(value = "/doAdd", method = RequestMethod.POST)
	public String doAdd(TicketFormBean ticket) {

		TTicket entity = new TTicket();
		entity.setTicketId(ticket.getTicketId());
		entity.setActivityName(ticket.getActivityName());
		entity.setActivityId(ticket.getActivityId());
		entity.setTicketName(ticket.getTicketName());
		entity.setTicketPrice(ticket.getTicketPrice());
		entity.setTicketQuantity(ticket.getTicketQuantity());
		entity.setTicketRemain(ticket.getTicketRemain());
		entity.setTicketSold(ticket.getTicketSold());
		entity.setTicketType(ticket.getTicketType());

		ticketService.create(entity);

		return "redirect:/activity/" + ticket.getActivityId() + "/preview";
	}

	@PreAuthorize("hasAnyAuthority('U','M','T','P')")
	@RequestMapping(value = "/tickethomepage", method = RequestMethod.GET)
	public String tickethomepage(Model model) {
	
//		List list = ticketDetailRepository.getTicketByOrder(0);
//		System.out.println("***tickethomepage: " + list);
		// 未付款
		List<TOrder> waitpayEntityList = orderService.waitpayCheck();
		List<OrderFormBean> waitpayList = new ArrayList<OrderFormBean>();

		for (TOrder entity : waitpayEntityList) {
			OrderFormBean bean = new OrderFormBean();
			bean.setOrderId(entity.getOrderId());
			bean.setActivityName(entity.getActivityName());
			bean.setUserId(entity.getUserId());
			bean.setActivityId(entity.getActivityId());
			
			bean.setTicketList(ticketService.getTicketByOrder(entity.getOrderId()));
			

			waitpayList.add(bean);
		}

		model.addAttribute("waitpayList", waitpayList);

		// 已付款
		List<TOrder> payEntityList = orderService.pay();
		List<OrderFormBean> payList = new ArrayList<OrderFormBean>();
//		List<TActivity> activity =activityService.selectWaitCheck();

		for (TOrder entity : payEntityList) {
			OrderFormBean bean = new OrderFormBean();
			bean.setOrderId(entity.getOrderId());
			bean.setActivityName(entity.getActivityName());
			bean.setActivityId(entity.getActivityId());
			bean.setUserId(entity.getUserId());
			
			
			bean.setTicketList(ticketService.getTicketByOrder(entity.getOrderId()));
			payList.add(bean);
			
				
		}
//		System.out.println("***tickethomepage: " + payList.size());
		model.addAttribute("payList", payList);

		// 取消
		List<TOrder> cancelEntityList = orderService.Cancel();
		List<OrderFormBean> cancelList = new ArrayList<OrderFormBean>();

		for (TOrder entity : cancelEntityList) {
			OrderFormBean bean = new OrderFormBean();
			bean.setOrderId(entity.getOrderId());
			bean.setActivityName(entity.getActivityName());
			bean.setActivityId(entity.getActivityId());
			bean.setUserId(entity.getUserId());
			
			bean.setTicketList(ticketService.getTicketByOrder(entity.getOrderId()));
			cancelList.add(bean);
		}

		model.addAttribute("cancelList", cancelList);
	
	//參加的活動
		List<Integer> activityIds = orderService.getMyOrderList();
		List<ActivityFormBean> myOrderList = new ArrayList<ActivityFormBean>();
		for (int id : activityIds) {
			TActivity entity = activityService.getActivityById(id);
			ActivityFormBean bean = new ActivityFormBean();
			bean.setActivityId(entity.getActivityId());
			bean.setActivityName(entity.getActivityName());
			bean.setActivityDate(sdf.format(entity.getActivityDate()));
			bean.setEndTime(sdf.format(entity.getEndTime()));
			bean.setActivityPlace(entity.getActivityPlace());
			bean.setActiivityDescription(entity.getActiivityDescription());
			bean.setCreateUser(entity.getCreateUser());
			myOrderList.add(bean);
		}
		model.addAttribute("myOrderList", myOrderList);
		
		
		
		return "ticket/tickethomepage";
		

	}
	
	
	
	
}

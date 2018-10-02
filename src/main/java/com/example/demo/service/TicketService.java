package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.bean.TicketDetail;
import com.example.demo.entity.TTicket;
import com.example.demo.repository.TTicketRepository;
import com.example.demo.repository.TicketDetailRepository;

@Service
public class TicketService {
	@Autowired
	private TTicketRepository ticketRepository;
	
	@Autowired
	private TicketDetailRepository ticketDetailRepository;
	
	public List<TicketDetail> getTicketByOrder(int orderId) {
		return ticketDetailRepository.getTicketByOrder(orderId);
	}
	public List<TTicket> getByActivityId(int id) {
		return ticketRepository.getByActivityId(id);
	}
	
	public Iterable<TTicket> findAll() {
		return ticketRepository.findAll();
	}
	
	public TTicket getTicketById(int id) {
		return ticketRepository.getById(id);
	}
	
	public List<TTicket> selectAll() {
		return ticketRepository.getAllTicket();
	}
	public TTicket create(TTicket ticket) {
		ticket.setTicketSold(0);
		return ticketRepository.save(ticket);
	}
	
	public void update(TTicket ticket) {
		TTicket db = ticketRepository.getById(ticket.getTicketId());
	
		db.setTicketName(ticket.getActivityName());
		db.setActivityId(ticket.getActivityId());
		db.setActivityName(ticket.getActivityName());
		db.setTicketId(ticket.getTicketId());
		db.setTicketName(ticket.getTicketName());
		db.setTicketPrice(ticket.getTicketPrice());
		db.setTicketQuantity(ticket.getTicketQuantity());
		db.setTicketRemain(ticket.getTicketRemain());
		db.setTicketSold(ticket.getTicketSold());
		db.setTicketType(ticket.getTicketType());
	
		ticketRepository.save(db);
	}
}
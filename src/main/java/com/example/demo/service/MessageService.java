package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.TMessage;
import com.example.demo.repository.TMessageRepository;

@Service
public class MessageService {

	@Autowired
	private TMessageRepository messageRepository;

	public TMessage getById(int id) {
		return messageRepository.getById(id);
	}

	public List<TMessage> selectAll() {
		return messageRepository.getAll();
	}

	// public TMessage create(TMessage message) {
	// TMessage db = messageRepository.getById(message.getMessageId());
	//
	// db.setMessageId(message.getMessageId());
	// db.setMessageName(message.getMessageName());
	// db.setMessage(message.getMessage());
	//
	// return messageRepository.save(message);
	// }

	public TMessage create(TMessage message) {
		message.setMessageId(message.getMessageId());
		message.setMessageName(message.getMessageName());
		message.setMessage(message.getMessage());
		message.setSendTime(message.getSendTime());
		TMessage rs = messageRepository.save(message);
		if (rs != null) {
			messageRepository.save(message);
		}
		return rs;
	}

}

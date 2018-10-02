package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.TMessage;

@Repository
public interface TMessageRepository extends CrudRepository<TMessage, Integer> {
	@Query("select a from TMessage a where a.messageId=:messageId")
	public TMessage getById(@Param("messageId") int messageId);

	@Query("select a from TMessage a order by messageId desc")
	public List<TMessage> getAll();
}

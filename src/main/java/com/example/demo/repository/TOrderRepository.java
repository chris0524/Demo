package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.TOrder;


@Repository
public interface TOrderRepository extends CrudRepository<TOrder, Integer>{
	@Query("select a from TOrder a where a.orderId=:orderId")
	public TOrder getById(@Param("orderId") int orderId);
	
	@Query("select a from TOrder a")
	public List<TOrder> getAllOrder();
	
	@Query(value = "select NEXT VALUE FOR ticketno_sequence", nativeQuery = true)
	public Long getTicketnoSequence();
	
	@Query("select a from TOrder a where a.userId=:userId")
	public List<TOrder> getByUserId(@Param("userId")String userId);
	
	@Query("select a from TOrder a where a.userId=:userId and a.orderStatus=:orderStatus")
	public List<TOrder> getByStatus(@Param("userId")String userId,@Param("orderStatus")int orderStatus);
	
	@Query(value = "SELECT DISTINCT activity_id ,user_id from t_order where user_id=user_id", nativeQuery = true)
	public List<TOrder> getIdByActivity(@Param("userId")String userId);
	
	@Query("select DISTINCT  activityId from TOrder a where a.userId=:userId")
	public List<Integer> getMYOrderDetail(@Param("userId") String userId);
	
	@Query("select a from TOrder a where a.activityId=:activityId")
	public List<TOrder> getAddOrder(@Param("activityId") int activityId);
}

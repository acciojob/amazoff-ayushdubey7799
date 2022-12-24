package com.driver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    public void addOrder(Order order){
         orderRepository.addOrder(order);
    }


    public void addPartner(String partnerId){
        orderRepository.addDeliveryPartner(partnerId);
    }


    public void addOrderPartnerPair(String orderId,String partnerId){
        orderRepository.addOrderPartnerPair(orderId,partnerId);
    }


    public Order getOrderById(String orderId){
        return orderRepository.getOrderById(orderId);
    }


    public DeliveryPartner getPartnerById(String partnerId){
        return orderRepository.getPartnerById(partnerId);
    }


    public int getOrderCountByPartnerId(String partnerId){
        return orderRepository.numberOfOrders(partnerId);
    }


    public List<String> getOrdersByPartnerId(String partnerId){
        return orderRepository.getAllOrdersByPartnerId(partnerId);
    }


    public List<String> getAllOrders(){
      return orderRepository.getAllOrders();
    }


    public int getCountOfUnassignedOrders() {
        return orderRepository.getUnassignedOrdersCount();
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(String time,String partnerId){
        return orderRepository.ordersLeft(time,partnerId);
    }


    public String getLastDeliveryTimeByPartnerId(String partnerId){
       return orderRepository.lastDeliveryTime(partnerId);
    }

    public void deletePartnerById(String partnerId){
       orderRepository.deletePartner(partnerId);
    }


    public void deleteOrderById(String orderId){
      orderRepository.deleteOrder(orderId);
    }
}



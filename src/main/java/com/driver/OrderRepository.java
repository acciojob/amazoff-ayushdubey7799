package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class OrderRepository {

    HashMap<String,Order> orderRepository = new HashMap<>();
    HashMap<String,DeliveryPartner> deliveryPartnerRepository = new HashMap<>();

    HashMap<DeliveryPartner, List<Order>> OrderPartnerMapping = new HashMap<>();

    HashMap<String,Boolean> orderStatus  = new HashMap<>();

    public void addOrder(Order order){
        orderStatus.put(order.getId(),false);
        orderRepository.put(order.getId(),order);
    }

    public void addDeliveryPartner(String partnerId){
        DeliveryPartner deliveryPartner = new DeliveryPartner(partnerId);
        deliveryPartnerRepository.put(deliveryPartner.getId(), deliveryPartner);
    }

    public void addOrderPartnerPair(String orderId,String partnerId){
        orderStatus.put(orderId,true);
        Order order = orderRepository.get(orderId);
        DeliveryPartner deliveryPartner = deliveryPartnerRepository.get(partnerId);
        int currentNumberOfOrders = deliveryPartner.getNumberOfOrders();
        currentNumberOfOrders++;
        deliveryPartner.setNumberOfOrders(currentNumberOfOrders);
        if(OrderPartnerMapping.containsKey(deliveryPartner)){
            List<Order> listOfOrders = OrderPartnerMapping.get(deliveryPartner);
            listOfOrders.add(order);
            OrderPartnerMapping.put(deliveryPartner,listOfOrders);
        }
        else{
            List<Order> firstOrder = new ArrayList<>();
            firstOrder.add(order);
            OrderPartnerMapping.put(deliveryPartner,firstOrder);
        }
    }

    public Order getOrderById(String orderId){
        return orderRepository.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId){
        return deliveryPartnerRepository.get(partnerId);
    }

    public int numberOfOrders(String partnerId){
        return deliveryPartnerRepository.get(partnerId).getNumberOfOrders();
    }

    public List<Order> getAllOrdersByPartnerId(String partnerId){
        return OrderPartnerMapping.get(deliveryPartnerRepository.get(partnerId));
    }

    public List<Order> getAllOrders(){
        List<Order> allOrders = new ArrayList<>();
        for(Order order: orderRepository.values()){
            allOrders.add(order);
        }
        return allOrders;
    }

    public int getUnassignedOrdersCount(){
       int unassignedOrders = 0;
       for(String orderId : orderStatus.keySet()){
           if(orderStatus.get(orderId)==false){
               unassignedOrders++;
           }
       }
       return unassignedOrders;
    }

    public int ordersLeft(String time,String partnerId){
        int HH = Integer.parseInt(time.substring(0,2));
        int MM = Integer.parseInt(time.substring(3,5));
        int undeliveredOrderCount = 0;
        int t = HH*60+MM;
        List<Order> orders = OrderPartnerMapping.get(deliveryPartnerRepository.get(partnerId));
        for(Order order : orders){
            if(order.getDeliveryTime()>t){
                undeliveredOrderCount++;
            }
        }
        return undeliveredOrderCount;
    }

    public String lastDeliveryTime(String partnerId){
        int time = 0;
        List<Order> orders = OrderPartnerMapping.get(partnerId);
        for(Order order : orders){
            time = Math.max(time,order.getDeliveryTime());
        }
        String t = Integer.toString(time);
        String lastDeliveryTime = "";
        lastDeliveryTime+=t.substring(0,2);
        lastDeliveryTime+=":";
        lastDeliveryTime+=t.substring(2,4);
        return lastDeliveryTime;
    }

    public void deletePartner(String partnerId){
        DeliveryPartner partner = deliveryPartnerRepository.get(partnerId);
        List<Order> orderList = OrderPartnerMapping.get(partnerId);
        for(Order order : orderList){
            orderStatus.put(order.getId(),false);
        }
        OrderPartnerMapping.remove(partner);
        deliveryPartnerRepository.remove(partnerId);
    }

    public void deleteOrder(String orderId){
        Order orderToBeDeleted = orderRepository.get(orderId);
        orderRepository.remove((orderId));
        for(List<Order> listOfOrders : OrderPartnerMapping.values()){
            for(Order order : listOfOrders){
                if(orderId == order.getId()){
                    orderStatus.remove(orderId);
                    listOfOrders.remove(order);
                }
            }
        }
    }


}

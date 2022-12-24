package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class OrderRepository {

    private HashMap<String,Order> orderRepository;
    private HashMap<String,DeliveryPartner> deliveryPartnerRepository;

    private HashMap<String, List<String>> OrderPartnerMapping;
    private HashMap<String,Boolean> orderStatus;
    public OrderRepository(){
        orderRepository = new HashMap<>();
        deliveryPartnerRepository = new HashMap<>();
        OrderPartnerMapping = new HashMap<>();
        orderStatus  = new HashMap<>();
    }
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
        int updatedNumberOfOrders = currentNumberOfOrders+1;

        if(OrderPartnerMapping.containsKey(partnerId)){
            List<String> listOfOrders = OrderPartnerMapping.get(partnerId);
            listOfOrders.add(orderId);
            OrderPartnerMapping.put(partnerId,listOfOrders);
        }
        else{
            List<String> firstOrder = new ArrayList<>();
            firstOrder.add(orderId);
            OrderPartnerMapping.put(partnerId,firstOrder);
        }
        deliveryPartner.setNumberOfOrders(updatedNumberOfOrders);
    }

    public Order getOrderById(String orderId){
        return orderRepository.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId){
        return deliveryPartnerRepository.get(partnerId);
    }

    public int numberOfOrders(String partnerId){
        if(!deliveryPartnerRepository.containsKey(partnerId))return 0;
        return deliveryPartnerRepository.get(partnerId).getNumberOfOrders();
    }

    public List<String> getAllOrdersByPartnerId(String partnerId){
//        List<String> allOrders = new ArrayList<>();
//        for(Order order: OrderPartnerMapping.get(partnerId)){
//            allOrders.add(order.getId());
//        }
        return OrderPartnerMapping.get(partnerId);
    }

    public List<String> getAllOrders(){
        List<String> allOrders = new ArrayList<>();
        for(Order order: orderRepository.values()){
            allOrders.add(order.getId());
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
        List<String> orders = OrderPartnerMapping.get(partnerId);
        for(String orderId : orders){
            if(orderRepository.get(orderId).getDeliveryTime()>t){
                undeliveredOrderCount++;
            }
        }
        return undeliveredOrderCount;
    }

    public String lastDeliveryTime(String partnerId){
        int time = 0;
        List<String> orders = OrderPartnerMapping.get(partnerId);
        for(String orderId : orders){
            time = Math.max(time,orderRepository.get(orderId).getDeliveryTime());
        }
        StringBuilder sB = new StringBuilder();

        int hours = time/60;
        int minutes = time%60;
        String HH = Integer.toString(hours);
        String MM = Integer.toString(hours);
        if(HH.length()==1)sB.append(0);
        sB.append(hours);
        sB.append(":");
        sB.append(minutes);
        String lastDeliveryTime = sB.toString();
        return lastDeliveryTime;
    }

    public void deletePartner(String partnerId){
        if(deliveryPartnerRepository.containsKey(partnerId)) {
            DeliveryPartner partner = deliveryPartnerRepository.get(partnerId);
            List<String> orderList = OrderPartnerMapping.get(partnerId);
            for (String orderId : orderList) {
                orderStatus.put(orderRepository.get(orderId).getId(), false);
            }
            OrderPartnerMapping.remove(partnerId);
            deliveryPartnerRepository.remove(partnerId);
        }
    }

    public void deleteOrder(String orderId){
        if(deliveryPartnerRepository.containsKey(orderId)) {
            Order orderToBeDeleted = orderRepository.get(orderId);
            orderRepository.remove((orderId));
            for (List<String> listOfOrders : OrderPartnerMapping.values()) {
                for (String order : listOfOrders) {
                    if (orderId == orderRepository.get(order).getId()) {
                        orderStatus.remove(orderId);
                        listOfOrders.remove(order);
                    }
                }
            }
        }
    }

}

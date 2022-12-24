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
        deliveryPartnerRepository.put(partnerId, deliveryPartner);
    }

    public void addOrderPartnerPair(String orderId,String partnerId){
        orderStatus.put(orderId,true);
        List<String> listOfOrderIds = new ArrayList<>();
        if(orderRepository.containsKey(orderId)&&deliveryPartnerRepository.containsKey(partnerId)){
            if(OrderPartnerMapping.containsKey(partnerId)){
                listOfOrderIds = OrderPartnerMapping.get(partnerId);
            }
            listOfOrderIds.add(orderId);
            deliveryPartnerRepository.get(partnerId).setNumberOfOrders(listOfOrderIds.size());
        }
        OrderPartnerMapping.put(partnerId,listOfOrderIds);
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
        List<String> allOrders = new ArrayList<>();
        if(OrderPartnerMapping.containsKey(partnerId)){
            allOrders = OrderPartnerMapping.get(partnerId);
        }
        return allOrders;
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

        for(String orderId : OrderPartnerMapping.get(partnerId)){
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
        List<String> orders = new ArrayList<>();
        for(String partnerId:OrderPartnerMapping.keySet()){
            for(String order : OrderPartnerMapping.get(partnerId)){
                if(order.equals(orderId)){
                    orders = OrderPartnerMapping.get(partnerId);
                    orders.remove(orderId);
                    OrderPartnerMapping.put(partnerId,orders);
                }
            }
        }
        orderRepository.remove(orderId);
        orderStatus.remove(orderId);
    }

}

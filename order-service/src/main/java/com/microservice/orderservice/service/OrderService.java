package com.microservice.orderservice.service;
import com.microservice.orderservice.dto.InventoryResponse;
import com.microservice.orderservice.dto.OrderLineItemsDto;
import com.microservice.orderservice.dto.OrderRequest;
import com.microservice.orderservice.dto.OrderResponse;
import com.microservice.orderservice.model.Order;
import com.microservice.orderservice.model.OrderLineItems;
import com.microservice.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private  WebClient.Builder webClientBuilder;


    public void placeOrder(OrderRequest orderRequest) {

        Order order = new Order();

        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        var skuCodes = order.getOrderLineItemsList().stream().map(orderLineItems1 -> orderLineItems1.getSkuCode()).toList();

        WebClient webClient = WebClient.create("http://localhost:8082");

        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
        var result = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);

        if(result) {
            orderRepository.save(order);
        }
        else {
            throw new IllegalArgumentException("Entered product not in the stock....");
        }
    }

   /* public void placeOrder(OrderRequest orderRequest) {

        Order order = new Order();

        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        var skuCodes = order.getOrderLineItemsList().stream().map(orderLineItems1 -> orderLineItems1.getSkuCode()).toList();

        WebClient webClient = WebClient.create("http://localhost:8082");

        InventoryResponse[] inventoryResponseArray = webClient.get()
                        .uri("/api/inventory",uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                                .retrieve()
                                        .bodyToMono(InventoryResponse[].class)
                                                .block();
        var result = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);

        if(result) {
            orderRepository.save(order);
        }
        else {
            throw new IllegalArgumentException("Entered product not in the stock....");
        }
    }*/
    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }

    public List<OrderResponse> getOrders() {
        return orderRepository.findAll()
                .stream().map(this::mapToResponse).toList();
    }

    public OrderResponse mapToResponse(Order order){
        return OrderResponse.builder()
                .orderLineItemsDto(order.getOrderLineItemsList().stream().map(this::mapToOrderLineItemsDto).toList())
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .build();
    }

    private OrderLineItemsDto mapToOrderLineItemsDto(OrderLineItems orderLineItems) {
       return OrderLineItemsDto.builder().
                id(orderLineItems.getId()).
                price(orderLineItems.getPrice()).
                skuCode(orderLineItems.getSkuCode())
                .quantity(orderLineItems.getQuantity()).
                build();
    }
}

package com.example.graphql.service;

import com.example.graphql.datasource.dto.OrderDto;
import com.example.graphql.datasource.dto.OrderDetailDto;
import com.example.graphql.datasource.dto.UserDto;
import com.example.graphql.datasource.repository.ModelRepository;
import com.example.graphql.datasource.repository.OrderDetailsRepository;
import com.example.graphql.datasource.repository.OrderRepository;
import com.example.graphql.datasource.repository.UserRepository;
import com.example.graphql.exception.NotEnoughDetailsException;
import com.example.graphql.types.Order;
import com.example.graphql.types.OrderCreateInput;
import com.example.graphql.utils.GraphqlBeanMapper;
import com.netflix.graphql.dgs.exceptions.DgsEntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.UUID;

import static com.example.graphql.types.OrderDetailStatus.FULLY_TAKEN;
import static com.example.graphql.types.OrderDetailStatus.PARTIALLY_TAKEN;
import static com.example.graphql.utils.GraphqlBeanMapper.calculateOrderProgress;
import static com.example.graphql.utils.GraphqlBeanMapper.mapToEntity;

@Service
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderDetailsRepository orderDetailsRepository;
  private final UserRepository userRepository;
  private final ModelRepository modelRepository;

  private Sinks.Many<OrderDto> orderSink = Sinks.many().multicast().onBackpressureBuffer();

  public OrderService(OrderRepository orderRepository, OrderDetailsRepository orderDetailsRepository, UserRepository userRepository, ModelRepository modelRepository) {
    this.orderRepository = orderRepository;
    this.orderDetailsRepository = orderDetailsRepository;
    this.userRepository = userRepository;
    this.modelRepository = modelRepository;
  }

  @Transactional
  public OrderDto createOrder(OrderCreateInput input) {
    var existingUser = userRepository.findById(UUID.fromString(input.getUserId()));
    if (existingUser.isEmpty()) {
      throw new DgsEntityNotFoundException(
              String.format("Customer UUID %s not found", input.getUserId()));
    }

    var order = mapToEntity(input, existingUser.get());
    var created = orderRepository.save(order);
    var orderDetails = input.getDetails().stream()
            .map(detail -> createOrderDetails(created, detail.getId(), detail.getAmount()))
            .toList();

    created.setOrdersDetails(orderDetails);

    return created;
  }

  public OrderDto findOrder(UUID orderId) {
    return orderRepository.findById(orderId).orElseThrow(DgsEntityNotFoundException::new);
  }

  public OrderDetailDto createOrderDetails(OrderDto order, String detailId, Integer amount) {
    var model = modelRepository.findById(UUID.fromString(detailId));
    if (model.isEmpty()) {
      throw new DgsEntityNotFoundException("No model with id " + detailId);
    }
    var orderDetail = mapToEntity(order, detailId, amount);
    return orderDetailsRepository.save(orderDetail);
  }

  public List<OrderDetailDto> getAllAvailableOrderDetails() {
    return orderDetailsRepository.findAllByAvailableNot(0);
  }

  public void changeOrderDetails(UUID orderId, UUID modelId, int takenAmount) {
    var orderDetail = orderDetailsRepository.findByOrderIdAndModelId(orderId, modelId)
            .orElseThrow(DgsEntityNotFoundException::new);

    if (orderDetail.getStatus().equals(FULLY_TAKEN) || orderDetail.getAvailable() < takenAmount) {
      throw new NotEnoughDetailsException();
    }

    orderDetail.setAvailable(orderDetail.getAvailable() - takenAmount);
    orderDetail.setStatus(orderDetail.getAvailable() > takenAmount ? PARTIALLY_TAKEN : FULLY_TAKEN);
    orderDetail.setProgress(calculateOrderProgress(orderDetail.getAmount(), orderDetail.getAvailable()));
    orderDetailsRepository.save(orderDetail);
  }

  public List<Order> findOrders(UserDto user) {
    var orders = orderRepository.findAllByUser(user);
    return orders.stream().map(GraphqlBeanMapper::mapToGraphql).toList();
  }

  public Flux<OrderDto> orderFlux() {
    return orderSink.asFlux();
  }

  public void sendToSubscription(OrderDto saved) {
    orderSink.tryEmitNext(saved);
  }
}

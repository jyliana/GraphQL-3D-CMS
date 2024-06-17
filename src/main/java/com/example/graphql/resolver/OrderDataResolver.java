package com.example.graphql.resolver;

import com.example.graphql.DgsConstants;
import com.example.graphql.service.OrderService;
import com.example.graphql.service.UserService;
import com.example.graphql.types.Order;
import com.example.graphql.types.OrderCreateInput;
import com.example.graphql.types.OrderDetail;
import com.example.graphql.utils.GraphqlBeanMapper;
import com.netflix.graphql.dgs.*;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.List;

import static com.example.graphql.utils.GraphqlBeanMapper.mapToGraphql;

@DgsComponent
@AllArgsConstructor
public class OrderDataResolver {

  private final OrderService orderService;
  private final UserService userService;

  @DgsMutation(field = DgsConstants.MUTATION.OrderCreate)
  public Order createOrder(@InputArgument(name = "order") OrderCreateInput input) {
    var saved = orderService.createOrder(input);
    orderService.sendToSubscription(saved);

    return mapToGraphql(saved);
  }

  @DgsQuery(field = DgsConstants.QUERY.AvailableOrderDetails)
  public List<OrderDetail> getAllAvailableOrderDetails() {
    return orderService.getAllAvailableOrderDetails().stream().map(GraphqlBeanMapper::mapToGraphql).toList();
  }

  @DgsQuery(field = DgsConstants.QUERY.OrdersByUser)
  public List<Order> getOrdersByUser(@InputArgument String email) {
    var user = userService.findUserByEmail(email);
    return orderService.findOrders(user);
  }

  @DgsSubscription(field = DgsConstants.SUBSCRIPTION.OrderCreated)
  public Flux<Order> subscribeOrderCreated() {
    return orderService.orderFlux().map(GraphqlBeanMapper::mapToGraphql);
  }

}

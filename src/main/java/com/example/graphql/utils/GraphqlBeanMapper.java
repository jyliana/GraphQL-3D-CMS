package com.example.graphql.utils;

import com.example.graphql.datasource.dto.*;
import com.example.graphql.types.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.ZoneOffset;
import java.util.Collections;
import java.util.UUID;

@Mapper(config = DataMappingConfig.class)
public abstract class GraphqlBeanMapper {

  protected static final ZoneOffset ZONE_OFFSET = ZoneOffset.ofHours(2);

  @Mapping(target = "expiryTime", expression = "java(dto.getExpiryTimestamp().atOffset(ZONE_OFFSET))")
  public abstract UserAuthToken mapToGraphql(UserTokenDto dto);

  @Mapping(target = "createDateTime", expression = "java(dto.getCreationTimestamp().atOffset(ZONE_OFFSET))")
  @Mapping(target = "orders", expression = """
          java(dto.getOrders() == null ?
          java.util.Collections.emptyList()
          : dto.getOrders().stream().map(GraphqlBeanMapper::mapToGraphql).toList())""")
  public abstract User mapToGraphql(UserDto dto);

  public abstract Model mapToGraphql(ModelDto dto);

  public static OrderDetail mapToGraphql(OrderDetailDto dto) {
    return OrderDetail.newBuilder()
            .id(String.valueOf(dto.getId()))
            .orderId(String.valueOf(dto.getOrder().getId()))
            .modelId(String.valueOf(dto.getModelId()))
            .totalAmount(dto.getAmount())
            .availableAmount(dto.getAvailable())
            .status(dto.getStatus())
            .progress(calculateOrderProgress(dto.getAmount(), dto.getAvailable()))
            .build();
  }

  public static Order mapToGraphql(OrderDto dto) {
    return Order.newBuilder()
            .id(String.valueOf(dto.getId()))
            .userId(String.valueOf(dto.getUser().getId()))
            .tradeDateTime(dto.getTradeDate().atOffset(ZONE_OFFSET))
            .dueDateTime(dto.getDueDate().atOffset(ZONE_OFFSET))
            .deliveryAddress(dto.getDeliveryAddress())
            .status(dto.getStatus())
            .orderDetails(dto.getOrdersDetails() == null ?
                    Collections.emptyList() :
                    dto.getOrdersDetails().stream().map(GraphqlBeanMapper::mapToGraphql).toList())
            .build();
  }

  public static Execution mapToGraphql(ExecutionDto dto) {
    return Execution.newBuilder()
            .id(String.valueOf(dto.getId()))
            .workerId(String.valueOf(dto.getWorkerId()))
            .orderId(String.valueOf(dto.getOrderId()))
            .modelId(String.valueOf(dto.getModelId()))
            .startDateTime(dto.getStartDate().atOffset(ZONE_OFFSET))
            .status(dto.getStatus())
            .total(dto.getTotal())
            .completed(dto.getCompleted())
            .progress(dto.getProgress())
            .build();
  }

  @Mapping(target = "hashedPassword", expression = "java(com.example.graphql.utils.HashUtil.hashBcrypt(input.getPassword()))")
  public abstract UserDto mapToEntity(UserCreateInput input);

  public static OrderDetailDto mapToEntity(OrderDto order, String modelId, Integer amount) {
    return OrderDetailDto.builder()
            .order(order)
            .modelId(UUID.fromString(modelId))
            .amount(amount)
            .available(amount)
            .status(OrderDetailStatus.NOT_TAKEN)
            .progress(0)
            .build();
  }

  public static OrderDto mapToEntity(OrderCreateInput input, UserDto user) {
    return OrderDto.builder()
            .user(user)
            .deliveryAddress(input.getDeliveryAddress())
            .dueDate(input.getDueDate().toLocalDateTime())
            .status(OrderStatus.CREATED)
            .build();
  }

  public abstract ModelDto mapToEntity(ModelCreateInput input);

  public static ExecutionDto mapToEntity(UUID id, String orderId, String modelId, Integer totalAmount, UUID userId) {
    return ExecutionDto.builder()
            .id(id)
            .workerId(userId)
            .orderId(UUID.fromString(orderId))
            .modelId(UUID.fromString(modelId))
            .total(totalAmount)
            .status(ExecutionStatus.NOT_STARTED)
            .completed(0)
            .progress(0)
            .build();
  }

  public static Integer calculateOrderProgress(Integer total, Integer available) {
    return 100 * (total - available) / total;
  }

}

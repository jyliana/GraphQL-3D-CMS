package com.example.graphql.resolver;

import com.example.graphql.datasource.dto.UserDto;
import com.example.graphql.exception.UserPermissionsException;
import com.example.graphql.service.ExecutionService;
import com.example.graphql.service.OrderService;
import com.example.graphql.service.UserService;
import com.example.graphql.types.*;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

import static com.example.graphql.utils.GraphqlBeanMapper.*;

@DgsComponent
@AllArgsConstructor
public class ExecutionDataResolver {

  private UserService userService;
  private ExecutionService executionService;
  private OrderService orderService;

  @DgsMutation
  public Execution takeDetailsInWork(@RequestHeader(name = "authToken") String authToken, @InputArgument(name = "detail") DetailInWorkInput input) {
    var user = validateUser(authToken, UserCategory.WORKER);

    orderService.changeOrderDetails(UUID.fromString(input.getOrderId()), UUID.fromString(input.getModelId()), input.getTotalAmount());
    var detail = mapToEntity(UUID.randomUUID(), input.getOrderId(), input.getModelId(), input.getTotalAmount(), user.getId());
    var saved = executionService.createExecutionForDetail(detail);

    return mapToGraphql(saved);
  }

  @DgsMutation
  public Execution updateDetailsInWork(@RequestHeader(name = "authToken") String authToken, @InputArgument(name = "execution") ExecutionUpdateInput input) {
    validateUser(authToken, UserCategory.WORKER);
    var execution = executionService.updateExecution(input.getExecutionId(), input.getCompleted());

    return mapToGraphql(execution);
  }

  private UserDto validateUser(String authToken, UserCategory userCategory) {
    var user = userService.findUserByAuthToken(authToken);

    if (!user.getCategory().equals(userCategory)) {
      throw new UserPermissionsException();
    }
    return user;
  }
}

package com.example.graphql.resolver;

import com.example.graphql.DgsConstants;
import com.example.graphql.service.UserService;
import com.example.graphql.types.*;
import com.example.graphql.utils.GraphqlBeanMapper;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import graphql.relay.SimpleListConnection;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@DgsComponent
@AllArgsConstructor
public class UserDataResolver {

  private UserService userService;
  private final GraphqlBeanMapper mapper;

  @DgsQuery(field = DgsConstants.QUERY.Me)
  public User accountInfo(@RequestHeader(name = "authToken") String authToken) {
    var user = userService.findUserByAuthToken(authToken);
    return mapper.mapToGraphql(user);
  }

  @DgsQuery(field = DgsConstants.QUERY.Users)
  public List<User> getUsers() {
    return userService.getAllUsers().stream().map(mapper::mapToGraphql).toList();
  }

  @DgsQuery
  public UserPagination usersPagination(DataFetchingEnvironment env, @InputArgument Integer page,
                                        @InputArgument Integer size) {
    var pageUsers = userService.findUsers(page, size);
    var users = pageUsers.getContent().stream().map(mapper::mapToGraphql).toList();

    var pageConnection = new SimpleListConnection<>(users).get(env);
    var paginatedResult = new UserPagination();
    paginatedResult.setUserConnection(pageConnection);
    paginatedResult.setPage(pageUsers.getNumber());
    paginatedResult.setSize(pageUsers.getSize());
    paginatedResult.setTotalElement(pageUsers.getTotalElements());
    paginatedResult.setTotalPage(pageUsers.getTotalPages());

    return paginatedResult;
  }

  @DgsQuery(field = DgsConstants.QUERY.User)
  public User getUserByUsername(@InputArgument String username) {
    var user = userService.getUser(username);
    return mapper.mapToGraphql(user);
  }

  @DgsMutation(field = DgsConstants.MUTATION.UserCreate)
  public UserResponse createUser(@InputArgument(name = "user") UserCreateInput input) {
    var user = mapper.mapToEntity(input);
    var saved = userService.createUser(user);
    return UserResponse.newBuilder().user(mapper.mapToGraphql(saved)).build();
  }

  @DgsMutation
  public UserResponse userLogin(@InputArgument(name = "user") UserLoginInput input) {
    var generatedToken = userService.login(input.getEmail(), input.getPassword());
    var userAuthToken = mapper.mapToGraphql(generatedToken);
    var userInfo = accountInfo(userAuthToken.getAuthToken());

    return UserResponse.newBuilder()
            .authToken(userAuthToken)
            .user(userInfo)
            .build();
  }

}

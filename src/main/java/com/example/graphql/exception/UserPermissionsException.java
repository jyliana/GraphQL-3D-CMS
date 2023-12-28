package com.example.graphql.exception;

public class UserPermissionsException extends RuntimeException {
  public UserPermissionsException() {
    super("You don't have access for this action");
  }
}

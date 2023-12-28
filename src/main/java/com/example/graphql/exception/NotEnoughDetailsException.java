package com.example.graphql.exception;

public class NotEnoughDetailsException extends RuntimeException {

  public NotEnoughDetailsException() {
    super("There are not enough details for execution");
  }

}

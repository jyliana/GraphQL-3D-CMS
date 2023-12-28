package com.example.graphql.config;

import com.example.graphql.DgsConstants;
import graphql.GraphQLError;
import graphql.execution.ResultPath;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.fieldvalidation.FieldAndArguments;
import graphql.execution.instrumentation.fieldvalidation.FieldValidationEnvironment;
import graphql.execution.instrumentation.fieldvalidation.FieldValidationInstrumentation;
import graphql.execution.instrumentation.fieldvalidation.SimpleFieldValidation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

@Configuration
public class InstrumentationConfig {

  private static final String PATH_STRING = "/";

  private BiFunction<FieldAndArguments, FieldValidationEnvironment, Optional<GraphQLError>> emailMustNotBeGmail() {
    return (fieldAndArguments, fieldValidationEnvironment) -> {
      Map<String, Object> argUser = fieldAndArguments.getArgumentValue(DgsConstants.MUTATION.USERCREATE_INPUT_ARGUMENT.User);
      var email = (String) argUser.get(DgsConstants.USER.Email);

      return email.toLowerCase().endsWith("@gmail.com") ?
              Optional.of(fieldValidationEnvironment.mkError("Gmail is not allowed, please choose other mail"))
              : Optional.empty();
    };
  }

  @Bean
  Instrumentation emailValidationInstrumentation() {
    var pathCreateNewUser = ResultPath.parse(PATH_STRING + DgsConstants.MUTATION.UserCreate);
    var fieldValidation = new SimpleFieldValidation();
    fieldValidation.addRule(pathCreateNewUser, emailMustNotBeGmail());
    return new FieldValidationInstrumentation(fieldValidation);
  }
}

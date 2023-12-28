package com.example.graphql.service;

import com.example.graphql.datasource.dto.ExecutionDto;
import com.example.graphql.datasource.repository.ExecutionRepository;
import com.example.graphql.datasource.repository.OrderDetailsRepository;
import com.example.graphql.exception.NotEnoughDetailsException;
import com.example.graphql.types.ExecutionStatus;
import com.example.graphql.types.OrderDetailStatus;
import com.netflix.graphql.dgs.exceptions.DgsEntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ExecutionService {

  private ExecutionRepository executionRepository;
  private OrderDetailsRepository orderDetailsRepository;

  public ExecutionDto createExecutionForDetail(ExecutionDto detail) {
    return executionRepository.save(detail);
  }

  public ExecutionDto updateExecution(String executionId, Integer completed) {
    var execution = executionRepository.findById(UUID.fromString(executionId)).orElseThrow(DgsEntityNotFoundException::new);
    if (execution.getStatus().equals(ExecutionStatus.COMPLETED)) {
      throw new NotEnoughDetailsException();
    }

    execution.setCompleted(execution.getCompleted() + completed);
    var progress = calculateProgress(execution.getTotal(), execution.getCompleted());
    execution.setStatus(calculateStatus(progress));
    execution.setProgress(progress);

    var allExecutions = executionRepository.findByOrderIdAndModelId(execution.getOrderId(), execution.getModelId());
    var allCompleted = allExecutions.stream().allMatch(exec -> exec.getStatus().equals(ExecutionStatus.COMPLETED));
    if (allCompleted) {
      var orderDetail = orderDetailsRepository.findByOrderIdAndModelId(execution.getOrderId(), execution.getModelId()).orElseThrow(DgsEntityNotFoundException::new);
      orderDetail.setStatus(OrderDetailStatus.COMPLETED);
      orderDetailsRepository.save(orderDetail);
    }

    return executionRepository.save(execution);
  }

  private ExecutionStatus calculateStatus(Integer progress) {
    return switch (progress) {
      case (0) -> ExecutionStatus.NOT_STARTED;
      case (100) -> ExecutionStatus.COMPLETED;
      default -> ExecutionStatus.IN_PROGRESS;
    };
  }

  public Integer calculateProgress(Integer total, Integer part) {
    return 100 * part / total;
  }
}

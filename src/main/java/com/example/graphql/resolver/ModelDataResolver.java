package com.example.graphql.resolver;

import com.example.graphql.DgsConstants;
import com.example.graphql.service.ModelService;
import com.example.graphql.types.Model;
import com.example.graphql.types.ModelCreateInput;
import com.example.graphql.utils.GraphqlBeanMapper;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import lombok.AllArgsConstructor;

import java.util.List;

@DgsComponent
@AllArgsConstructor
public class ModelDataResolver {

  private ModelService modelService;
  private GraphqlBeanMapper mapper;

  @DgsMutation(field = DgsConstants.MUTATION.ModelCreate)
  public Model createDetail(@InputArgument(name = "model") ModelCreateInput input) {
    var model = mapper.mapToEntity(input);
    var saved = modelService.createDetail(model);
    return mapper.mapToGraphql(saved);
  }

  @DgsQuery(field = DgsConstants.QUERY.Models)
  public List<Model> getAllModels() {
    return modelService.getAllModels().stream().map(mapper::mapToGraphql).toList();
  }

}

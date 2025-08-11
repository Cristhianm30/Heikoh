package io.github.cristhianm30.heikoh.infrastructure.mapper;

import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import io.github.cristhianm30.heikoh.infrastructure.entity.ExpenseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ExpenseEntityMapper {

    ExpenseEntity toEntity(ExpenseModel model);

    ExpenseModel toModel(ExpenseEntity entity);
}
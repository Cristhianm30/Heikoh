package io.github.cristhianm30.heikoh.infrastructure.mapper;

import io.github.cristhianm30.heikoh.domain.model.IncomeModel;
import io.github.cristhianm30.heikoh.infrastructure.entity.IncomeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IncomeEntityMapper {


    IncomeEntity toEntity(IncomeModel model);

    IncomeModel toModel(IncomeEntity entity);
}
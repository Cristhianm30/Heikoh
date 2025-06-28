package io.github.cristhianm30.Heikoh.infrastructure.infrastructure.mapper;

import io.github.cristhianm30.Heikoh.domain.model.UserModel;
import io.github.cristhianm30.Heikoh.infrastructure.infrastructure.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IUserEntityMapper {

    UserEntity toEntity(UserModel userModel);
    UserModel toModel(UserEntity userEntity);
}

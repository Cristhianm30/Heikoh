package io.github.cristhianm30.Heikoh.application.mapper;


import io.github.cristhianm30.Heikoh.application.dto.request.RegisterUserRequest;
import io.github.cristhianm30.Heikoh.application.dto.response.UserResponse;
import io.github.cristhianm30.Heikoh.domain.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserDtoMapper {

    UserModel toUserDomain(RegisterUserRequest request);

    UserResponse toUserResponse(UserModel user);
}
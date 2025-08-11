package io.github.cristhianm30.heikoh.application.mapper;

import io.github.cristhianm30.heikoh.application.dto.response.LoginResponse;
import io.github.cristhianm30.heikoh.domain.model.LoginData;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface AuthDtoMapper {
    LoginResponse toLoginResponse(LoginData loginData);
}

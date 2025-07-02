package io.github.cristhianm30.heikoh.application.service.impl;


import io.github.cristhianm30.heikoh.application.dto.request.IncomeRequest;
import io.github.cristhianm30.heikoh.application.dto.response.IncomeResponse;
import io.github.cristhianm30.heikoh.application.mapper.IncomeMapper;
import io.github.cristhianm30.heikoh.application.service.IncomeService;
import io.github.cristhianm30.heikoh.domain.exception.IncomeNotRegisteredException;
import io.github.cristhianm30.heikoh.domain.model.IncomeModel;
import io.github.cristhianm30.heikoh.domain.port.in.RegisterIncomeServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static io.github.cristhianm30.heikoh.domain.util.constant.ExceptionConstants.INCOME_NOT_REGISTERED;
import static io.github.cristhianm30.heikoh.domain.util.constant.LogConstant.INCOME_REGISTRATION_FAILED;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {

    private final RegisterIncomeServicePort registerIncomeServicePort;
    private final IncomeMapper incomeMapper;

    @Override
    public Mono<IncomeResponse> registerIncome(IncomeRequest request, Long userId) {
        IncomeModel incomeModel = incomeMapper.toModel(request);
        incomeModel.setUserId(userId);
        return registerIncomeServicePort.registerIncome(incomeModel)
                .map(incomeMapper::toResponse)
                .doOnError(ex -> log.error(INCOME_REGISTRATION_FAILED, ex.getMessage()))
                .onErrorMap(ex -> new IncomeNotRegisteredException(INCOME_NOT_REGISTERED));
    }
}

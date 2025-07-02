package io.github.cristhianm30.heikoh.application.service.impl;


import io.github.cristhianm30.heikoh.application.dto.request.IncomeRequest;
import io.github.cristhianm30.heikoh.application.dto.response.IncomeResponse;
import io.github.cristhianm30.heikoh.application.mapper.IncomeMapper;
import io.github.cristhianm30.heikoh.application.service.IncomeService;
import io.github.cristhianm30.heikoh.domain.model.IncomeModel;
import io.github.cristhianm30.heikoh.domain.port.in.RegisterIncomeServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
                .map(incomeMapper::toResponse);
    }
}

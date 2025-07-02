package io.github.cristhianm30.heikoh.application.service.impl;


import io.github.cristhianm30.heikoh.application.dto.request.ExpenseRequest;
import io.github.cristhianm30.heikoh.application.dto.response.ExpenseResponse;
import io.github.cristhianm30.heikoh.application.mapper.ExpenseMapper;
import io.github.cristhianm30.heikoh.application.service.ExpenseService;
import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import io.github.cristhianm30.heikoh.domain.port.in.RegisterExpenseServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final RegisterExpenseServicePort registerExpenseServicePort;
    private final ExpenseMapper expenseMapper;

    @Override
    public Mono<ExpenseResponse> registerExpense(ExpenseRequest request, Long userId) {
        ExpenseModel expenseModel = expenseMapper.toModel(request);
        expenseModel.setUserId(userId);
        return registerExpenseServicePort.registerExpense(expenseModel)
                .map(expenseMapper::toResponse);
    }
}

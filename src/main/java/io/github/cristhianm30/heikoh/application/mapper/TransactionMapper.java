package io.github.cristhianm30.heikoh.application.mapper;

import io.github.cristhianm30.heikoh.application.dto.request.TransactionRequest;
import io.github.cristhianm30.heikoh.application.dto.response.TransactionResponse;
import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import io.github.cristhianm30.heikoh.domain.model.IncomeModel;
import io.github.cristhianm30.heikoh.domain.model.TransactionData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "type", constant = "Gasto")
    @Mapping(target = "amount", source = "amount", qualifiedByName = "roundAmount")
    TransactionResponse toTransactionResponse(ExpenseModel expenseModel);

    @Mapping(target = "type", constant = "Ingreso")
    @Mapping(target = "amount", source = "amount", qualifiedByName = "roundAmount")
    TransactionResponse toTransactionResponse(IncomeModel incomeModel);

    TransactionData toTransactionData(TransactionRequest request);

    @Named("roundAmount")
    default BigDecimal roundAmount(BigDecimal value) {
        return value == null ? null : value.setScale(2, RoundingMode.HALF_UP);
    }
}

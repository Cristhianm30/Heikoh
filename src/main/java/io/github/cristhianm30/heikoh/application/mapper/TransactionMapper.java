package io.github.cristhianm30.heikoh.application.mapper;

import io.github.cristhianm30.heikoh.application.dto.request.TransactionsRequest;
import io.github.cristhianm30.heikoh.application.dto.response.TransactionResponse;
import io.github.cristhianm30.heikoh.application.dto.response.TransactionsResponse;
import io.github.cristhianm30.heikoh.domain.model.ExpenseModel;
import io.github.cristhianm30.heikoh.domain.model.IncomeModel;
import io.github.cristhianm30.heikoh.domain.model.TransactionsData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface TransactionMapper {

    @Mapping(target = "type", constant = "Gasto")
    @Mapping(target = "amount", source = "amount", qualifiedByName = "roundAmount")
    TransactionsResponse toTransactionsResponse(ExpenseModel expenseModel);

    @Mapping(target = "type", constant = "Ingreso")
    @Mapping(target = "amount", source = "amount", qualifiedByName = "roundAmount")
    TransactionsResponse toTransactionsResponse(IncomeModel incomeModel);

    TransactionsData toTransactionData(TransactionsRequest request);

    @Mapping(target = "type", constant = "Gasto")
    @Mapping(target = "date", source = "transactionDate")
    @Mapping(target = "amount", source = "amount", qualifiedByName = "roundAmount")
    TransactionResponse toTransactionResponse(ExpenseModel expenseModel);

    @Mapping(target = "type", constant = "Ingreso")
    @Mapping(target = "date", source = "transactionDate")
    @Mapping(target = "amount", source = "amount", qualifiedByName = "roundAmount")
    TransactionResponse toTransactionResponse(IncomeModel incomeModel);

    ExpenseModel toExpenseModel(io.github.cristhianm30.heikoh.application.dto.request.RegisterExpenseRequest request);

    ExpenseModel toExpenseModel(io.github.cristhianm30.heikoh.application.dto.request.UpdateExpenseRequest request);

    IncomeModel toIncomeModel(io.github.cristhianm30.heikoh.application.dto.request.RegisterIncomeRequest request);

    IncomeModel toIncomeModel(io.github.cristhianm30.heikoh.application.dto.request.UpdateIncomeRequest request);

    @Named("roundAmount")
    default BigDecimal roundAmount(BigDecimal value) {
        return value == null ? null : value.setScale(2, RoundingMode.HALF_UP);
    }
}

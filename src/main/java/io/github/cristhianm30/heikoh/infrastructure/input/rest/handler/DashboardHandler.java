package io.github.cristhianm30.heikoh.infrastructure.input.rest.handler;

import io.github.cristhianm30.heikoh.application.service.DashboardService;
import io.github.cristhianm30.heikoh.domain.exception.InvalidDateRangeException;
import io.github.cristhianm30.heikoh.infrastructure.configuration.security.jwt.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class DashboardHandler {

    private final DashboardService dashboardService;

    public Mono<ServerResponse> getFinancialSummary(ServerRequest request) {
        LocalDate startDate = request.queryParam("startDate")
                .map(this::parseDate)
                .orElse(null);
        LocalDate endDate = request.queryParam("endDate")
                .map(this::parseDate)
                .orElse(null);

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            return Mono.error(new InvalidDateRangeException("Start date cannot be after end date."));
        }

        return withAuthenticatedUser(request, user ->
                dashboardService.getFinancialSummary(user.getId(), startDate, endDate)
                        .flatMap(summary -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(summary))
                        .onErrorResume(DateTimeParseException.class, e ->
                                Mono.error(new InvalidDateRangeException("Invalid date format. Please use YYYY-MM-DD.")))
        );
    }

    private LocalDate parseDate(String dateString) {
        return LocalDate.parse(dateString);
    }

    private Mono<ServerResponse> withAuthenticatedUser(ServerRequest request,
                                                       Function<AuthenticatedUser, Mono<ServerResponse>> action) {
        return request.principal()
                .cast(Authentication.class)
                .map(Authentication::getPrincipal)
                .cast(AuthenticatedUser.class)
                .flatMap(action);
    }
}

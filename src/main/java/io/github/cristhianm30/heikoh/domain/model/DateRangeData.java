package io.github.cristhianm30.heikoh.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DateRangeData {
    private final LocalDate startDate;
    private final LocalDate endDate;
}
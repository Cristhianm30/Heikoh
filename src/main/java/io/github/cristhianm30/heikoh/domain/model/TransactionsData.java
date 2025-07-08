package io.github.cristhianm30.heikoh.domain.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionsData {
    private Integer year;
    private Integer month;
    private Integer limit;
    private Integer offset;
    private String type;
}
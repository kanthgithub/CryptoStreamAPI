package com.cryptoStreamAPI.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovingAverageModel {

    private BigDecimal movingAveragePx;
    private LocalDateTime tickerDate;
    private Integer dayInterval;

    
}

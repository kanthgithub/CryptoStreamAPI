package com.cryptoStreamAPI.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HistoricDataModel {

    private String base;
    private String currency;

    private List<PriceModel>  prices;
}


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
public class MarketDataModel {

    private Long sequence;
    private List<OrderItemModel> bids; // price, size, orders
    private List<OrderItemModel> asks; // price, size, orders
}


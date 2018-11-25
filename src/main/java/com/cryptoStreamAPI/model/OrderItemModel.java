package com.cryptoStreamAPI.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemModel {
    private BigDecimal price;
    private BigDecimal size;
    private BigDecimal remainingSize;
    private BigDecimal oldSize;
    private BigDecimal newSize;
    private String orderId; // a uuid that represents the individual order placed.
    private BigDecimal num;
    private String messageType;
    private String side;
    private String reason;
}

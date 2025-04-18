package com.univiser.shopping.domain.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    private Long id;

    private String name;

    private String description;

    private int quantity;

    private BigDecimal price;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

package com.univiser.shopping.domain;

import com.univiser.shopping.domain.entity.Item;
import lombok.Data;

import java.util.List;

@Data
public class InventoryResponse {

    private String statusMessage;
    private String statusCode;
    private String responseTime;
    private String origin;
    private List<Item> result;
}

package com.univiser.shopping.shopping_management.service;

import com.univiser.shopping.domain.APIResponse;
import org.springframework.http.ResponseEntity;

public interface ShoppingService {

    ResponseEntity<APIResponse> getAllAvailableItemsFromInventoryService();
}

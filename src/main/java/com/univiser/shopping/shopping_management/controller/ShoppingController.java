package com.univiser.shopping.shopping_management.controller;

import com.univiser.shopping.domain.APIResponse;
import com.univiser.shopping.shopping_management.service.ShoppingService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v1/shopping-management")
@RequiredArgsConstructor
public class ShoppingController {

    @NonNull
    private final ShoppingService shoppingService;

    @GetMapping("/fetch-all-from-inventory")
    public ResponseEntity<APIResponse> getAllItems() {
        log.info("Fetching all inventory items");
        var response = shoppingService.getAllAvailableItemsFromInventoryService();
        log.info("Successfully fetched all inventory items");
        return response;
    }


}

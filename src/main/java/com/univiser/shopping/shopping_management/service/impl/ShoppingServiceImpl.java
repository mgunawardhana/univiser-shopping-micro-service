package com.univiser.shopping.shopping_management.service.impl;

import com.univiser.shopping.domain.APIResponse;
import com.univiser.shopping.domain.InventoryResponse;
import com.univiser.shopping.shopping_management.service.ShoppingService;
import com.univiser.shopping.util.ResponseUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class ShoppingServiceImpl implements ShoppingService {

    @Value("${inventory.service.base-url}")
    private String inventoryServiceUrl;

    @NonNull
    private final ResponseUtil responseUtil;

    private final WebClient.Builder builder = WebClient.builder();

    public ShoppingServiceImpl(@NonNull ResponseUtil responseUtil) {
        this.responseUtil = responseUtil;
    }

    @Override
    public ResponseEntity<APIResponse> getAllAvailableItemsFromInventoryService() {
        try {
            InventoryResponse inventoryResponse = builder.build()
                    .get()
                    .uri(inventoryServiceUrl)
                    .retrieve()
                    .bodyToMono(InventoryResponse.class)
                    .block();

            log.info("Fetched items from inventory: {}", inventoryResponse.getResult());

            return responseUtil.wrapSuccess(inventoryResponse.getResult(), HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error fetching items: {}", e.getMessage());
            return responseUtil.wrapError("Error fetching items!", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

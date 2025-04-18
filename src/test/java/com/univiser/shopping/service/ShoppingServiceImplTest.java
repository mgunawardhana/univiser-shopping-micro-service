package com.univiser.shopping.service;

import com.univiser.shopping.domain.APIResponse;
import com.univiser.shopping.domain.InventoryResponse;
import com.univiser.shopping.domain.entity.Item;
import com.univiser.shopping.shopping_management.service.impl.ShoppingServiceImpl;
import com.univiser.shopping.util.ResponseUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShoppingServiceImplTest {

    @Mock
    private ResponseUtil responseUtil;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private ShoppingServiceImpl shoppingService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        String INVENTORY_SERVICE_URL = "http://inventory-service/api/items";
        ReflectionTestUtils.setField(shoppingService, "inventoryServiceUrl", INVENTORY_SERVICE_URL);
        ReflectionTestUtils.setField(shoppingService, "builder", webClientBuilder);

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(INVENTORY_SERVICE_URL)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    public void testGetAllAvailableItemsFromInventoryService_Success() {
        List<Item> mockItems = Arrays.asList(
                Item.builder()
                        .id(1L)
                        .name("Test Item 1")
                        .description("Description 1")
                        .quantity(10)
                        .price(new BigDecimal("19.99"))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),
                Item.builder()
                        .id(2L)
                        .name("Test Item 2")
                        .description("Description 2")
                        .quantity(5)
                        .price(new BigDecimal("29.99"))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );

        InventoryResponse mockResponse = new InventoryResponse();
        mockResponse.setStatusCode("200");
        mockResponse.setStatusMessage("Success");
        mockResponse.setOrigin("inventory-service");
        mockResponse.setResponseTime("100ms");
        mockResponse.setResult(mockItems);

        APIResponse apiResponse = APIResponse.builder()
                .statusCode("200")
                .statusMessage("Success")
                .result(mockItems)
                .build();

        ResponseEntity<APIResponse> expectedResponse = ResponseEntity.ok(apiResponse);

        when(responseSpec.bodyToMono(InventoryResponse.class)).thenReturn(Mono.just(mockResponse));
        when(responseUtil.wrapSuccess(mockItems, HttpStatus.OK)).thenReturn(expectedResponse);

        ResponseEntity<APIResponse> result = shoppingService.getAllAvailableItemsFromInventoryService();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockItems, result.getBody().getResult());

        verify(responseUtil).wrapSuccess(mockItems, HttpStatus.OK);
    }

    @Test
    public void testGetAllAvailableItemsFromInventoryService_EmptyList() {
        List<Item> emptyList = Collections.emptyList();

        InventoryResponse mockResponse = new InventoryResponse();
        mockResponse.setStatusCode("200");
        mockResponse.setStatusMessage("Success");
        mockResponse.setResult(emptyList);

        APIResponse apiResponse = APIResponse.builder()
                .statusCode("200")
                .statusMessage("Success")
                .result(emptyList)
                .build();

        ResponseEntity<APIResponse> expectedResponse = ResponseEntity.ok(apiResponse);

        when(responseSpec.bodyToMono(InventoryResponse.class)).thenReturn(Mono.just(mockResponse));
        when(responseUtil.wrapSuccess(emptyList, HttpStatus.OK)).thenReturn(expectedResponse);

        ResponseEntity<APIResponse> result = shoppingService.getAllAvailableItemsFromInventoryService();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(emptyList, result.getBody().getResult());

        verify(responseUtil).wrapSuccess(emptyList, HttpStatus.OK);
    }

    @Test
    public void testGetAllAvailableItemsFromInventoryService_Exception() {
        String errorMessage = "Connection timeout";
        Exception exception = new RuntimeException(errorMessage);

        APIResponse errorApiResponse = APIResponse.builder()
                .statusCode("500")
                .statusMessage("Error fetching items!")
                .errorType("RuntimeException")
                .build();

        ResponseEntity<APIResponse> errorResponse = ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorApiResponse);

        when(responseSpec.bodyToMono(InventoryResponse.class)).thenReturn(Mono.error(exception));
        when(responseUtil.wrapError(eq("Error fetching items!"), eq(errorMessage), eq(HttpStatus.INTERNAL_SERVER_ERROR)))
                .thenReturn(errorResponse);

        ResponseEntity<APIResponse> result = shoppingService.getAllAvailableItemsFromInventoryService();

        assertNotNull(result);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Error fetching items!", result.getBody().getStatusMessage());

        verify(responseUtil).wrapError("Error fetching items!", errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testGetAllAvailableItemsFromInventoryService_NullResponse() {
        APIResponse errorApiResponse = APIResponse.builder()
                .statusCode("500")
                .statusMessage("Error fetching items!")
                .errorType("NullPointerException")
                .build();

        ResponseEntity<APIResponse> errorResponse = ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorApiResponse);

        when(responseSpec.bodyToMono(InventoryResponse.class)).thenReturn(Mono.empty());
        when(responseUtil.wrapError(eq("Error fetching items!"), anyString(), eq(HttpStatus.INTERNAL_SERVER_ERROR)))
                .thenReturn(errorResponse);

        ResponseEntity<APIResponse> result = shoppingService.getAllAvailableItemsFromInventoryService();

        assertNotNull(result);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());

        verify(responseUtil).wrapError(eq("Error fetching items!"), any(), eq(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private static class MockitoAnnotations {
        public static void openMocks(Object testClass) {
        }
    }
}
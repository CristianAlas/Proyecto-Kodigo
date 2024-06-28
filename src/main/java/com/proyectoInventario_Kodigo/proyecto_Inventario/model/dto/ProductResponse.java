package com.proyectoInventario_Kodigo.proyecto_Inventario.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
public class ProductResponse {

    private Long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal price;
    private String status;
    private Set<CategoryResponse> categories;
    private Set<BrandResponse> brands;
    private Set<ModelProductResponse> models;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreReceptionDetailRequest {

        @NotNull(message = "The field productId cannot be null")
        private Long productId;

        @NotNull(message = "The field aisleId cannot be null")
        private Long aisleId;

        @NotNull(message = "The field shelfId cannot be null")
        private Long shelfId;

        @NotNull(message = "The field quantity cannot be null")
        @Min(value = 1, message = "Quantity must be greater than 0")
        private int quantity;
    }
}

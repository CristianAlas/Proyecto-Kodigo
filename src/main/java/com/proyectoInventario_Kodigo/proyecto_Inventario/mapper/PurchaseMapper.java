package com.proyectoInventario_Kodigo.proyecto_Inventario.mapper;

import com.proyectoInventario_Kodigo.proyecto_Inventario.model.dto.PurchaseRequest;
import com.proyectoInventario_Kodigo.proyecto_Inventario.model.dto.PurchaseResponse;
import com.proyectoInventario_Kodigo.proyecto_Inventario.model.entity.Purchase;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {

    PurchaseResponse toResponse(Purchase purchase);

    Purchase toEntity(PurchaseRequest request);
}


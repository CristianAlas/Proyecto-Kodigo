package com.proyectoInventario_Kodigo.proyecto_Inventario.service.impl;

import com.proyectoInventario_Kodigo.proyecto_Inventario.mapper.PurchaseMapper;
import com.proyectoInventario_Kodigo.proyecto_Inventario.service.interfaces.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.proyectoInventario_Kodigo.proyecto_Inventario.exceptions.*;
import com.proyectoInventario_Kodigo.proyecto_Inventario.model.dto.*;
import com.proyectoInventario_Kodigo.proyecto_Inventario.model.entity.*;
import com.proyectoInventario_Kodigo.proyecto_Inventario.repository.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ModelProductRepository modelProductRepository;
    private final SupplierRepository supplierRepository;
    private final PurchaseMapper purchaseMapper;

    private static final Logger logger = LoggerFactory.getLogger(PurchaseServiceImpl.class);

    @Override
    @Transactional
    public PurchaseResponse createPurchase(PurchaseRequest request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(SupplierNotFoundException::new);

        Purchase purchase = new Purchase();
        purchase.setSupplier(supplier);
        purchase.setCode(generatePurchaseCode());

        Set<PurchaseDetail> details = request.getDetails().stream().map(detailRequest -> {
            Product product = createOrUpdateProduct(detailRequest);
            PurchaseDetail detail = new PurchaseDetail();
            detail.setProduct(product);
            detail.setQuantity(detailRequest.getQuantity());
            detail.setPurchase(purchase);
            return detail;
        }).collect(Collectors.toSet());

        purchase.setDetails(details);
        Purchase savedPurchase = purchaseRepository.save(purchase);

        return purchaseMapper.toResponse(savedPurchase);
    }

    @Override
    @Transactional
    public PurchaseResponse updatePurchase(Long purchaseId, PurchaseRequest request) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(PurchaseNotFoundException::new);

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(SupplierNotFoundException::new);

        purchase.setSupplier(supplier);

        Set<PurchaseDetail> details = request.getDetails().stream().map(detailRequest -> {
            Product product = createOrUpdateProduct(detailRequest);
            PurchaseDetail detail = new PurchaseDetail();
            detail.setProduct(product);
            detail.setQuantity(detailRequest.getQuantity());
            detail.setPurchase(purchase);
            return detail;
        }).collect(Collectors.toSet());

        purchase.getDetails().clear();
        purchase.getDetails().addAll(details);

        Purchase updatedPurchase = purchaseRepository.save(purchase);

        return purchaseMapper.toResponse(updatedPurchase);
    }

    @Override
    @Transactional
    public void deletePurchase(Long purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(PurchaseNotFoundException::new);
        purchaseRepository.delete(purchase);
    }

    private Product createOrUpdateProduct(PurchaseDetailRequest detailRequest) {
        Product product;
        if (detailRequest.getProductId() != null) {
            product = productRepository.findById(detailRequest.getProductId())
                    .orElseThrow(ProductNotFoundException::new);
        } else {
            product = new Product();
            product.setCode(generateProductCode());
        }

        product.setName(detailRequest.getName());
        product.setDescription(detailRequest.getDescription());
        product.setPrice(detailRequest.getPrice());
        product.setStatus(detailRequest.getStatus());
        product.setStock(detailRequest.getQuantity());

        Set<Category> categories = categoryRepository.findAllById(detailRequest.getCategoryIds()).stream()
                .collect(Collectors.toSet());
        product.setCategories(categories);

        Set<Brand> brands = brandRepository.findAllById(detailRequest.getBrandIds()).stream()
                .collect(Collectors.toSet());
        product.setBrands(brands);

        // Actualizar modelos de producto
        Set<ModelProduct> models = detailRequest.getModels().stream().map(modelRequest -> {
            ModelProduct model = new ModelProduct();
            model.setCode(generateUniqueModelCode(modelRequest.getName()));
            model.setName(modelRequest.getName());
            model.setProduct(product);
            return model;
        }).collect(Collectors.toSet());

        product.getModels().clear();
        product.getModels().addAll(models);

        return productRepository.save(product);
    }

    private String generatePurchaseCode() {
        String prefix = "PUR";
        String suffix = String.format("%04d", purchaseRepository.count() + 1);
        return prefix + suffix;
    }

    private String generateProductCode() {
        String prefix = "PRD";
        String suffix = String.format("%04d", productRepository.count() + 1);
        return prefix + suffix;
    }

    private String generateUniqueModelCode(String name) {
        String prefix = "MDL";
        long count = modelProductRepository.countByName(name);
        String suffix;
        String code;
        do {
            suffix = String.format("%04d", ++count);
            code = prefix + suffix;
        } while (modelProductRepository.existsByCode(code));
        return code;
    }
}

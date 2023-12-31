package com.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.model.request.creator.AddProductRequest;
import com.ecommerce.model.response.creator.AddProductResponse;
import com.ecommerce.service.ProductItemService;

@RestController
@RequestMapping("/productItem")
public class ProductItemController {
	@Autowired
	private ProductItemService productItemService;
	
	@PostMapping("/addProduct")
	public ResponseEntity<Object> addProduct(@RequestBody AddProductRequest addProductRequest) {
		AddProductResponse response = productItemService.addProduct(addProductRequest);
		return ResponseEntity.ok().body(response);
	}
}

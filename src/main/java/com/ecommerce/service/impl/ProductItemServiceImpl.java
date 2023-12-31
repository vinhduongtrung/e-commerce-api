package com.ecommerce.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.ProductConfiguration;
import com.ecommerce.entity.ProductItem;
import com.ecommerce.entity.Shop;
import com.ecommerce.entity.Variation;
import com.ecommerce.entity.VariationValue;
import com.ecommerce.model.request.creator.AddProductRequest;
import com.ecommerce.model.request.creator.ProductItemRequest;
import com.ecommerce.model.request.creator.VariationRequest;
import com.ecommerce.model.request.creator.VariationValueRequest;
import com.ecommerce.model.response.creator.AddProductResponse;
import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductConfigRepository;
import com.ecommerce.repository.ProductItemRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ShopRepository;
import com.ecommerce.repository.VariationRepository;
import com.ecommerce.repository.VariationValueRepository;
import com.ecommerce.service.ProductItemService;

@Service
public class ProductItemServiceImpl implements ProductItemService{
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductItemRepository productItemRepository;
	@Autowired
	private ProductConfigRepository productConfigRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private ShopRepository shopRepository;
	@Autowired
	private VariationRepository variationRepository;
	@Autowired
	private VariationValueRepository variationValueRepository;
	
	@Override
	public AddProductResponse addProduct(AddProductRequest addProductRequest) {
		Product product = new Product();
		//set product info
		product.setName(addProductRequest.getProductName());
		product.setDescription(addProductRequest.getDescription());
		product.setBrand(addProductRequest.getBrand());
		//set date
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String createdDate = now.format(formatter);
		product.setCreatedDate(createdDate);
		//add shop
		Long shopId = addProductRequest.getShopId();
		Shop shop = shopRepository.findById(shopId).get();
		product.setShop(shop);
		//add category
		String categoryName = addProductRequest.getCategoryName();
		Category category = categoryRepository.findByName(categoryName);
		if(category != null) {
			product.setCategory(category);
		}else {
			Category newCategory = new Category();
			newCategory.setName(categoryName);
			Category savedCategory = categoryRepository.save(newCategory);
			product.setCategory(savedCategory);
		}
		//insert product
		productRepository.save(product);
		//insert variation
		List<VariationRequest> variations = addProductRequest.getVariations();
		//loop 1, save variation
		for(int i = 0; i < variations.size(); i++) {
			String variationName = variations.get(i).getVariationName();
			Variation find = variationRepository.findByName(variationName);
			if(find != null) {
				List<VariationValueRequest> values = variations.get(i).getValues();
				//loop 2, save value
				for(int j = 0; j < values.size(); j++) {
					VariationValue findValue = variationValueRepository.findByValue(values.get(j).getValueName());
					if(findValue == null) {
						VariationValue value = new VariationValue();
						value.setValue(values.get(j).getValueName());
						value.setVariation(find);
						variationValueRepository.save(value);
					}
				}
			}
			else {
				Variation variation = new Variation();
				variation.setName(variationName);
				variation.setProduct(product);
				Variation variationResult = variationRepository.save(variation);
				List<VariationValueRequest> values = variations.get(i).getValues();
				//loop 2, save value
				for(int j = 0; j < values.size(); j++) {
					VariationValue findValue = variationValueRepository.findByValue(values.get(j).getValueName());
					if(findValue == null) {
						VariationValue value = new VariationValue();
						value.setValue(values.get(j).getValueName());
						value.setVariation(variationResult);
						variationValueRepository.save(value);
					}
				}
			}
		}
		//insert product item
		List<ProductItemRequest> items = addProductRequest.getItems();
		//loop 1
		for(int i = 0; i < items.size(); i++) {
			ProductItem item = new ProductItem();
			item.setName(items.get(i).getName());
			item.setQty(items.get(i).getQty());
			item.setImage(items.get(i).getImage());
			item.setOriginalPrice(items.get(i).getOriginalPrice());
			item.setProduct(product);
			ProductItem result = productItemRepository.save(item);
			String[] options = items.get(i).getVariationValue().split("-");
			for(String option : options) {
				VariationValue val = variationValueRepository.findByValue(option);
				ProductConfiguration productConfig = new ProductConfiguration();
				productConfig.setValue(val);
				productConfig.setProductItem(result);
				productConfigRepository.save(productConfig);
			}
		}
		AddProductResponse response = new AddProductResponse();
		return response;
	}
	


}

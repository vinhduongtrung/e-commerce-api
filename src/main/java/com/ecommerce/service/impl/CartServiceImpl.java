package com.ecommerce.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.ProductItem;
import com.ecommerce.entity.Shop;
import com.ecommerce.model.response.creator.CartDTO;
import com.ecommerce.model.response.creator.CartItemCreatorResponse;
import com.ecommerce.model.response.detail.IUserDetail;
import com.ecommerce.model.response.list.ICart;
import com.ecommerce.model.response.list.ICartItemInfo;
import com.ecommerce.model.response.list.ICartItemList;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductItemRepository;
import com.ecommerce.repository.ShopRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.CartService;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartItemRepository cartItemRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ProductItemRepository productItemRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ShopRepository shopRepository;
	

	@Override
	public List<CartItemCreatorResponse> getAllItemInCart(Long cartId) {
		List<CartItem> cartItems = cartItemRepository.findAllItemInCart(cartId);
		List<CartItemCreatorResponse> response = new ArrayList<>();
		for (CartItem cartItem : cartItems) {
			CartItemCreatorResponse res = new CartItemCreatorResponse(cartItem);
			response.add(res);
		}
		return response;
	}


	@Override
	public List<ICartItemList> findAllItemInCartDTO(Long cartId) {
		return cartItemRepository.findAllItemInCartDTO(cartId);
	}


	@Override
	public ICart getAllCartInfo(Long cartId) {
		return cartRepository.getAllCartInfo(cartId);
	}


	@Override
	public CartDTO getEverything(Long cartId) {
		Cart cart = cartRepository.findById(cartId).get();
		Long userId = cart.getUser().getId();
		Map<String, List<ICartItemList>> itemMap = new HashMap<>();
		
		ICart cartInfo = cartRepository.getAllCartInfo(cartId);
		IUserDetail userInfo = userRepository.findByIdDTO(userId);
		List<ICartItemList> cartItems = cartItemRepository.findAllItemInCartDTO(cartId);
		int itemSize = cartItems.size();
		
		for(ICartItemList item : cartItems) {
			ProductItem productItem = productItemRepository.findById(item.getProductItemId()).get();
			Product product = productItem.getProduct();
			Shop shop = shopRepository.findById(product.getShop().getId()).get();
			String shopName = shop.getName();
			itemMap.putIfAbsent(shopName, new ArrayList<>());
			itemMap.get(shopName).add(item);
		}
		List<ICartItemInfo> cartItemInfoList = new ArrayList<>();
		
		for(String shopName : itemMap.keySet()) {
			ICartItemInfo cartItemInfo = new ICartItemInfo();
			cartItemInfo.setShopName(shopName);
			cartItemInfo.setCartItemList(itemMap.get(shopName));
			cartItemInfoList.add(cartItemInfo);
		}
		
		CartDTO cartDTO = new CartDTO();
		cartDTO.setItems(itemSize);
		cartDTO.setCartInfo(cartInfo);
		cartDTO.setUserInfo(userInfo);
		cartDTO.setCartItemInfoList(cartItemInfoList);
		
		return cartDTO;
	}


	@Override
	public List<ICartItemList> findAllCheckedItemInCart(Long cartId) {
		return cartItemRepository.findAllCheckedItemInCartDTO(cartId);
	}

}

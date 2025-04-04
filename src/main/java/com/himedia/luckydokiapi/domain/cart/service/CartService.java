package com.himedia.luckydokiapi.domain.cart.service;



import com.himedia.luckydokiapi.domain.cart.dto.CartItemDTO;
import com.himedia.luckydokiapi.domain.cart.dto.CartItemListDTO;
import com.himedia.luckydokiapi.domain.cart.entity.CartItem;

import java.util.List;

public interface CartService {

    List<CartItemListDTO> getCartItemList(String email);

    List<CartItemListDTO> addCartItem(String email, CartItemDTO cartItemDTO);

    List<CartItemListDTO> changeCartItemQty(String email, Long cartItemId, int qty);

    List<CartItemListDTO> removeCartItem(String email, Long cartItemId);

    List<CartItemListDTO> removeFromCartItemList(String email, List<Long> cartItemIdList);

    default CartItemListDTO entityToDTO(CartItem cartItem) {
        return CartItemListDTO.builder()
                .cartItemId(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getName())
                .price(cartItem.getPrice())
                .qty(cartItem.getQty())
                .discountPrice(cartItem.getDiscountPrice())
                .discountRate(cartItem.getDiscountRate())
                .imageName(cartItem.getProduct().getImageList().get(0).getImageName())
                .tagList(cartItem.getProduct().getTagList())
                .build();
    }


}

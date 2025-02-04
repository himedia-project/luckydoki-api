package com.himedia.luckydokiapi.domain.cart.service;


import com.himedia.luckydokiapi.domain.cart.dto.CartItemDTO;
import com.himedia.luckydokiapi.domain.cart.dto.CartItemListDTO;
import com.himedia.luckydokiapi.domain.cart.entity.Cart;
import com.himedia.luckydokiapi.domain.cart.entity.CartItem;
import com.himedia.luckydokiapi.domain.cart.repository.CartItemRepository;
import com.himedia.luckydokiapi.domain.cart.repository.CartRepository;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    @Override
    public List<CartItemListDTO> getCartItemList(String email) {
        log.info("getCartItemList..........");
        return cartItemRepository.getItemsOfCartList(email).stream()
                .map(this::entityToDTO)
                .toList();
    }

    @Override
    public List<CartItemListDTO> addCartItem(CartItemDTO cartItemDTO) {
        log.info("addCartItem..........");
        String email = cartItemDTO.getEmail();
        Long pno = cartItemDTO.getProductId();

        // 장바구니 가져오기
        Cart cart = this.getCart(email);
        CartItem cartItem = cartItemRepository.getItemOfPno(email, pno);

        if (cartItem == null) {
            // 상품 정보를 데이터베이스에서 가져오기
            Product product = productRepository.findById(pno)
                    .orElseThrow(() -> new EntityNotFoundException("해당 상품이 존재하지 않습니다. productId: " + pno));

            // 새로운 CartItem 생성
            cartItem = CartItem.builder()
                    .product(product)
                    .cart(cart)
                    .price(product.getPrice()) // 가격 설정
                    .build();

            cartItemRepository.save(cartItem);
        } else {
            log.info("이미 장바구니에 존재하는 상품입니다. 상품 ID: " + pno);
        }

        return getCartItemList(email);
    }


    @Override
    public List<CartItemListDTO> removeCartItem(Long cartItemId) {
        log.info("removeCartItem..........");
        Long cartId = cartItemRepository.getCartFromItem(cartItemId);

        log.info("cart id: " + cartId);

        cartItemRepository.deleteById(cartItemId);

        return cartItemRepository.getItemsOfCartByCartId(cartId).stream()
                .map(this::entityToDTO)
                .toList();
    }

    private Cart getCart(String email) {
        Cart cart = null;

        Optional<Cart> result = cartRepository.getCartOfMember(email);
        if (result.isEmpty()) {
            log.info("Cart of the member is not exist!!");
            Member member = Member.builder().email(email).build();
            Cart tempCart = Cart.builder().member(member).build();
            cart = cartRepository.save(tempCart);
        } else {
            cart = result.get();
        }

        return cart;
    }
}

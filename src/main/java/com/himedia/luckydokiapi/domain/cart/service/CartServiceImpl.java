package com.himedia.luckydokiapi.domain.cart.service;


import com.himedia.luckydokiapi.domain.cart.dto.CartItemDTO;
import com.himedia.luckydokiapi.domain.cart.dto.CartItemListDTO;
import com.himedia.luckydokiapi.domain.cart.entity.Cart;
import com.himedia.luckydokiapi.domain.cart.entity.CartItem;
import com.himedia.luckydokiapi.domain.cart.repository.CartItemRepository;
import com.himedia.luckydokiapi.domain.cart.repository.CartRepository;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.service.MemberService;
import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.service.ProductService;
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
    private final MemberService memberService;
    private final ProductService productService;


    @Transactional(readOnly = true)
    @Override
    public List<CartItemListDTO> getCartItemList(String email) {
        log.info("getCartItemList..........");
        return cartItemRepository.getItemsOfCartList(email).stream()
                .map(this::entityToDTO)
                .toList();
    }

    @Override
    public List<CartItemListDTO> addCartItem(String email, CartItemDTO cartItemDTO) {
        log.info("addCartItem..........");
        Long productId = cartItemDTO.getProductId();

        // 장바구니 가져오기
        Cart cart = this.getCart(email);
        CartItem cartItem = cartItemRepository.getItemOfPno(email, productId);

        if (cartItem == null) {
            // 상품 정보를 데이터베이스에서 가져오기
            Product product = productService.getEntity(productId);

            // 새로운 CartItem 생성
            cartItemRepository.save(CartItem.from(product, cart));
        } else {
            log.info("이미 장바구니에 존재하는 상품입니다. 상품 ID: " + productId);
        }

        return this.getCartItemList(email);
    }


    @Override
    public List<CartItemListDTO> removeCartItem(String email, Long cartItemId) {
        log.info("removeCartItem..........");
        memberService.getEntity(email);
        Long cartId = cartItemRepository.getCartFromItem(cartItemId);
        log.info("cart id: {}", cartId);

        // cart_item에서 해당상품 삭제
        cartItemRepository.deleteById(cartItemId);

        return cartItemRepository.getItemsOfCartByCartId(cartId).stream()
                .map(this::entityToDTO)
                .toList();
    }

    @Override
    public List<CartItemListDTO> removeCartItemAll(String email, List<Long> cartItemIdList) {
        log.info("removeCartItemAll..........");
        memberService.getEntity(email);
        // 전체 삭제(벌크)
        cartItemRepository.deleteBulk(cartItemIdList);

        return cartItemRepository.findByIds(cartItemIdList).stream()
                .map(this::entityToDTO)
                .toList();
    }


    /**
     * 이메일을 통해, Cart 객체 가져오기
     * @param email 이메일
     * @return Cart 객체
     */
    private Cart getCart(String email) {
        Cart cart = null;

        Optional<Cart> result = cartRepository.getCartOfMember(email);
        if (result.isEmpty()) {
            log.info("Cart of the member is not exist!!");
            Member member = memberService.getEntity(email);
            cart = cartRepository.save(Cart.from(member));
        } else {
            log.info("Cart of the member already exist!!");
            cart = result.get();
        }

        return cart;
    }
}

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
    public List<CartItemListDTO> addCartItem(String email, CartItemDTO itemDTO) {
        log.info("addCartItem..........");
        memberService.getEntity(email);
        Long productId = itemDTO.getProductId();
        int qty = itemDTO.getQty();
        // 장바구니 아이템 번호 cartItemId가 없는 경우(새로 추가하는 경우)
        Cart cart = this.getCart(email);
        CartItem cartItem = null;

        // 이미 동일한 상품이 담긴 적이 있다면
        cartItem = cartItemRepository.getItemOfPno(email, productId);
        if (cartItem == null) {
            // 그게 아니라면 새로 추가
            Product product = productService.getEntity(productId);
            cartItem = CartItem.from(product, cart, qty);
        } else {
            // 동일한 상품이 맞으면, 수량을 추가
            cartItem.addQty(qty);
        }
        cartItemRepository.save(cartItem);
        return this.getCartItemList(email);
    }

    @Override
    public List<CartItemListDTO> changeCartItemQty(String email, Long cartItemId, int qty) {
        log.info("changeCartItemQty..........");
        // 장바구니 아이템 번호가 있고 && 수량만 변경하는 경우
        memberService.getEntity(email);
        CartItem cartItem = this.getCartItemEntity(cartItemId);
        cartItem.changeQty(qty);

        return getCartItemList(email);
    }


    @Override
    public List<CartItemListDTO> removeCartItem(String email, Long cartItemId) {
        log.info("removeCartItem..........");
        memberService.getEntity(email);
        this.getCartItemEntity(cartItemId);
        Long cartId = cartItemRepository.getCartFromItem(cartItemId);
        log.info("cart id: {}", cartId);

        // cart_item에서 해당상품 삭제
        cartItemRepository.deleteById(cartItemId);

        return cartItemRepository.getItemsOfCartByCartId(cartId).stream()
                .map(this::entityToDTO)
                .toList();
    }

    @Override
    public List<CartItemListDTO> removeFromCartItemList(String email, List<Long> cartItemIdList) {
        log.info("removeCartItemAll..........");
        memberService.getEntity(email);
        cartItemIdList.forEach(this::getCartItemEntity);
        // 전체 삭제(벌크)
        cartItemRepository.deleteBulk(cartItemIdList);

        return cartItemRepository.findByIds(cartItemIdList).stream()
                .map(this::entityToDTO)
                .toList();
    }


    /**
     * 이메일을 통해, Cart 객체 가져오기, 없으면 생성해서 가져오기
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


    /**
     * CartItem Entity 가져오기
     * @param cartItemId CartItem pk
     * @return CartItem Entity
     */
    private CartItem getCartItemEntity(Long cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품이 장바구니에 존재하지 않습니다. cartItemId: " + cartItemId));
    }
}

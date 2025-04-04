package com.himedia.luckydokiapi.domain.product.entity;

import com.himedia.luckydokiapi.domain.cart.entity.CartItem;
import com.himedia.luckydokiapi.domain.community.entity.CommunityProduct;
import com.himedia.luckydokiapi.domain.likes.entity.ProductLike;
import com.himedia.luckydokiapi.domain.order.entity.OrderItem;
import com.himedia.luckydokiapi.domain.product.dto.ProductDTO;
import com.himedia.luckydokiapi.domain.product.dto.TagDTO;
import com.himedia.luckydokiapi.domain.product.enums.ProductApproval;
import com.himedia.luckydokiapi.domain.product.enums.ProductBest;
import com.himedia.luckydokiapi.domain.product.enums.ProductEvent;
import com.himedia.luckydokiapi.domain.product.enums.ProductIsNew;
import com.himedia.luckydokiapi.domain.review.entity.Review;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import com.himedia.luckydokiapi.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

import static com.himedia.luckydokiapi.util.NumberGenerator.generateRandomNumber;

//트랜젝셔널에 걸린 피드만 수정하게 해주는 어노테인션
@DynamicUpdate
@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
@ToString(exclude = {"imageList", "shop", "category", "productTagList", "productLikes", "productReviews", "categoryBridges", "orderItems", "cartItems", "communityProducts"})
@Table(name = "product", indexes = {
    @Index(name = "idx_product_name", columnList = "name"),
    @Index(name = "idx_product_code", columnList = "code")
})
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //mdpick 빼고 best 넣음

    // 상품 코드
    private String code;  // 24343233930

    @Size(max = 35)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "price", nullable = false)
    private Integer price;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "discount_price")
    private Integer discountPrice;

    @ColumnDefault("0")
    @Column(name = "discount_rate")
    private Integer discountRate;

    // LongText
    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;

    @ColumnDefault("0")
    @Column(name = "del_flag")
    private Boolean delFlag;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "stock_number", nullable = false)
    private Integer stockNumber;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'N'")
    @Builder.Default
    private ProductIsNew isNew = ProductIsNew.N;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'N'")
    @Builder.Default
    private ProductBest best = ProductBest.N;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'N'")
    @Builder.Default
    private ProductEvent event = ProductEvent.N;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'N'")
    @Builder.Default
    private ProductApproval approvalStatus = ProductApproval.N;


    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;      // 해당 category

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;


    @ElementCollection
    @Builder.Default
    private List<ProductImage> imageList = new ArrayList<>();

    // 태그 리스트
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ProductTag> productTagList = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ProductLike> productLikes = new ArrayList<>();

    // 리뷰 리스트
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Review> productReviews = new ArrayList<>();

    @OneToMany(mappedBy = "product" , cascade = CascadeType.ALL)
    @Builder.Default
    private List<CategoryBridge> categoryBridges = new ArrayList<>();

    @OneToMany(mappedBy = "product" , cascade = CascadeType.ALL)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "product" , cascade = CascadeType.ALL)
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "product" , cascade = CascadeType.ALL)
    @Builder.Default
    private List<CommunityProduct> communityProducts = new ArrayList<>();

    public static Product copy(Product originProduct) {
        return Product.builder()
                .id(originProduct.getId())
                .code(originProduct.getCode())
                .name(originProduct.getName())
                .price(originProduct.getPrice())
                .discountPrice(originProduct.getDiscountPrice())
                .discountRate(originProduct.getDiscountRate())
                .description(originProduct.getDescription())
                .delFlag(originProduct.getDelFlag())
                .stockNumber(originProduct.getStockNumber())
                .isNew(originProduct.getIsNew())
                .best(originProduct.getBest())
                .event(originProduct.getEvent())
                .approvalStatus(originProduct.getApprovalStatus())
                .category(originProduct.getCategory())
                .shop(originProduct.getShop())
                .imageList(new ArrayList<>(originProduct.imageList))
                .productTagList(new ArrayList<>(originProduct.productTagList))
                .categoryBridges(new ArrayList<>(originProduct.categoryBridges))
                .build();
    }


    public Integer productLikesCount(ProductLike productLike) {
        return productLikes.size();
    }

    /**
     * 상품 이미지, 상품에 추가
     *
     * @param image 이미지
     */
    public void addImage(ProductImage image) {

        image.setOrd(this.imageList.size());
        imageList.add(image);
    }


    /**
     * 상품 이미지 파일, 상품이미지에 추가
     *
     * @param fileName 이미지 파일명
     */
    public void addImageString(String fileName) {

        ProductImage productImage = ProductImage.builder()
                .imageName(fileName)
                .build();
        addImage(productImage);
    }

    /**
     * 상품 이미지 리스트 초기화
     */
    public void clearImageList() {
        this.imageList.clear();
    }

    /*
     * 상품 수정 로직
     */
    public void changeName(String name) {
        this.name = name;
    }

    public void changeBest(ProductBest best) {
        this.best = best;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changeStockNumber(Integer stockNumber) {
        this.stockNumber = stockNumber;
    }


    public void changeIsNew(ProductIsNew isNew) {
        this.isNew = isNew;
    }

    public void changeCategory(Category category) {
        this.category = category;
    }

    public void changeEvent(ProductEvent event) {
        this.event = event;
    }

    public void changeShop(Shop shop) {
        this.shop = shop;
    }

    /**
     * 할인가격 업데이트 -> 할인율 업데이트
     */
    public void updateDiscountRate() {
        if (this.price != null && this.discountPrice != null && this.price > 0) {
            this.discountRate = (int) ((1 - (double) this.discountPrice / this.price) * 100);
        } else {
            this.discountRate = 0;
        }
    }

    public void changePrice(Integer price) {
        this.price = price;
        updateDiscountRate();
    }

    public void changeDiscountPrice(Integer discountPrice) {
        this.discountPrice = discountPrice;
        updateDiscountRate();
    }


    /**
     * 상품 리뷰 평균 구하기
     * @return 리뷰 평균
     */
    public double getReviewAverage() {
        if (productReviews == null || productReviews.isEmpty()) {
            return 0;
        }
        return productReviews.stream().mapToDouble(Review::getRating).average().orElse(0);
    }

    /**
     * 상품 재고 감소
     * @param count 감소할 수량
     */
    public void decreaseStock(int count) {
        if (this.stockNumber - count < 0) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.stockNumber -= count;
    }

    /**
     * 상품 리뷰 카운트
     * @return 리뷰 카운트
     */
    public int getReviewCount() {
        return this.productReviews.size();
    }

    /**
     * 상품 좋아요 카운트
     * @return 좋아요 카운트
     */
    public int getLikesCount() {
        return this.productLikes.size();
    }

    /**
     * 상품 판매량
     * @return 판매량
     */
    public int getSalesCount() {
        return this.orderItems.stream().mapToInt(OrderItem::getCount).sum();
    }

    /**
     * 상품 카테고리 전체 이름
     * @return 카테고리 전체 이름 ex) "상의 > 티셔츠 > 반팔"
     */
    public String getCategoryAllName() {
        String mainCategoryName = null;
        String subCategoryName = null;
        // 3차 카테고리는 무조건 있음
        String childCategoryName = this.category.getName();
        // 2차 카테고리 있다면
        if (this.category.getParent() != null) {
            subCategoryName = this.category.getParent().getName();
            // 3차 카테고리 있다면
            if(this.category.getParent().getParent() != null) {
                mainCategoryName = this.category.getParent().getParent().getName();
            } else {
                // 2차 카테고리까지만 있는 경우
                subCategoryName = this.category.getParent().getName();
            }
        }
        return createCategoryAllName(mainCategoryName, subCategoryName, childCategoryName);
    }

    /**
     * 카테고리 전체 이름 생성
     * @param mainCategoryName 1차 카테고리 이름
     * @param subCategoryName 2차 카테고리 이름
     * @param childCategoryName 3차 카테고리 이름
     * @return 카테고리 전체 이름
     */
    private String createCategoryAllName(String mainCategoryName, String subCategoryName, String childCategoryName) {
        StringBuilder sb = new StringBuilder();
        if (mainCategoryName != null) {
            sb.append(mainCategoryName).append(" > ");
        }
        if (subCategoryName != null) {
            sb.append(subCategoryName).append(" > ");
        }
        sb.append(childCategoryName);
        return sb.toString();
    }

    /**
     * 상품 태그 리스트
     * @return 태그 리스트
     */
    public List<TagDTO> getTagList() {
        return this.productTagList.stream()
                .map(productTag -> TagDTO.from(productTag.getTag())).toList();
    }

    public void changeApprovalStatus(ProductApproval productApproval) {
        this.approvalStatus = productApproval;
    }


    /**
     * 특정 사용자가 이 상품에 좋아요를 눌렀는지 확인
     * @param email 사용자 이메일
     * @return 좋아요 여부
     */
    public boolean isLikedByUser(String email) {
        if (email == null) {
            return false;
        }
        return this.productLikes.stream()
                .anyMatch(like -> like.getMember().getEmail().equals(email));
    }

    /**
     * 상품 엔티티 구현
     * @param dto 상품 Request DTO
     * @param category 카테고리
     * @param shop 샵
     * @return 상품
     */
    public static Product of(ProductDTO.Request dto, Category category, Shop shop) {
        Product product = Product.builder()
                .code(generateRandomNumber(10))
                .category(category)
                .name(dto.getName())
                .price(dto.getPrice())
                .discountPrice(dto.getDiscountPrice())
                .discountRate((int) ((1 - (double) dto.getDiscountPrice() / dto.getPrice()) * 100))
                .description(dto.getDescription())
                .stockNumber(dto.getStockNumber() == null ? 99 : dto.getStockNumber())
                .shop(shop)
                .delFlag(false)
                .build();

        //업로드 처리가 끝난 파일들의 이름 리스트
        List<String> uploadFileNames = dto.getUploadFileNames();

        if (uploadFileNames == null) {
            return product;
        }

        // 이미지 파일 업로드 처리
        uploadFileNames.forEach(product::addImageString);

        return product;
    }
}

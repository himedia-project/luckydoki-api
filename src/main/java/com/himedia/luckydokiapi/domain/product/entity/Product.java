package com.himedia.luckydokiapi.domain.product.entity;

import com.himedia.luckydokiapi.domain.product.enums.*;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import com.himedia.luckydokiapi.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString(exclude = "imageList")
@Table(name = "product")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 상품 코드
    private String code;  // 24343233930

    @Size(max = 255)
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

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'N'")
    private ProductDisplay display;     // 유저단 노출

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'N'")
    private ProductApproval approval;   // 승인

    @NotNull
    @ColumnDefault("1")
    @Column(name = "stock_number", nullable = false)
    private Integer stockNumber;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'N'")
    private ProductIsNew isNew;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'N'")
    private ProductBest best;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'N'")
    private ProductEvent event;

    // 장르
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;      // 마지막 category

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

}

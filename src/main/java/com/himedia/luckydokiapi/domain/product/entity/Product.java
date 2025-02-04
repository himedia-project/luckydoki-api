package com.himedia.luckydokiapi.domain.product.entity;

import com.himedia.luckydokiapi.domain.product.enums.ProductMdPick;
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
    private ProductMdPick mdPick;


    // 장르
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

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

    public void changePrice(Integer price) {
        this.price = price;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changeStockNumber(Integer stockNumber) {
        this.stockNumber = stockNumber;
    }


    public void changeMdPick(ProductMdPick mdPick) {
        this.mdPick = mdPick;
    }

    public void changeCategory(Category category) {
        this.category = category;
    }

}

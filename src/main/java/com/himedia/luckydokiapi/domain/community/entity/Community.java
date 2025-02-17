package com.himedia.luckydokiapi.domain.community.entity;

import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.product.entity.ProductImage;
import com.himedia.luckydokiapi.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "community")
public class Community extends BaseEntity { // 커뮤니티 게시글

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email", nullable = false)
    private Member member;

    @Column(nullable = false, length = 100) // 제목 추가
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ElementCollection
    @Builder.Default
    private List<CommunityImage> imageList = new ArrayList<>();

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL)
    @Builder.Default
    private List<CommunityProduct> communityProductList = new ArrayList<>();

    /**
     * 커뮤니티 게시글 이미지, 커뮤니티에 추가
     * @param image
     */
    public void addImage(CommunityImage image) {
        image.setOrd(this.imageList.size());
        imageList.add(image);
    }

    /**
     * 커뮤니티 이미지 파일, 커뮤니티 이미지에 추가
     *
     * @param fileName 이미지 파일명
     */
    public void addImageString(String fileName) {

        CommunityImage communityImage = CommunityImage.builder()
                .imageName(fileName)
                .build();
        addImage(communityImage);
    }

    /**
     * 커뮤니티 게시글 상품, 커뮤니티에 추가
     * @param communityProduct
     */

    public void addProduct(CommunityProduct product) {
        communityProductList.add(product);
    }


    /**
     * 게시글 이미지 리스트 초기화
     */
    public void clearImageList() {
        this.imageList.clear();
    }

    /**
     * 셀러 샵이미지 반환 if, 단순 유저는 null 반환
     * @return
     */
    public String getSellerShopImage() {
        // 게시글 작성자의 상점 이미지
        return member.getShop() != null ? member.getShop().getImage() : null;
    }
}

package com.himedia.luckydokiapi.domain.shop.entity;


import com.himedia.luckydokiapi.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString(exclude = "imageList")
@Table(name = "community")
public class Community extends BaseEntity {        // 커뮤니티 게시글글

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Builder.Default
    @ElementCollection
    List<CommunityImage> imageList = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String content;



}

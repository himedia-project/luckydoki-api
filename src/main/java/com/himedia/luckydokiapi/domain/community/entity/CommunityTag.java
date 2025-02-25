package com.himedia.luckydokiapi.domain.community.entity;

import com.himedia.luckydokiapi.domain.product.entity.Product;
import com.himedia.luckydokiapi.domain.product.entity.ProductTag;
import com.himedia.luckydokiapi.domain.product.entity.Tag;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Entity
@ToString
@Setter
@Table(name = "community_tag")
@NoArgsConstructor
@AllArgsConstructor
public class CommunityTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public static CommunityTag from(Tag tag, Community community) {
        return CommunityTag.builder()
                .tag(tag)
                .community(community)
                .build();
    }
}
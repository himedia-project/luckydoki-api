package com.himedia.luckydokiapi.domain.community.entity;

import com.himedia.luckydokiapi.domain.member.entity.Member;
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
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 게시글 작성자

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ElementCollection
    @CollectionTable(name = "community_images", joinColumns = @JoinColumn(name = "community_id"))
    @Column(name = "image_url", nullable = true)
    private List<String> imageList = new ArrayList<>();

    @OneToMany
    @Builder.Default
    private List<CommunityProduct> communityProductList = new ArrayList<>();
}

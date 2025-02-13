package com.himedia.luckydokiapi.domain.community.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommunityImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community; // Community와 연결

    @Column(nullable = false)
    private String imageName; // S3 URL 저장

    private Integer ord;


    public void setOrd(int ord){
        this.ord = ord;
    }

    public String getImageName() { // 반드시 있어야 된다고 함!
        return imageName;
    }
}

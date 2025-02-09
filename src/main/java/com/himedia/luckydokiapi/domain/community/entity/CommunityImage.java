package com.himedia.luckydokiapi.domain.community.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommunityImage {

    private String imageName;

    private Integer ord;

    public void setOrd(int ord){
        this.ord = ord;
    }
}

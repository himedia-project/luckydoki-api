package com.himedia.luckydokiapi.domain.product.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImage {

    private String imageName;

    private Integer ord;

    public void setOrd(int ord){
        this.ord = ord;
    }

}

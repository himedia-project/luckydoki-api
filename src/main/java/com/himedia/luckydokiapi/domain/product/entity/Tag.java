package com.himedia.luckydokiapi.domain.product.entity;

import com.himedia.luckydokiapi.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tag", indexes = {
        @Index(name = "idx_tag_name", columnList = "name")
})
@ToString
public class Tag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public static Tag from(String tag) {
        return Tag.builder()
                .name(tag)
                .build();
    }

}

package com.himedia.luckydokiapi.domain.product.entity;


import com.himedia.luckydokiapi.domain.product.enums.LastType;
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
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "category")
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255)
    @Column(name = "logo")
    private String logo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_id")
    private Category parent;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'N'")
    private LastType lastType;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Category> children = new ArrayList<>();


    // 카테고리 브릿지
    @OneToMany(mappedBy = "category")
    @Builder.Default
    private List<CategoryBridge> categoryBridgeList = new ArrayList<>();


    public void changeLogo(String logo) {
        this.logo = logo;
    }

}
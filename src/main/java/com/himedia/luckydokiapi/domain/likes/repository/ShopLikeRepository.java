package com.himedia.luckydokiapi.domain.likes.repository;

import com.himedia.luckydokiapi.domain.likes.entity.ShopLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopLikeRepository extends JpaRepository<ShopLike, Long> {
}

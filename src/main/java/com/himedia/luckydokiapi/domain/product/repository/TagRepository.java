package com.himedia.luckydokiapi.domain.product.repository;


import com.himedia.luckydokiapi.domain.product.entity.Tag;
import com.himedia.luckydokiapi.domain.product.repository.querydsl.TagRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends JpaRepository<Tag, Long>, TagRepositoryCustom {

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Tag t WHERE t.name = :tag")
    boolean existsByName(@Param("tag") String tag);

    @Query("SELECT t FROM Tag t WHERE t.name = :tag")
    Tag findByName(@Param("tag") String tag);
}

package com.himedia.luckydokiapi.domain.likes.repository;

import com.himedia.luckydokiapi.domain.likes.dto.LikesProductDTO;
import com.himedia.luckydokiapi.domain.likes.entity.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
    boolean existsByMember_EmailAndProduct_Id(String email, Long id);

@Modifying
@Transactional
    @Query("delete from ProductLike pl where pl.product.id =:id and pl.member.email =:email")
    void deleteByEmailAndProductId(@Param("email") String email, @Param("id") Long id);


    @Query("select m from ProductLike m where m.member.email =:email")
    List<ProductLike> findByEmail(@Param("email") String email);
}

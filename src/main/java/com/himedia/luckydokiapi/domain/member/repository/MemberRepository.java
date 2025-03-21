package com.himedia.luckydokiapi.domain.member.repository;


import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.repository.querydsl.MemberRepositoryCustom;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String>
        , MemberRepositoryCustom {

    @EntityGraph(attributePaths = {"memberRoleList"})
    @Query("select m from Member m where m.email = :email")
    Optional<Member> getWithRoles(@Param("email") String email);

    @Query("select m from Member m where m.email = :email")
    Optional<Member> findByEmail(@Param("email") String email);

    @Query("select case when count(m) > 0 then true else false end from Member m where m.email = :email")
    Boolean existsByEmail(@Param("email") String email);

//    @Query("select m from Member m where m.nickName = :nickName")
//    Optional<Member> findByNickName(@Param("nickName") String nickName);
}

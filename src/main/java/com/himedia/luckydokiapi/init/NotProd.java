package com.himedia.luckydokiapi.init;

import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.enums.MemberRole;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;


@Slf4j
@Configuration
@Profile({"!prod"})
@RequiredArgsConstructor
public class NotProd {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    @Bean
    public CommandLineRunner init() {
        return (args) -> {
            log.info("init data...");

            // 데이터가 하나라도 있으면 초기화하지 않음
            if (memberRepository.count() > 0 ) {
                log.info("이미 초기 데이터가 존재합니다.");
                return;
            } else {
                log.info("초기 데이터가 없어 초기화합니다.");
                Member member1 = Member.builder()
                        .email("user@test.com")
                        .name("user")
                        .password(passwordEncoder.encode("1234"))
                        .phone("010-1234-5678")
                        .delFlag(false)
                        .build();

                member1.addRole(MemberRole.USER);

                Member member2 = Member.builder()
                        .email("seller@test.com")
                        .name("seller")
                        .password(passwordEncoder.encode("1234"))
                        .phone("010-1234-5678")
                        .delFlag(false)
                        .build();

                member2.addRole(MemberRole.SELLER);

                Member member3 = Member.builder()
                        .email("admin@test.com")
                        .name("admin")
                        .password(passwordEncoder.encode("1234"))
                        .phone("010-1234-5678")
                        .delFlag(false)
                        .build();

                member3.addRole(MemberRole.ADMIN);

                memberRepository.saveAll(List.of(member1, member2, member3));
                log.info("Member 초기 데이터 생성 완료");
            }

        };
    }
}

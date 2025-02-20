package com.himedia.luckydokiapi.init;

import com.himedia.luckydokiapi.domain.coupon.service.CouponService;
import com.himedia.luckydokiapi.domain.member.entity.Member;
import com.himedia.luckydokiapi.domain.member.enums.MemberActive;
import com.himedia.luckydokiapi.domain.member.enums.MemberRole;
import com.himedia.luckydokiapi.domain.member.enums.PushActive;
import com.himedia.luckydokiapi.domain.member.repository.MemberRepository;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import com.himedia.luckydokiapi.domain.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Configuration
@Profile({"!prod"})
@RequiredArgsConstructor
public class NotProd {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private final ShopRepository shopRepository;

    private final CouponService couponService;


    @Bean
    public CommandLineRunner init() {
        return (args) -> {
            log.info("init data...");

            if(couponService.countAllCoupon() > 0) {
                log.info("이미 초기 쿠폰 데이터가 존재합니다.");
                return;
            } else {
                log.info("초기 쿠폰 데이터가 없어 초기화합니다.");
                couponService.createWelcomeCoupon();
                log.info("Coupon 초기 데이터 생성 완료");
            }

            // 데이터가 하나라도 있으면 초기화하지 않음
            if (memberRepository.count() > 0) {
                log.info("이미 초기 데이터가 존재합니다.");
                return;
            } else {
                log.info("초기 데이터가 없어 초기화합니다.");
                List<Member> members = new ArrayList<>();

                // Create 10 users
                for (int i = 1; i <= 10; i++) {
                    Member user = Member.builder()
                            .email("user" + i + "@test.com")
                            .nickName("user" + i)
                            .profileImage("s_3f0b0873-b2e5-48d0-94e1-f72e5b9c75a5-luckydoki_favicon.png")
                            .password(passwordEncoder.encode("1234"))
                            .phone("010-1234-5678")
                            .active(MemberActive.Y)
                            .pushActive(PushActive.Y)
                            .build();
                    user.addRole(MemberRole.USER);
                    members.add(user);
                }

                // Create 10 sellers
                for (int i = 1; i <= 10; i++) {
                    Member seller = Member.builder()
                            .email("seller" + i + "@test.com")
                            .nickName("seller" + i)
                            .profileImage("s_3f0b0873-b2e5-48d0-94e1-f72e5b9c75a5-luckydoki_favicon.png")
                            .password(passwordEncoder.encode("1234"))
                            .phone("010-1234-5678")
                            .active(MemberActive.Y)
                            .pushActive(PushActive.Y)
                            .build();
                    seller.addRole(MemberRole.SELLER);
                    members.add(seller);
                }

                // Create admin
                Member admin = Member.builder()
                        .email("admin@test.com")
                        .nickName("admin")
                        .profileImage("s_3f0b0873-b2e5-48d0-94e1-f72e5b9c75a5-luckydoki_favicon.png")
                        .password(passwordEncoder.encode("1234"))
                        .phone("010-1234-5678")
                        .active(MemberActive.Y)
                        .pushActive(PushActive.Y)
                        .build();
                admin.addRole(MemberRole.ADMIN);
                members.add(admin);

                memberRepository.saveAll(members);


                // Shop 초기 데이터 생성
                List<Shop> shops = new ArrayList<>();

                for (int i = 1; i <= 10; i++) {
                    Shop shop = Shop.builder()
                            .member(memberRepository.findByEmail("seller" + i + "@test.com").get())
                            .image("s_3f00c99b-5252-4393-93f0-faf94cd999c7-seller.jpg")
                            .introduction("seller" +i + "의 세상에 오신 걸 환영합니다!")
                            .build();
                    shops.add(shop);
                }

                // 쿠폰 회원가입 쿠폰 발급
                couponService.issueCoupon(1L, members.stream().map(Member::getEmail).toList());

                shopRepository.saveAll(shops);
                log.info("Welcome Coupon, Member, Shop 초기 데이터 생성 완료");
            }

        };
    }
}

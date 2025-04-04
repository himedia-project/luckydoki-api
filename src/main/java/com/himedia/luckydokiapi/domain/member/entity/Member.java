package com.himedia.luckydokiapi.domain.member.entity;


//import com.himedia.luckydokiapi.domain.chat.entity.ChatRoom;

import com.himedia.luckydokiapi.domain.chat.entity.ChatRoom;
import com.himedia.luckydokiapi.domain.community.entity.Community;
import com.himedia.luckydokiapi.domain.coupon.entity.CouponRecord;
import com.himedia.luckydokiapi.domain.coupon.enums.CouponStatus;
import com.himedia.luckydokiapi.domain.member.dto.JoinRequestDTO;
import com.himedia.luckydokiapi.domain.member.enums.MemberActive;
import com.himedia.luckydokiapi.domain.member.enums.MemberRole;
import com.himedia.luckydokiapi.domain.member.enums.PushActive;
import com.himedia.luckydokiapi.domain.member.enums.ShopApproved;
import com.himedia.luckydokiapi.domain.order.entity.Order;
import com.himedia.luckydokiapi.domain.order.entity.OrderItem;
import com.himedia.luckydokiapi.domain.review.entity.Review;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import com.himedia.luckydokiapi.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;

@DynamicUpdate
@SuperBuilder
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString(exclude = {"shop", "memberRoleList", "communityList", "couponRecordList", "sellerApplicationList", "orderList", "reviewList"})
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    private String email;
    private String nickName;
    private Long birthday;
    private String profileImage;
    private String password;
    private String phone;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'Y'")
    @Column(nullable = false)
    private MemberActive active;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'Y'")
    private PushActive pushActive;

    @Column
    private String fcmToken;


    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "member_role_list", joinColumns = @JoinColumn(name = "email"))
    @Column(name = "role") // 해당 memberRoleList 를 저장할 컬럼명을 지정
    @Builder.Default
    private List<MemberRole> memberRoleList = new ArrayList<>();


    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Community> communityList = new ArrayList<>();

    // 쿠폰 record list 추가
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @Builder.Default
    private List<CouponRecord> couponRecordList = new ArrayList<>();

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Shop shop;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Order> orderList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Review> reviewList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @Builder.Default
    private List<SellerApplication> sellerApplicationList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<ChatRoom> chatRooms = new ArrayList<>();
//
//    @Builder.Default
//    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL)
//    private List<ChatRoom> buyerChatRooms = new ArrayList<>();

    public void addRole(MemberRole memberRole) {
        memberRoleList.add(memberRole);
    }

    public void changeRole(MemberRole role) {
        this.memberRoleList.clear();
        this.memberRoleList.add(role);
    }

    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }

    public void updatePhone(String phone) {
        this.phone = phone;
    }

    /**
     * 회원에게 발급된 쿠폰 수
     */
    public Long getActiveCouponCount() {
        return couponRecordList.stream()
                .filter(couponRecord -> couponRecord.getCoupon().getStatus() == CouponStatus.ISSUED)
                .count();
    }

    public void deactivate() {
        this.active = MemberActive.N;
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    // 일반 엔티티 정적 팩토리 메서드
    public static Member from(JoinRequestDTO request) {
        Member member = Member.builder()
                .email(request.getEmail())
                .nickName(request.getNickName())
                .password(request.getPassword())
                .birthday(request.getBirthday())
                .active(MemberActive.Y)
                .pushActive(PushActive.Y)
                .profileImage("s_2b3b4d26-7b17-4426-90d7-310976988d2e-luckydoki_favicon.png")
                .phone(request.getPhone())
                .build();

        member.addRole(MemberRole.USER);
        return member;

    }

    /**
     * 소셜 맴버 엔티티 정적 팩토리 메서드
     *
     * @param email
     * @param tempPassword
     * @return 소셜 맴버 엔티티
     */
    public static Member fromSocialMember(String email, String tempPassword) {
        Member member = Member.builder()
                .email(email)
                .password(tempPassword)
                .nickName("소셜회원")
                .profileImage("s_2b3b4d26-7b17-4426-90d7-310976988d2e-luckydoki_favicon.png")
                .active(MemberActive.Y)
                .pushActive(PushActive.Y)
                .build();
        member.addRole(MemberRole.USER);
        return member;
    }

    /**
     * 셀러 신청여부
     * @return 셀러 신청 여부
     */
    public boolean getSellerRequested() {
        return !sellerApplicationList.isEmpty();
    }

    /**
     * 월간 구매액
     * @return 월간 구매액
     */
    public Long getMonthlyPurchase() {
        return orderList.stream()
                .filter(order -> order.getOrderDate().isAfter(now().minusMonths(1)))
                .mapToLong(Order::getTotalPrice)
                .sum();
    }

    /**
     * 월간 판매액
     * 셀러가 등록한 상품 중 최근 한달간 판매된 상품의 총액
     * @return 월간 판매액
     */
    public Long getMonthlySales() {
        if(this.getShop() == null) {
            return 0L;
        }
        return this.getShop().getProductList().stream()
                .filter(product -> product.getOrderItems().stream()
                        .anyMatch(orderItem -> orderItem.getOrder().getOrderDate().isAfter(now().minusMonths(1))))
                .mapToLong(product -> product.getOrderItems().stream()
                        .filter(orderItem -> orderItem.getOrder().getOrderDate().isAfter(now().minusMonths(1)))
                        .mapToLong(OrderItem::getTotalPrice)
                        .sum())
                .sum();
    }


    /**
     * 리뷰 등록 수
     * @return 리뷰 등록 수
     */
    public Long getReviewCount() {
        return (long) reviewList.size();
    }

    public void changeProfileImage(String uploadedImageName) {
        this.profileImage = uploadedImageName;
    }
}


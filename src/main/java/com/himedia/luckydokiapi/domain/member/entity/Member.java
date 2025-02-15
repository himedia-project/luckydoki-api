package com.himedia.luckydokiapi.domain.member.entity;


//import com.himedia.luckydokiapi.domain.chat.entity.ChatRoom;

import com.himedia.luckydokiapi.domain.community.entity.Community;
import com.himedia.luckydokiapi.domain.coupon.entity.CouponRecord;
import com.himedia.luckydokiapi.domain.coupon.enums.CouponStatus;
import com.himedia.luckydokiapi.domain.member.enums.MemberActive;
import com.himedia.luckydokiapi.domain.member.enums.MemberRole;
import com.himedia.luckydokiapi.domain.member.enums.PushActive;
import com.himedia.luckydokiapi.domain.shop.entity.Shop;
import com.himedia.luckydokiapi.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;


@SuperBuilder
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString(exclude = {"shop", "memberRoleList", "communityList", "couponRecordList"})
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    private String email;
    private String nickName;
    private Long birthday;
    private String profileImage;
    private String password;
    private String phone;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'Y'")
    private MemberActive active;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'Y'")
    private PushActive pushActive;


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

//    @Builder.Default
//    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
//    private List<ChatRoom> sellerChatRooms = new ArrayList<>();
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
     * 셀러로 등록
     * @param shop 셀러로 등록할 shop
     */
    public void addRoleAndShop(Shop shop) {
        this.addRole(MemberRole.SELLER);
        this.shop = shop;
    }

    /**
     * 회원에게 발급된 쿠폰 수
     */
    public Long getActiveCouponCount() {
        return couponRecordList.stream()
                .filter(couponRecord -> couponRecord.getCoupon().getStatus() == CouponStatus.ISSUED)
                .count();
    }
}


package com.himedia.luckydokiapi.domain.member.entity;


//import com.himedia.luckydokiapi.domain.chat.entity.ChatRoom;
import com.himedia.luckydokiapi.domain.community.entity.Community;
import com.himedia.luckydokiapi.domain.member.enums.MemberActive;
import com.himedia.luckydokiapi.domain.member.enums.MemberRole;
import com.himedia.luckydokiapi.domain.member.enums.PushActive;
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
@ToString(exclude = {"memberRoleList"})
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    private String email;
    private String nickName;
    private Long birthday;
    private String profileImage;
    private String password;
    private String phone;

    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }

    public void updatePhone(String phone) {
        this.phone = phone;
    }

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

}

package com.himedia.luckydokiapi.security;

import com.himedia.luckydokiapi.domain.member.enums.MemberActive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
public class MemberDTO extends User {

    private String email;
    private String password;
    private String phone;
    private String nickName;
    private List<String> roleNames = new ArrayList<>();
    private MemberActive active;



    public MemberDTO(String email, String password, String phone,String nickName, List<String> roleNames, MemberActive active) {
        // ROLE_ 접두사를 붙여서 권한을 부여
        super(email, password, roleNames.stream().map(str -> new SimpleGrantedAuthority("ROLE_" + str)).collect(Collectors.toList()));
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.nickName = nickName;
        this.roleNames = roleNames;
        this.active = active;
    }

    public Map<String, Object> getClaims() {

        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("email", this.email);
        dataMap.put("password", this.password);
        dataMap.put("phone", this.phone);
        dataMap.put("nickName", this.nickName);
        dataMap.put("roleNames", this.roleNames);
        dataMap.put("active", this.active != null ? this.active.name() : "Y");

        return dataMap;
    }

}

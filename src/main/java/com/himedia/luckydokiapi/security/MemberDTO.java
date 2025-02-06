package com.himedia.luckydokiapi.security;

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
    private String nickName;
    private List<String> roleNames = new ArrayList<>();


    public MemberDTO(String email, String password, String nickName, List<String> roleNames) {
        // ROLE_ 접두사를 붙여서 권한을 부여
        super(email, password, roleNames.stream().map(str -> new SimpleGrantedAuthority("ROLE_" + str)).collect(Collectors.toList()));
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.roleNames = roleNames;
    }

    public Map<String, Object> getClaims() {

        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("email", this.email);
        dataMap.put("password", this.password);
        dataMap.put("nickName", this.nickName);
        dataMap.put("roleNames", this.roleNames);

        return dataMap;
    }

}

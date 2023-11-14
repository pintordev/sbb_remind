package com.pintor.sbb_remind.security;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum MemberAuthority {

    ADMIN("admin", 0, "관리자"),
    MEMBER("member", 1, "회원");

    private String type;
    private Integer code;
    private String typeKor;
    private static final Map<Integer, String> codeToType = Stream.of(values()).collect(Collectors.toMap(MemberAuthority::getCode, MemberAuthority::getType));
    private static final Map<String, Integer> typeToCode = Stream.of(values()).collect(Collectors.toMap(MemberAuthority::getType, MemberAuthority::getCode));
    private static final Map<String, String> typeToTypeKor = Stream.of(values()).collect(Collectors.toMap(MemberAuthority::getType, MemberAuthority::getTypeKor));

    MemberAuthority(String type, Integer code, String typeKor) {
        this.type = type;
        this.code = code;
        this.typeKor = typeKor;
    }

    public Integer getDecCode() {
        return (int) Math.pow(2, this.code);
    }

    public static Integer getDecCodeByType(String type) {
        return (int) Math.pow(2, typeToCode.get(type));
    }

    public static String getTypeByCode(int code) {
        return codeToType.get(code);
    }

    public static String getTypeKorByType(String type) {
        return typeToTypeKor.get(type);
    }
}

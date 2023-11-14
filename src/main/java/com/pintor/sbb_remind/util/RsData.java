package com.pintor.sbb_remind.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RsData<T> {

    private String code;

    private String message;

    private T data;
}

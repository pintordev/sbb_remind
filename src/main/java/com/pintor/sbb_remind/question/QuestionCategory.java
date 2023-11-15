package com.pintor.sbb_remind.question;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum QuestionCategory {

    FREE("자유", 0),
    QUESTION("질문", 1);

    private String type;
    private Integer code;

    private static final Map<Integer, String> codeToType = Stream.of(values()).collect(Collectors.toMap(QuestionCategory::getCode, QuestionCategory::getType));
    private static final Map<String, Integer> typeToCode = Stream.of(values()).collect(Collectors.toMap(QuestionCategory::getType, QuestionCategory::getCode));

    QuestionCategory(String type, Integer code) {
        this.type = type;
        this.code = code;
    }

    public static List<String> getList() {
        List<String> questionCategoryList = new ArrayList<>();
        for (QuestionCategory questionCategory : QuestionCategory.values()) {
            questionCategoryList.add(questionCategory.getType());
        }
        return questionCategoryList;
    }

    public static String getTypeByCode(Integer code) {
        return codeToType.get(code);
    }

    public static Integer getCodeByType(String type) {
        return typeToCode.get(type);
    }
}

package com.wuwei.watermark.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class TextContentFieldUtils {
    private TextContentFieldUtils() {
    }

    /**
     * 获取文字内容模版中的字段
     */
    public static List<String> getField(String textContent) {
        if (StringUtils.isBlank(textContent)) {
            return Collections.emptyList();
        }
        if (!validateTextContent(textContent)) {
            return Collections.emptyList();
        }
        if (!textContent.contains("{")) {
            return Collections.emptyList();
        }

        List<String> fieldList = new ArrayList<>();
        int startLeftIndex = 0;
        int startRightIndex = 0;

        while (((startLeftIndex = textContent.indexOf("{", startLeftIndex)) != -1)
                && ((startRightIndex = textContent.indexOf("}", startRightIndex)) != -1)
        ) {
            startLeftIndex += 1;
            fieldList.add(textContent.substring(startLeftIndex, startRightIndex));
            startRightIndex += 1;
        }

        fieldList = fieldList.stream().distinct().collect(toList());
        return fieldList;
    }

    /**
     * 校验是否合法的文字内容模版
     */
    public static boolean validateTextContent(String textContent) {
        if (StringUtils.isBlank(textContent)) {
            return true;
        }

        char[] charArray = textContent.toCharArray();
        Deque<Character> charDeque = new ArrayDeque<>();
        for (char word : charArray) {
            boolean isTargetChar = word == '{' || word == '}';
            if (isTargetChar) {
                if (charDeque.isEmpty()) {
                    charDeque.push(word);
                } else {
                    Character topChar = charDeque.peek();
                    if (topChar == '{' && word == '}') {
                        charDeque.pop();
                    } else {
                        break;
                    }
                }
            }
        }

        return charDeque.isEmpty();
    }


}

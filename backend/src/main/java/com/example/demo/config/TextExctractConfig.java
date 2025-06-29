package com.example.demo.config;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextExctractConfig {

    public static Map<String, String> parseSimpleJsonLikeString(String text) {
        Map<String, String> resultMap = new HashMap<>();

        // 정규식 패턴 - "Key": "Value"
        Pattern pattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            resultMap.put(key, value);
        }

        return resultMap;
    }
}

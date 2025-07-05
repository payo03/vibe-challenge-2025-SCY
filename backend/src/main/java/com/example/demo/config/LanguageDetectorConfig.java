package com.example.demo.config;

import com.github.pemistahl.lingua.api.*;

import java.util.Arrays;
import java.util.List;

public class LanguageDetectorConfig {

    private LanguageDetectorConfig() {}     // Prevent instantiation
    private static final LanguageDetector detector;

    static {
        List<Language> languages = Arrays.asList(
                Language.KOREAN,
                Language.ENGLISH,
                Language.JAPANESE,
                Language.CHINESE,
                Language.FRENCH,
                Language.SPANISH
        );

        detector = LanguageDetectorBuilder.fromLanguages(languages.toArray(new Language[0]))
                .withPreloadedLanguageModels()
                .build();
    }

    // 언어감지 함수
    public static String detectLanguage(String text) {
        if (text == null || text.trim().isEmpty()) return "ko";

        Language lang = detector.detectLanguageOf(text);
        return lang.getIsoCode639_1().toString();
    }
}

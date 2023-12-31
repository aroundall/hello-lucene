package io.amuji;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class LangPicker {

    private final List<String> chineseWords = new ArrayList<>();
    private final List<String> englishWords = new ArrayList<>();

    public LangPicker(String inputString) {

        Pattern chinesePattern = Pattern.compile("[\\u4E00-\\u9FA5]+");
        Pattern englishPattern = Pattern.compile("\\b[A-Za-z1-9&\\-]+\\b");
        Matcher chineseMatcher = chinesePattern.matcher(inputString);
        Matcher englishMatcher = englishPattern.matcher(inputString);

        while (englishMatcher.find()) {
            englishWords.add(englishMatcher.group());
        }

        while (chineseMatcher.find()) {
            chineseWords.add(chineseMatcher.group());
        }

        log.info("Picked English words: {}", this.englishWords);
        log.info("Picked Chinese words: {}", this.chineseWords);

    }

    public List<String> getChineseWords() {
        return chineseWords;
    }

    public List<String> getEnglishWords() {
        return englishWords;
    }
}

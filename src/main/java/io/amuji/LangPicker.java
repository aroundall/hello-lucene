package io.amuji;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LangPicker {

    private final List<String> chineseWords = new ArrayList<>();
    private final List<String> englishWords = new ArrayList<>();

    public LangPicker(String inputString) {

        // Regular expression pattern for Chinese characters
        String chinesePattern = "[\\u4E00-\\u9FA5]+";

        // Regular expression pattern for English words
        String englishPattern = "\\b[A-Za-z]+\\b";

        // Create the pattern objects
        Pattern chineseRegex = Pattern.compile(chinesePattern);
        Pattern englishRegex = Pattern.compile(englishPattern);

        // Create the matchers
        Matcher chineseMatcher = chineseRegex.matcher(inputString);
        Matcher englishMatcher = englishRegex.matcher(inputString);

        while (englishMatcher.find()) {
            englishWords.add(englishMatcher.group());
        }

        while (chineseMatcher.find()) {
            chineseWords.add(chineseMatcher.group());
        }

    }

    public List<String> getChineseWords() {
        return chineseWords;
    }

    public List<String> getEnglishWords() {
        return englishWords;
    }
}

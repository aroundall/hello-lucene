package io.amuji;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class Search {
    private List<String> categories = new ArrayList<>();
    private String keywords;
    private final List<Keyword> normalizedKeywords = new ArrayList<>();


    public boolean isBlank() {
        if (this.hasCategories()) {
            return false;
        }

        if (this.hasKeywords()) {
            return false;
        }

        return true;
    }

    public boolean hasKeywords() {
        return StringUtils.isNotBlank(this.keywords);
    }


    public void normalizedKeywords(List<String> normalizedKeywords) {
        this.normalizedKeywords.clear();
        normalizedKeywords.forEach(keyword -> this.normalizedKeywords.add(new Keyword(keyword)));
    }

    public List<Keyword> normalizedKeywords() {
        return this.normalizedKeywords;
    }

    public Search addCategory(String category) {
        Objects.requireNonNull(category);
        this.categories.add(category);
        return this;
    }

    public boolean hasCategories() {
        return !this.categories.isEmpty();
    }

    public List<String> getCategories() {
        return categories;
    }

    public Search setCategories(List<String> categories) {
        this.categories = categories;
        return this;
    }

    public String getKeywords() {
        return keywords;
    }

    public Search setKeywords(String keywords) {
        this.keywords = keywords;
        return this;
    }

    public static class Keyword {
        private static final Pattern chinesePattern = Pattern.compile("[\\u4E00-\\u9FA5]+");

        @Getter
        private final String value;
        private boolean isChinese;


        public Keyword(String value) {
            Objects.requireNonNull(value);
            this.value = value;
            detectLang();
        }

        private void detectLang() {
            this.isChinese = chinesePattern.matcher(this.value).matches();
        }

        public boolean isChinese() {
            return this.isChinese;
        }

    }
}

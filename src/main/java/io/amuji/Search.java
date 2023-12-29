package io.amuji;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Search {
    private List<String> categories = new ArrayList<>();
    private String keywords;
    private String englishKeywords;
    private String chineseKeywords;
    private boolean picked = false;


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

    public boolean hasKeywordsEn() {
        return StringUtils.isNotBlank(getKeywordsEn());
    }

    public String getKeywordsEn() {
        if (!this.hasKeywords()) {
            return null;
        }

        pickKeywords();

        return this.englishKeywords;
    }

    public boolean hasKeywordsZh() {
        return StringUtils.isNotBlank(getKeywordsZh());
    }

    public String getKeywordsZh() {
        if (!this.hasKeywords()) {
            return null;
        }

        pickKeywords();

        return this.chineseKeywords;
    }

    private void pickKeywords() {
        if (!picked) {
            LangPicker langPicker = new LangPicker(this.keywords);
            this.englishKeywords = String.join(" ", langPicker.getEnglishWords());
            this.chineseKeywords = String.join("", langPicker.getChineseWords());
            picked = true;
        }
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
}

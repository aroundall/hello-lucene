package io.amuji;

import com.google.common.base.Joiner;
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

    public boolean hasEnglishKeywords() {
        return StringUtils.isNotBlank(getEnglishKeywords());
    }

    public String getEnglishKeywords() {
        pickKeywords();

        return this.englishKeywords;
    }

    public boolean hasChineseKeywords() {
        return StringUtils.isNotBlank(getChineseKeywords());
    }

    public String getChineseKeywords() {
        pickKeywords();

        return this.chineseKeywords;
    }

    private void pickKeywords() {
        if (picked) {
            return;
        }

        if (StringUtils.isNotBlank(this.keywords)) {
            LangPicker langPicker = new LangPicker(this.keywords);
            this.englishKeywords = Joiner.on(" ").skipNulls().join(langPicker.getEnglishWords());
            this.chineseKeywords = Joiner.on(" ").skipNulls().join(langPicker.getChineseWords());
        }
        picked = true;
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

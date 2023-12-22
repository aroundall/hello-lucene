package io.amuji;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Search {
    private List<String> categories = new ArrayList<>();
    private String keywords;

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
        return Objects.nonNull(this.keywords);
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

package io.amuji;

import java.util.List;

public class Search {
    private List<String> categories;
    private String keywords;

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

package io.amuji;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static io.amuji.Fields.*;
import static org.apache.lucene.search.BooleanClause.Occur.MUST;
import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;

public class SearchRequest {
    @Setter
    @Getter
    private List<String> categories = new ArrayList<>();

    @Getter
    private String keywords;
    private final List<Keyword> normalizedKeywords = new ArrayList<>();

    public SearchRequest() {
    }

    public SearchRequest(String keywords) {
        setKeywords(keywords);
    }


    public Query getQuery() {
        if (this.isBlank()) {
            throw new RuntimeException("Empty search request, cannot build query.");
        }

        BooleanQuery.Builder querybuilder = new BooleanQuery.Builder();
        if (this.hasKeywords()) {
            querybuilder.add(buildKeywordsQuery(), MUST);
        }

        if (this.hasCategories()) {
            querybuilder.add(buildCategoryQuery(), MUST);
        }

        return querybuilder.build();
    }

    private Query buildKeywordsQuery() {
        BooleanQuery.Builder keywordsBuilder = new BooleanQuery.Builder();

        for (SearchRequest.Keyword keyword : this.normalizedKeywords) {
            if (keyword.isChinese()) {
                keywordsBuilder.add(new TermQuery(new Term(FIELD_NAME_FORM_NAME_CN, keyword.getValue())), SHOULD);
                keywordsBuilder.add(new TermQuery(new Term(FIELD_NAME_FORM_NAME_TC, keyword.getValue())), SHOULD);
            } else {
                keywordsBuilder.add(new FuzzyQuery(new Term(FIELD_NAME_FORM_NAME, keyword.getValue())), SHOULD);
            }
        }

        return keywordsBuilder.build();
    }

    private Query buildCategoryQuery() {
        BooleanQuery.Builder catQueryBuilder = new BooleanQuery.Builder();

        this.getCategories().forEach(category ->
                catQueryBuilder.add(new TermQuery(new Term(FIELD_NAME_CAT_ID, category)), SHOULD));

        return catQueryBuilder.build();
    }

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
        return !this.normalizedKeywords.isEmpty();
    }

    public SearchRequest addCategory(String category) {
        Objects.requireNonNull(category);
        this.categories.add(category);
        return this;
    }

    public boolean hasCategories() {
        return !this.categories.isEmpty();
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
        this.normalizedKeywords.clear();
        normalizeKeywords(this.keywords).forEach(
                keyword -> this.normalizedKeywords.add(new Keyword(keyword)));
    }

    private List<String> normalizeKeywords(String keywords) {
        List<String> normalized = new ArrayList<>();

        if (StringUtils.isBlank(keywords)) {
            return normalized;
        }

        Analyzer analyzer = AnalyzerFactory.getInstance().getKeywordsAnalyzer();
        try (TokenStream tokenStream = analyzer.tokenStream("any-filed", keywords)) {
            CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

            tokenStream.reset(); // Reset the token stream to the beginning

            while (tokenStream.incrementToken()) {
                String analyzedWord = charTermAttribute.toString();
                normalized.add(analyzedWord);
            }

            tokenStream.end(); // Perform end-of-stream operations
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return normalized;
    }

    private static class Keyword {
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

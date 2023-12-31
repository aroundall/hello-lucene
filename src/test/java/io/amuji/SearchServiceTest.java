package io.amuji;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

class SearchServiceTest {

    private SearchService searchService;

    @BeforeEach
    void setUp() {
        searchService = new SearchService();
    }

    @Test
    void matched_keyword_should_have_result() {
        List<Request> matched = searchService.search(new Search().setKeywords("Address"));

        assertThat(matched).hasSize(2);
    }

    @Test
    void matched_term_query_should_have_result() {
        List<Request> matched = searchService.search(new Search().setKeywords("Certificate"));

        assertThat(matched).hasSize(1);
    }

    @Test
    void matched_simplified_chinese_keyword_should_have_result() {
        List<Request> matched = searchService.search(new Search().setKeywords("会面"));

        assertThat(matched).hasSize(1);
    }

    @Test
    void matched_category_should_return_result() {
        List<Request> matched = searchService.search(new Search()
                .addCategory("CG123456789"));
        assertThat(matched).isNotEmpty();
        assertThat(matched.stream().allMatch(request -> request.getCategoryId().equalsIgnoreCase("CG123456789"))).isTrue();
    }

    @Test
    void matched_categories_should_return_result() {
        List<Request> matched = searchService.search(new Search()
                .addCategory("CG123456789")
                .addCategory("CG234567890"));
        assertThat(matched).isNotEmpty();
        assertThat(matched.stream().allMatch(request -> {
            String catId = request.getCategoryId();
            return catId.equalsIgnoreCase("CG123456789") || catId.equalsIgnoreCase("CG234567890");
        })).isTrue();
    }


    @Test
    void matched_keyword_and_keyword_should_return_result() {
        List<Request> matched = searchService.search(new Search().addCategory("CG567890123").setKeywords("Procedure"));
        assertThat(matched).hasSize(1);
    }

    @Test
    void matched_keywords_should_return_result() {
        List<Request> matched = searchService.search(new Search()
                .setKeywords("Mortgage Payment"));
        assertThat(matched).isNotEmpty();
    }


    @Test
    void matched_categories_and_keywords_should_return_result() {
        List<Request> matched = searchService.search(new Search()
                .addCategory("CG345678901")
                .addCategory("CG345678901")
                .setKeywords("Mortgage Payment"));
        assertThat(matched).hasSize(2);
    }

    @Test
    void matched_traditional_chinese_should_return_result() {
        List<Request> matched = searchService.search(new Search()
                .setKeywords("貸款還款"));
        assertThat(matched).hasSize(4);

        matched = searchService.search(new Search()
                .setKeywords("贷款还款"));
        assertThat(matched).hasSize(4);
    }

    @Test
    void matched_multiple_chinese_words_should_return_result() {
        List<Request> matched = searchService.search(new Search()
                .setKeywords("贈與未成年人法案"));
        assertThat(matched).hasSize(4);
    }

    @Test
    void matched_fuzzy_query_should_have_result() {
        List<Request> matched = searchService.search(new Search().setKeywords("hallowen"));

        assertThat(matched).hasSize(1);
    }

    @Test
    void matched_fuzzy_en_mixed_zh_words_should_have_result() {

        Set<String> expectedFormIds = Stream.of(
                        searchService.search(new Search().setKeywords("Unform")).stream(),
                        searchService.search(new Search().setKeywords("acount")).stream(),
                        searchService.search(new Search().setKeywords("未成年人")).stream(),
                        searchService.search(new Search().setKeywords("法案")).stream())
                .flatMap(Function.identity())
                .map(Request::getFormId).collect(toSet());


        List<Request> matched = searchService.search(new Search().setKeywords("Unform acount 未成年人法案"));

        assertThat(matched.stream().map(Request::getFormId).collect(toSet())).isEqualTo(expectedFormIds);
        assertThat(matched)
                .isNotEmpty()
                .allMatch(request -> {
                    String formName = request.getFormName().toLowerCase();
                    String formNameCN = request.getFormNameCN();
                    return formName.contains("uniform")
                            || formName.contains("form")
                            || formName.contains("account")
                            || formName.contains("about")
                            || formNameCN.contains("未成年人")
                            || formNameCN.contains("法案");
                });
    }
}
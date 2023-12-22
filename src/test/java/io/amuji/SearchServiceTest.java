package io.amuji;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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
    void matched_chinese_keyword_should_have_result() {
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
}
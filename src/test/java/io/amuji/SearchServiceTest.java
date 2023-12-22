package io.amuji;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class SearchServiceTest {
    @Test
    void matched_keyword_should_have_result() {
        SearchService searchService = new SearchService();
        List<Request> matched = searchService.search(new Search().setKeywords("Address"));

        Assertions.assertThat(matched).hasSize(2);
    }

    @Test
    void matched_term_query_should_have_result() {
        SearchService searchService = new SearchService();
        List<Request> matched = searchService.search(new Search().setKeywords("Certificate"));

        Assertions.assertThat(matched).hasSize(1);
    }

    @Test
    void matched_chinese_keyword_should_have_result() {
        SearchService searchService = new SearchService();
        List<Request> matched = searchService.search(new Search().setKeywords("会面"));

        Assertions.assertThat(matched).hasSize(1);
    }
}
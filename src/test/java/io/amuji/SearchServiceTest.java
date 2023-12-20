package io.amuji;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class SearchServiceTest {
    @Test
    void matched_keyword_should_have_result() {
        SearchService searchService = new SearchService();
        List<Request> investForms = searchService.search("Address");

        Assertions.assertThat(investForms).hasSize(2);
    }
}
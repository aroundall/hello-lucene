package io.amuji;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class SearchServiceTest {
    @Test
    void matched_keyword_should_have_result() {
        SearchService searchService = new SearchService();
        List<Request> matched = searchService.search("Address");

        Assertions.assertThat(matched).hasSize(2);
    }

    @Test
    void matched_term_query_should_have_result() {
        SearchService searchService = new SearchService();
        List<Request> matched = searchService.search(new TermQuery(new Term("formName", "address")));

        Assertions.assertThat(matched).hasSize(2);
    }
}
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
        List<Form> matched = searchService.search(new SearchRequest("Address"));

        assertThat(matched).hasSize(2);
    }

    @Test
    void matched_term_query_should_have_result() {
        List<Form> matched = searchService.search(new SearchRequest("Certificate"));

        assertThat(matched).hasSize(1);
    }

    @Test
    void matched_simplified_chinese_keyword_should_have_result() {
        List<Form> matched = searchService.search(new SearchRequest("会面"));

        assertThat(matched).hasSize(1);
    }

    @Test
    void matched_category_should_return_result() {
        List<Form> matched = searchService.search(new SearchRequest()
                .addCategory("CG123456789"));
        assertThat(matched).isNotEmpty();
        assertThat(matched.stream().allMatch(form -> form.getCategoryId().equalsIgnoreCase("CG123456789"))).isTrue();
    }

    @Test
    void matched_categories_should_return_result() {
        List<Form> matched = searchService.search(new SearchRequest()
                .addCategory("CG123456789")
                .addCategory("CG234567890"));
        assertThat(matched).isNotEmpty();
        assertThat(matched.stream().allMatch(form -> {
            String catId = form.getCategoryId();
            return catId.equalsIgnoreCase("CG123456789") || catId.equalsIgnoreCase("CG234567890");
        })).isTrue();
    }


    @Test
    void matched_keyword_and_keyword_should_return_result() {
        List<Form> matched = searchService.search(new SearchRequest("Procedure").addCategory("CG567890123"));
        assertThat(matched).hasSize(1);
    }

    @Test
    void matched_keywords_should_return_result() {
        List<Form> matched = searchService.search(new SearchRequest("Mortgage Payment"));
        assertThat(matched).isNotEmpty();
    }


    @Test
    void matched_categories_and_keywords_should_return_result() {
        List<Form> matched = searchService.search(new SearchRequest("Mortgage Payment")
                .addCategory("CG345678901")
                .addCategory("CG345678901"));
        assertThat(matched).hasSize(2);
    }

    @Test
    void matched_traditional_chinese_should_return_result() {
        List<Form> matched = searchService.search(new SearchRequest("貸款還款"));
        assertThat(matched).hasSize(4);

        matched = searchService.search(new SearchRequest("贷款还款"));
        assertThat(matched).hasSize(4);
    }

    @Test
    void matched_multiple_chinese_words_should_return_result() {
        List<Form> matched = searchService.search(new SearchRequest("贈與未成年人法案"));
        assertThat(matched).hasSize(4);
    }

    @Test
    void matched_fuzzy_query_should_have_result() {
        List<Form> matched = searchService.search(new SearchRequest("hallowen"));

        assertThat(matched).hasSize(1);
    }

    @Test
    void matched_fuzzy_en_mixed_zh_words_should_have_result() {

        Set<String> expectedFormIds = Stream.of(
                        searchService.search(new SearchRequest("Unform")).stream(),
                        searchService.search(new SearchRequest("acount")).stream(),
                        searchService.search(new SearchRequest("未成年人")).stream(),
                        searchService.search(new SearchRequest("法案")).stream())
                .flatMap(Function.identity())
                .map(Form::getFormId).collect(toSet());


        List<Form> matched = searchService.search(new SearchRequest("Unform acount 未成年人法案"));

        assertThat(matched.stream().map(Form::getFormId).collect(toSet())).isEqualTo(expectedFormIds);
        assertThat(matched)
                .isNotEmpty()
                .allMatch(form -> {
                    String formName = form.getFormName().toLowerCase();
                    String formNameCN = form.getFormNameCN();
                    return formName.contains("uniform")
                            || formName.contains("form")
                            || formName.contains("account")
                            || formName.contains("about")
                            || formNameCN.contains("未成年人")
                            || formNameCN.contains("法案");
                });
    }

    @Test
    void pagination_should_work() {
        List<Form> firstPage = searchService.search(new SearchRequest("account", new PageRequest(0, 5)));
        assertThat(firstPage).hasSize(5);
        List<Form> secondPage = searchService.search(new SearchRequest("account", new PageRequest(1, 5)));
        assertThat(secondPage)
                .hasSize(5)
                .noneMatch(p -> firstPage.stream().map(Form::getFormId).collect(toSet()).contains(p.getFormId()));
    }
}
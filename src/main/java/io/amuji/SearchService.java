package io.amuji;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

@Slf4j
public class SearchService {

    private final LuceneIndex index;

    public SearchService() {
        List<Form> forms = new JsonDataLoader().load("sample-data.json");
        index = new LuceneIndex();
        index.buildIndex(forms);
    }

    public List<Form> search(SearchRequest searchRequest) {
        log.info("Start to search...");
        if (searchRequest.isBlank()) {
            log.warn("The search criteria is blank, returns empty.");
            return Collections.emptyList();
        }

        List<Form> result = index.search(searchRequest);
        log.info("Finished searching:");
        result.forEach(form -> log.info(form.toString()));
        return result;
    }
}

package io.amuji;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

@Slf4j
public class SearchService {

    private final LuceneIndex index;

    public SearchService() {
        List<Request> requests = new JsonDataLoader().load("sample-data.json");
        index = new LuceneIndex();
        index.buildIndex(requests);
    }

    public List<Request> search(Search search) {
        log.info("Start to search...");
        if (search.isBlank()) {
            log.warn("The search criteria is blank, returns empty.");
            return Collections.emptyList();
        }

        search.normalizedKeywords(index.normalizeKeywords(search.getKeywords()));
        List<Request> result = index.search(search);
        log.info("Finished searching:");
        result.forEach(request -> log.info(request.toString()));
        return result;
    }
}

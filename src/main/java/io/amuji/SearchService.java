package io.amuji;

import java.util.List;

public class SearchService {

    private final LuceneIndexing indexing;

    public SearchService() {
        List<Request> requests = new JsonDataLoader().load("sample-data.json");
        indexing = new LuceneIndexing();
        indexing.indexing(requests);
    }

    public List<Request> search(String keywords) {
        return indexing.search(keywords);
    }
}

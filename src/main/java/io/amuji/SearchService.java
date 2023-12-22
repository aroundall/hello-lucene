package io.amuji;

import org.apache.lucene.search.Query;

import java.util.List;

public class SearchService {

    private final LuceneIndex index;

    public SearchService() {
        List<Request> requests = new JsonDataLoader().load("sample-data.json");
        index = new LuceneIndex();
        index.buildIndex(requests);
    }

    public List<Request> search(String keywords) {
        return index.search(keywords);
    }

    public List<Request> search(Query query) {
        return index.search(query);
    }

    public List<Request> search(Search search) {
        return index.search(search);
    }
}

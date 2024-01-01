package io.amuji;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.amuji.Fields.*;

@Slf4j

public class LuceneIndex {
    private static final int MAX_HIT_SIZE = 100;

    private final Directory indexDir = new ByteBuffersDirectory();
    private final Analyzer analyzer;

    {
        Map<String, Analyzer> analyzerMap = Map.of(
                FIELD_NAME_CAT_ID, AnalyzerFactory.getInstance().getCategoryAnalyzer());
        analyzer = new PerFieldAnalyzerWrapper(AnalyzerFactory.getInstance().getKeywordsAnalyzer(), analyzerMap);
    }

    public void buildIndex(List<Request> requests) {
        log.info("Start to build index for {} docs", requests.size());
        try {
            IndexWriter writer = new IndexWriter(indexDir, new IndexWriterConfig(analyzer));
            for (Request request : requests) {
                Document document = createDocument(request);
                writer.addDocument(document);

            }
            writer.commit();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Completed to build index for {} docs", requests.size());
    }

    private static Document createDocument(Request request) {
        Document document = new Document();
        document.add(new StringField(FIELD_NAME_FORM_ID, request.getFormId(), Field.Store.YES));
        document.add(new TextField(FIELD_NAME_FORM_NAME, request.getFormName(), Field.Store.YES));
        document.add(new TextField(FIELD_NAME_FORM_NAME_CN, request.getFormNameCN(), Field.Store.YES));
        document.add(new TextField(FIELD_NAME_FORM_NAME_TC, request.getFormNameTC(), Field.Store.YES));
        document.add(new StringField(FIELD_NAME_CAT_ID, request.getCategoryId(), Field.Store.YES));
        return document;
    }

    public List<Request> search(SearchRequest searchRequest) {
        Query query = parseQuery(searchRequest.getQuery());
        return search(query);
    }


    private Query parseQuery(Query query) {
        try {
            log.info("The input query: {}", query.toString());
            Query parsed = new QueryParser(FIELD_NAME_FORM_NAME, analyzer).parse(query.toString());
            log.info("The parsed query: {}", parsed.toString());
            return parsed;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Request> search(Query query) {
        try {
            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(indexDir));
            TopDocs docs = searcher.search(query, MAX_HIT_SIZE);

            return toRequests(docs, searcher);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Request> toRequests(TopDocs docs, IndexSearcher searcher) throws IOException {
        List<Request> matched = new ArrayList<>();
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            matched.add(Request.builder()
                    .formId(searcher.doc(scoreDoc.doc).get(FIELD_NAME_FORM_ID))
                    .categoryId(searcher.doc(scoreDoc.doc).get(FIELD_NAME_CAT_ID))
                    .formName(searcher.doc(scoreDoc.doc).get(FIELD_NAME_FORM_NAME))
                    .formNameCN(searcher.doc(scoreDoc.doc).get(FIELD_NAME_FORM_NAME_CN))
                    .build());
        }
        return matched;
    }

}

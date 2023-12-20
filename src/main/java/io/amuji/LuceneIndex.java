package io.amuji;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
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

public class LuceneIndex {
    public static final String FIELD_NAME_FORM_ID = "formId";
    public static final String FIELD_NAME_FORM_NAME = "formName";
    public static final String FIELD_NAME_FORM_NAME_CN = "formNameCN";

    private final Directory indexDir = new ByteBuffersDirectory();
    private final Analyzer analyzer = new SmartChineseAnalyzer();
    private final int MAX_HIT_SIZE = 100;

    public void buildIndex(List<Request> requests) {
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
    }

    private static Document createDocument(Request request) {
        Document document = new Document();
        document.add(new StringField(FIELD_NAME_FORM_ID, request.getFormId(), Field.Store.YES));
        document.add(new TextField(FIELD_NAME_FORM_NAME, request.getFormName(), Field.Store.YES));
        document.add(new TextField(FIELD_NAME_FORM_NAME_CN, request.getFormNameCN(), Field.Store.YES));
        return document;
    }

    public List<Request> search(String keywords) {
        try {
            Query query = new QueryParser(FIELD_NAME_FORM_NAME, analyzer).parse(keywords);

            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(indexDir));
            TopDocs docs = searcher.search(query, MAX_HIT_SIZE);

            return toRequests(docs, searcher);
        } catch (IOException | ParseException e) {
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
                    .build());
        }
        return matched;
    }
}

package io.amuji;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LuceneIndexing {
    public static final String FIELD_NAME_FORM_ID = "formId";
    public static final String FIELD_NAME_FORM_NAME = "formName";
    public static final String FIELD_NAME_FORM_NAME_CN = "formNameCN";

    private final RAMDirectory indexDir = new RAMDirectory();

    public void indexing(List<Request> requests) {
        Analyzer analyzer = new StandardAnalyzer();
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
//            Query query = new TermQuery(new Term(FIELD_NAME_FORM_NAME, keywords));
            Query query = new QueryParser(FIELD_NAME_FORM_NAME, new StandardAnalyzer()).parse(keywords);

            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(indexDir));
            TopDocs docs = searcher.search(query, 100);

            List<Request> matched = new ArrayList<>();
            for (ScoreDoc scoreDoc : docs.scoreDocs) {
                matched.add(Request.builder()
                        .formId(searcher.doc(scoreDoc.doc).get(FIELD_NAME_FORM_ID))
                        .build());
            }

            return matched;
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}

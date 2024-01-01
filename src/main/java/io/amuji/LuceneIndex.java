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
import org.apache.lucene.search.*;
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

    public void buildIndex(List<Form> forms) {
        log.info("Start to build index for {} docs", forms.size());
        try {
            IndexWriter writer = new IndexWriter(indexDir, new IndexWriterConfig(analyzer));
            for (Form form : forms) {
                Document document = createDocument(form);
                writer.addDocument(document);

            }
            writer.commit();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Completed to build index for {} docs", forms.size());
    }

    private static Document createDocument(Form form) {
        Document document = new Document();
        document.add(new StringField(FIELD_NAME_FORM_ID, form.getFormId(), Field.Store.YES));
        document.add(new TextField(FIELD_NAME_FORM_NAME, form.getFormName(), Field.Store.YES));
        document.add(new TextField(FIELD_NAME_FORM_NAME_CN, form.getFormNameCN(), Field.Store.YES));
        document.add(new TextField(FIELD_NAME_FORM_NAME_TC, form.getFormNameTC(), Field.Store.YES));
        document.add(new StringField(FIELD_NAME_CAT_ID, form.getCategoryId(), Field.Store.YES));
        return document;
    }

    public List<Form> search(SearchRequest searchRequest) {
        Query query = parseQuery(searchRequest.getQuery());
        if (searchRequest.hasPageRequest()) {
            return search(query, searchRequest.getPageRequest());
        }else {
            return search(query);
        }
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

    private List<Form> search(Query query) {
        try {
            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(indexDir));
            TopDocs docs = searcher.search(query, MAX_HIT_SIZE);

            return toRequests(docs, searcher);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Form> search(Query query, PageRequest pageRequest) {
        TopScoreDocCollector collector = TopScoreDocCollector.create(
                pageRequest.getSize() * (pageRequest.getPage() + 1), MAX_HIT_SIZE);
        try {
            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(indexDir));
            searcher.search(query, collector);

            log.info("Total hits: {}", collector.getTotalHits());

            return toRequest(pageRequest, collector, searcher);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Form> toRequest(PageRequest pageRequest, TopScoreDocCollector collector, IndexSearcher searcher) throws IOException {
        int start = pageRequest.getStart();
        int end = Math.min(pageRequest.getEnd(), collector.getTotalHits());

        ScoreDoc[] scoreDocs = collector.topDocs().scoreDocs;
        List<Form> matched = new ArrayList<>();
        for (int i = start; i < end; i++) {
            ScoreDoc doc = scoreDocs[i];
            matched.add(toRequest(searcher, doc));
        }
        return matched;
    }

    private List<Form> toRequests(TopDocs docs, IndexSearcher searcher) throws IOException {
        List<Form> matched = new ArrayList<>();
        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            matched.add(toRequest(searcher, scoreDoc));
        }
        return matched;
    }

    private Form toRequest(IndexSearcher searcher, ScoreDoc scoreDoc) throws IOException {
        return Form.builder()
                .formId(searcher.doc(scoreDoc.doc).get(FIELD_NAME_FORM_ID))
                .categoryId(searcher.doc(scoreDoc.doc).get(FIELD_NAME_CAT_ID))
                .formName(searcher.doc(scoreDoc.doc).get(FIELD_NAME_FORM_NAME))
                .formNameCN(searcher.doc(scoreDoc.doc).get(FIELD_NAME_FORM_NAME_CN))
                .build();
    }

}

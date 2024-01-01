package io.amuji;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;

public class AnalyzerFactory {

    private static AnalyzerFactory factory;

    public Analyzer getKeywordsAnalyzer() {
        return new CJKAnalyzer();
    }

    public Analyzer getCategoryAnalyzer() {
        return new KeywordAnalyzer();
    }

    public static AnalyzerFactory getInstance() {
        if (factory == null) {
            factory = new AnalyzerFactory();
        }
        return factory;
    }
}

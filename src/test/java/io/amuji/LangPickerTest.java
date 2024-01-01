package io.amuji;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;

class LangPickerTest {
    @Test
    void english_words() {
        LangPicker picker = new LangPicker("Hi,who are you? and what are you doing? Are you from A&B/C123 company? Is it a non-profit org?");
        assertThat(picker.getEnglishWords()).contains("A&B", "non-profit", "C123", "what", "who", "are", "you");
        assertThat(picker.getChineseWords()).isEmpty();
    }

    @Test
    void simple_chinese_words() {
        LangPicker picker = new LangPicker("你是谁？要到哪里去？什么。。。意思？什么 意思？");
        assertThat(picker.getChineseWords()).containsSequence("你是谁", "要到哪里去", "什么", "意思", "什么", "意思");
        assertThat(picker.getEnglishWords()).isEmpty();
    }

    @Test
    void traditional_chinese_words() {
        LangPicker picker = new LangPicker("你是誰？要到哪裡去？什麼。。。意思？什麼 意思");
        assertThat(picker.getChineseWords()).containsSequence("你是誰", "要到哪裡去", "什麼", "意思", "什麼", "意思");
        assertThat(picker.getEnglishWords()).isEmpty();
    }

    @Test
    void Japanese_words() {
        LangPicker picker = new LangPicker("あなたは誰ですか？あなたはだれですか？");
        assertThat(picker.getChineseWords()).hasSize(1).containsSequence("誰");
        assertThat(picker.getEnglishWords()).isEmpty();
    }

    @Test
    void mixed_lang_words() {
        String text = "are you going to吃饭？Oh，这是个very nice的点子。certificate, and certificated";
        Analyzer analyzer = new CJKAnalyzer();
        try (TokenStream tokenStream = analyzer.tokenStream("fieldName", new StringReader(text))) {
            CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

            tokenStream.reset(); // Reset the token stream to the beginning

            while (tokenStream.incrementToken()) {
                String analyzedWord = charTermAttribute.toString();
                System.out.println(analyzedWord);
            }

            tokenStream.end(); // Perform end-of-stream operations
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        LangPicker picker = new LangPicker(text);
        assertThat(picker.getChineseWords()).containsSequence("吃饭", "这是个", "的点子");
        assertThat(picker.getEnglishWords())
                .containsSequence("are", "you", "going", "Oh")
                .doesNotContainSequence("to", "very", "nice");
    }
}
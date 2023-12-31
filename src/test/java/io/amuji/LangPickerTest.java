package io.amuji;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LangPickerTest {
    @Test
    void english_words() {
        LangPicker picker = new LangPicker("Hi, who are you? and what are you doing? Are you from A&B/C123 company? Is it a non-profit org?");
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
}
package com.bisoft.dictionary.model;

/**
 * Created by burakisik on 9/3/2017.
 */

public class WordObject {
    public String word;
    public String definition;
    public String phoneSpelling;
    public String audioPath;

    public WordObject(){};

    public WordObject(String word, String definition, String phoneSpelling, String audioPath) {
        this.word = word;
        this.definition = definition;
        this.phoneSpelling = phoneSpelling;
        this.audioPath = audioPath;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getPhoneSpelling() {
        return phoneSpelling;
    }

    public void setPhoneSpelling(String phoneSpelling) {
        this.phoneSpelling = phoneSpelling;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }
}

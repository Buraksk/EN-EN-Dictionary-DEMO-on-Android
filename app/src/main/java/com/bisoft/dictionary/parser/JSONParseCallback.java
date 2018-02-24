package com.bisoft.dictionary.parser;

import com.bisoft.dictionary.model.WordObject;

/**
 * Created by burakisik on 24.02.2018.
 */

public interface JSONParseCallback {
    public void onParseFinished(WordObject result);
}

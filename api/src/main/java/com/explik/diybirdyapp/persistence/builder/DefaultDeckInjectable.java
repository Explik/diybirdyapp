package com.explik.diybirdyapp.persistence.builder;

import com.explik.diybirdyapp.persistence.vertex.LanguageVertex;

public interface DefaultDeckInjectable {
    void injectDefaultFrontLanguage(LanguageVertex languageVertex);
    void injectDefaultBackLanguage(LanguageVertex languageVertex);
}

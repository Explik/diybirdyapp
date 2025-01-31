package com.explik.diybirdyapp.persistence.repository;

import com.explik.diybirdyapp.model.content.VoiceModel;

public interface VoiceRepository {
    VoiceModel get(String languageId);
}

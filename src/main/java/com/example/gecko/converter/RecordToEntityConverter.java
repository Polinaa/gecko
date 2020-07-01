package com.example.gecko.converter;

import com.example.gecko.model.Record;
import com.example.gecko.repository.entity.RecordEntity;
import org.springframework.stereotype.Component;

@Component
public class RecordToEntityConverter {

    public RecordEntity convert(Record record) {
        return new RecordEntity()
                .setPrimaryKey(record.getPrimaryKey())
                .setName(record.getName())
                .setDescription(record.getDescription())
                .setUpdatedTimestamp(record.getUpdatedTimestamp());
    }
}

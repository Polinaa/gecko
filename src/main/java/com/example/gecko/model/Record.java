package com.example.gecko.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Record {

    private String primaryKey;
    private String name;
    private String description;
    private Long updatedTimestamp;
}

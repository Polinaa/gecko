package com.example.gecko.repository.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "data_table")
public class RecordEntity {

    @Id
    @Column(name = "key")
    private String primaryKey;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "updated_timestamp")
    private Long updatedTimestamp;
}

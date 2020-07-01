package com.example.gecko.repository;

import com.example.gecko.model.SearchRequest;
import com.example.gecko.repository.entity.RecordEntity;
import com.example.gecko.repository.entity.RecordEntity_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

import static org.springframework.data.jpa.domain.Specification.where;

@Component
public class RecordEntitySpecificationBuilder {

    public Specification<RecordEntity> build(SearchRequest request) {
        return where(buildFrom(request.getFrom()))
                .and(buildTo(request.getTo()));
    }

    private Specification<RecordEntity> buildFrom(ZonedDateTime from) {
        return from != null ?
                (root, query, cb) -> cb.greaterThanOrEqualTo(root.get(RecordEntity_.updatedTimestamp), from.toInstant().toEpochMilli()) :
                null;
    }

    private Specification<RecordEntity> buildTo(ZonedDateTime to) {
        return to != null ?
                (root, query, cb) -> cb.lessThanOrEqualTo(root.get(RecordEntity_.updatedTimestamp), to.toInstant().toEpochMilli()) :
                null;
    }
}

package com.example.gecko.repository;

import com.example.gecko.repository.entity.RecordEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepository extends PagingAndSortingRepository<RecordEntity, String>, JpaSpecificationExecutor<RecordEntity> {

}

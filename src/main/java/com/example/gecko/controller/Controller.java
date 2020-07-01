package com.example.gecko.controller;

import com.example.gecko.exception.GeckoServiceException;
import com.example.gecko.exception.RecordNotFoundException;
import com.example.gecko.model.SearchRequest;
import com.example.gecko.repository.entity.RecordEntity;
import com.example.gecko.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/data")
public class Controller {

    private final int MAX_PAGE_SIZE = 100;

    private final RecordService recordService;

    @PostMapping
    public ResponseEntity<List<RecordEntity>> upload(@NotNull @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok().body(recordService.validateAndSaveRecords(file));
    }

    @GetMapping
    public ResponseEntity<Page<RecordEntity>> findAll(@Valid SearchRequest request, Pageable pageable) {
        if (pageable.getPageSize() > MAX_PAGE_SIZE) {
            throw new GeckoServiceException("Page size exceeded max value " + MAX_PAGE_SIZE);
        }
        return ResponseEntity.ok().body(recordService.findAll(request, pageable));
    }

    @GetMapping(path = "/{key}")
    public ResponseEntity<RecordEntity> findOne(@PathVariable(value = "key") @NotBlank String key) {
        return ResponseEntity.ok().body(recordService.findByKey(key));
    }

    @DeleteMapping(path = "/{key}")
    public ResponseEntity<?> delete(@PathVariable(value = "key") @NotBlank String key) {
        recordService.delete(key);
        return ResponseEntity.ok().body(null);
    }

    @ExceptionHandler(GeckoServiceException.class)
    public ResponseEntity<?> handleServiceException(GeckoServiceException exception) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<?> handleRecordNotFoundException(RecordNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }
}

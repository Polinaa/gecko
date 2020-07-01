package com.example.gecko;

import com.example.gecko.repository.RecordRepository;
import com.example.gecko.repository.entity.RecordEntity;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class GeckoApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RecordRepository recordRepository;

    @BeforeEach
    @Sql({"classpath:data.sql"})
    void before() {
    }

    @Test
    public void shouldFindAllRecords() throws Exception {
        mvc.perform(get("/data"))
           .andExpect(status().isOk())
           .andExpect(content().json("{\n"
                   + "\"content\": [\n"
                   + "{\n"
                   + "\"primaryKey\": \"key1\",\n"
                   + "\"name\": \"name1\",\n"
                   + "\"description\": \"description1\",\n"
                   + "\"updatedTimestamp\": 1593505252000\n"
                   + "},\n"
                   + "{\n"
                   + "\"primaryKey\": \"key2\",\n"
                   + "\"name\": \"name2\",\n"
                   + "\"description\": \"description2\",\n"
                   + "\"updatedTimestamp\": 1593631139000\n"
                   + "}\n"
                   + "],\n"
                   + "\"pageable\": {\n"
                   + "\"sort\": {\n"
                   + "\"sorted\": false,\n"
                   + "\"unsorted\": true,\n"
                   + "\"empty\": true\n"
                   + "},\n"
                   + "\"offset\": 0,\n"
                   + "\"pageNumber\": 0,\n"
                   + "\"pageSize\": 20,\n"
                   + "\"paged\": true,\n"
                   + "\"unpaged\": false\n"
                   + "},\n"
                   + "\"last\": true,\n"
                   + "\"totalPages\": 1,\n"
                   + "\"totalElements\": 2,\n"
                   + "\"size\": 20,\n"
                   + "\"number\": 0,\n"
                   + "\"numberOfElements\": 2,\n"
                   + "\"sort\": {\n"
                   + "\"sorted\": false,\n"
                   + "\"unsorted\": true,\n"
                   + "\"empty\": true\n"
                   + "},\n"
                   + "\"first\": true,\n"
                   + "\"empty\": false\n"
                   + "}"));
    }

    @Test
    public void shouldFindByKey() throws Exception {
        mvc.perform(get("/data/key2"))
           .andExpect(status().isOk())
           .andExpect(content().json("{\n"
                   + "\"primaryKey\": \"key2\",\n"
                   + "\"name\": \"name2\",\n"
                   + "\"description\": \"description2\",\n"
                   + "\"updatedTimestamp\": 1593631139000\n"
                   + "}"));
    }

    @Test
    public void shouldReturnNotFoundOnFindIfNoKey() throws Exception {
        mvc.perform(get("/data/test"))
           .andExpect(status().isNotFound());
    }

    @Test
    public void shouldDeleteByKey() throws Exception {
        mvc.perform(delete("/data/key2"))
           .andExpect(status().isOk());
        assertEquals(1, Lists.newArrayList(recordRepository.findAll()).size());
    }

    @Test
    public void shouldReturnNotFoundOnDeleteIfNoKey() throws Exception {
        mvc.perform(delete("/data/test"))
           .andExpect(status().isNotFound());
    }

    @Test
    public void shouldUploadNewFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain",
                "PRIMARY_KEY,NAME,DESCRIPTION,UPDATED_TIMESTAMP\nkey3,name3,description3,1593505253000\n".getBytes());

        mvc.perform(multipart("/data").file(file))
           .andExpect(status().isOk())
           .andExpect(content().json("[{\n"
                   + "\"primaryKey\": \"key3\",\n"
                   + "\"name\": \"name3\",\n"
                   + "\"description\": \"description3\",\n"
                   + "\"updatedTimestamp\": 1593505253000\n"
                   + "}]"));
        assertEquals(3, Lists.newArrayList(recordRepository.findAll()).size());
    }

    @Test
    public void shouldUpdate() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain",
                "PRIMARY_KEY,NAME,DESCRIPTION,UPDATED_TIMESTAMP\nkey2,name2,newDescription,1593505253000\n".getBytes());

        mvc.perform(multipart("/data").file(file))
           .andExpect(status().isOk())
           .andExpect(content().json("[{\n"
                   + "\"primaryKey\": \"key2\",\n"
                   + "\"name\": \"name2\",\n"
                   + "\"description\": \"newDescription\",\n"
                   + "\"updatedTimestamp\": 1593505253000\n"
                   + "}]"));
        assertEquals(2, Lists.newArrayList(recordRepository.findAll()).size());

        RecordEntity data = recordRepository.findById("key2").orElse(null);
        assertNotNull(data);
        RecordEntity expectedData = new RecordEntity().setPrimaryKey("key2")
                                                      .setName("name2")
                                                      .setDescription("newDescription")
                                                      .setUpdatedTimestamp(1593505253000l);
        assertEquals(expectedData, data);
    }

    @Test
    public void shouldUploadNewFileWithKeyDuplicates() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain",
                "PRIMARY_KEY,NAME,DESCRIPTION,UPDATED_TIMESTAMP\nkey3,name,description,1593505253000\nkey3,name3,description3,1593505253000\n".getBytes());

        mvc.perform(multipart("/data").file(file))
           .andExpect(status().isOk())
           .andExpect(content().json("[{\n"
                   + "\"primaryKey\": \"key3\",\n"
                   + "\"name\": \"name\",\n"
                   + "\"description\": \"description\",\n"
                   + "\"updatedTimestamp\": 1593505253000\n"
                   + "},\n"
                   + "{\n"
                   + "\"primaryKey\": \"key3\",\n"
                   + "\"name\": \"name3\",\n"
                   + "\"description\": \"description3\",\n"
                   + "\"updatedTimestamp\": 1593505253000\n"
                   + "}]"));
        assertEquals(3, Lists.newArrayList(recordRepository.findAll()).size());

        RecordEntity data = recordRepository.findById("key3").orElse(null);
        assertNotNull(data);
        RecordEntity expectedData = new RecordEntity().setPrimaryKey("key3")
                                                      .setName("name3")
                                                      .setDescription("description3")
                                                      .setUpdatedTimestamp(1593505253000l);
        assertEquals(expectedData, data);
    }

    @Test
    public void shouldReturnBadRequestIfInvalidTimeFormat() throws Exception {
        mvc.perform(get("/data?from='1994-11'"))
           .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFindByAllFromTime() throws Exception {
        mvc.perform(get("/data?from=2020-07-01T13:15:30Z"))
           .andExpect(status().isOk())
           .andExpect(content().json("{\n"
                   + "\"content\": [\n"
                   + "{\n"
                   + "\"primaryKey\": \"key2\",\n"
                   + "\"name\": \"name2\",\n"
                   + "\"description\": \"description2\",\n"
                   + "\"updatedTimestamp\": 1593631139000\n"
                   + "}\n"
                   + "],\n"
                   + "\"pageable\": {\n"
                   + "\"sort\": {\n"
                   + "\"sorted\": false,\n"
                   + "\"unsorted\": true,\n"
                   + "\"empty\": true\n"
                   + "},\n"
                   + "\"offset\": 0,\n"
                   + "\"pageNumber\": 0,\n"
                   + "\"pageSize\": 20,\n"
                   + "\"paged\": true,\n"
                   + "\"unpaged\": false\n"
                   + "},\n"
                   + "\"last\": true,\n"
                   + "\"totalPages\": 1,\n"
                   + "\"totalElements\": 1,\n"
                   + "\"size\": 20,\n"
                   + "\"number\": 0,\n"
                   + "\"numberOfElements\": 1,\n"
                   + "\"sort\": {\n"
                   + "\"sorted\": false,\n"
                   + "\"unsorted\": true,\n"
                   + "\"empty\": true\n"
                   + "},\n"
                   + "\"first\": true,\n"
                   + "\"empty\": false\n"
                   + "}"));
    }

    @Test
    public void shouldFindByCustomSize() throws Exception {
        mvc.perform(get("/data?size=1"))
           .andExpect(status().isOk())
           .andExpect(content().json("{\n"
                   + "\"content\": [\n"
                   + "{\n"
                   + "\"primaryKey\": \"key1\",\n"
                   + "\"name\": \"name1\",\n"
                   + "\"description\": \"description1\",\n"
                   + "\"updatedTimestamp\": 1593505252000\n"
                   + "}\n"
                   + "],\n"
                   + "\"pageable\": {\n"
                   + "\"sort\": {\n"
                   + "\"sorted\": false,\n"
                   + "\"unsorted\": true,\n"
                   + "\"empty\": true\n"
                   + "},\n"
                   + "\"offset\": 0,\n"
                   + "\"pageNumber\": 0,\n"
                   + "\"pageSize\": 1,\n"
                   + "\"paged\": true,\n"
                   + "\"unpaged\": false\n"
                   + "},\n"
                   + "\"last\": false,\n"
                   + "\"totalPages\": 2,\n"
                   + "\"totalElements\": 2,\n"
                   + "\"size\": 1,\n"
                   + "\"number\": 0,\n"
                   + "\"numberOfElements\": 1,\n"
                   + "\"sort\": {\n"
                   + "\"sorted\": false,\n"
                   + "\"unsorted\": true,\n"
                   + "\"empty\": true\n"
                   + "},\n"
                   + "\"first\": true,\n"
                   + "\"empty\": false\n"
                   + "}"));
    }

    @Test
    public void shouldReturnBadRequestIfInvalidOneRow() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain",
                "PRIMARY_KEY,NAME,DESCRIPTION,UPDATED_TIMESTAMP\nkey3,name3,description3,1593505253000\nkey4,name4,description4,2020/03/03\n".getBytes());

        mvc.perform(multipart("/data").file(file))
           .andExpect(status().isBadRequest());
        assertEquals(2, Lists.newArrayList(recordRepository.findAll()).size());
    }
}

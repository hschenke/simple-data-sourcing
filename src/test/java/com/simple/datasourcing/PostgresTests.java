package com.simple.datasourcing;

import com.simple.datasourcing.postgres.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.springframework.test.annotation.*;

@Slf4j
@DirtiesContext
class PostgresTests extends DataSourcingTestBase {

    @BeforeAll
    static void setUp() {
        postgreSQLContainer.start();
    }

    @AfterAll
    static void tearDown() {
        postgreSQLContainer.stop();
    }

    public PostgresTests() {
        super(new PostgresDataMaster(postgreSQLContainer.getJdbcUrl()));
    }

    @Test
    void audit() {
        runAuditTest();
    }

    @Test
    void allActions1() {
        runActionsFor(testData1);
    }

    @Test
    void allActions2() {
        runActionsFor(testData2);
    }

    @Test
    void allActions3() {
        runActionsFor(testData3);
    }

//    @Test
//    void zonedDateTimeTest() throws JsonProcessingException {
//        var objectMapper = new ObjectMapper();
//        // Custom formatter
//        DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
//
//        // JavaTimeModule with custom serializer for ZonedDateTime
//        JavaTimeModule javaTimeModule = new JavaTimeModule();
//        javaTimeModule.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer(formatter));
//        javaTimeModule.addDeserializer(ZonedDateTime.class, new CustomZonedDateTimeDeserializer());
//
//        // Register the module
//        objectMapper.registerModule(javaTimeModule);
//        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//        var now = ZonedDateTime.now();
//        System.out.println(now);
//        var mapped = objectMapper.writeValueAsString(now);
//        System.out.println(mapped);
//        System.out.println(objectMapper.readValue(mapped, ZonedDateTime.class));
//    }
}
package com.simple.datasourcing;

import com.simple.datasourcing.entity.*;
import com.simple.datasourcing.model.*;
import com.simple.datasourcing.service.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class SimpleDataSourcingAppTests {

    @Autowired
    DataActions<TestData1> dataActions1;
    @Autowired
    DataActions<TestData2> dataActions2;
    @Autowired
    DataEventRepository<?> repo;

    @Test
    void contextLoads() {
        System.out.println("---");
        System.out.println("Repo count : " + repo.count());
        System.out.println("---");

        var testData1 = TestData1.builder().id("test1").build();
        var testData2 = TestData2.builder().id("test2").build();

        var test1 = dataActions1.createFor("testId", testData1);
        var test2 = dataActions2.createFor("testId", testData2);

        System.out.println(test1);
        assertThat(test1).isNotNull().hasFieldOrPropertyWithValue("data", testData1);
        System.out.println("---");

        System.out.println(dataActions1.deleteFor("testId"));
        System.out.println("---");

        dataActions1.getAllFor("testId").forEach(System.out::println);
        System.out.println("---");

        System.out.println(dataActions1.getLastFor("testId"));
    }
}
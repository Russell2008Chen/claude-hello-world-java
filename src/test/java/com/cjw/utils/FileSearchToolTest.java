package com.cjw.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * FileSearchTool 的单元测试
 */
public class FileSearchToolTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        // 创建测试文件
        Files.writeString(tempDir.resolve("test1.txt"), "ERROR: Something failed at 2024-01-15\nINFO: Started");
        Files.writeString(tempDir.resolve("test2.txt"), "WARNING: Low memory\nERROR: Crash at 2024-01-16");
        Files.writeString(tempDir.resolve("readme.md"), "This is not a txt file");
    }

    @Test
    void testSearchWithMatchingFile() throws IOException {
        FileSearchTool tool = FileSearchTool.builder(tempDir.toString(), "ERROR")
            .addExtractPattern("error", "ERROR: (.*)")
            .build();

        Map<String, Map<String, String>> results = tool.search();

        assertEquals(2, results.size());
        assertTrue(results.containsKey("test1.txt"));
        assertTrue(results.containsKey("test2.txt"));
    }

    @Test
    void testSearchWithNoMatches() throws IOException {
        FileSearchTool tool = FileSearchTool.builder(tempDir.toString(), "NOTEXIST")
            .addExtractPattern("error", "ERROR: (.*)")
            .build();

        Map<String, Map<String, String>> results = tool.search();

        assertTrue(results.isEmpty());
    }

    @Test
    void testSearchWithInvalidFolder() throws IOException {
        FileSearchTool tool = FileSearchTool.builder("/nonexistent/folder", "ERROR")
            .addExtractPattern("error", "ERROR: (.*)")
            .build();

        Map<String, Map<String, String>> results = tool.search();

        assertTrue(results.isEmpty());
    }

    @Test
    void testExtractContentWithGroup() throws IOException {
        FileSearchTool tool = FileSearchTool.builder(tempDir.toString(), "ERROR")
            .addExtractPattern("message", "ERROR: (.*)")
            .build();

        Map<String, Map<String, String>> results = tool.search();

        Map<String, String> test1Result = results.get("test1.txt");
        assertEquals("Something failed at 2024-01-15", test1Result.get("message"));
    }

    @Test
    void testExtractContentWithoutGroup() throws IOException {
        FileSearchTool tool = FileSearchTool.builder(tempDir.toString(), "ERROR")
            .addExtractPattern("error", "ERROR")
            .build();

        Map<String, Map<String, String>> results = tool.search();

        Map<String, String> test1Result = results.get("test1.txt");
        assertEquals("ERROR", test1Result.get("error"));
    }

    @Test
    void testExtractContentMultiplePatterns() throws IOException {
        FileSearchTool tool = FileSearchTool.builder(tempDir.toString(), "ERROR")
            .addExtractPattern("message", "ERROR: (.*)")
            .addExtractPattern("date", "(\\d{4}-\\d{2}-\\d{2})")
            .build();

        Map<String, Map<String, String>> results = tool.search();

        Map<String, String> test1Result = results.get("test1.txt");
        assertEquals("Something failed at 2024-01-15", test1Result.get("message"));
        assertEquals("2024-01-15", test1Result.get("date"));
    }

    @Test
    void testExtractContentWithNoMatchReturnsEmpty() throws IOException {
        FileSearchTool tool = FileSearchTool.builder(tempDir.toString(), "ERROR")
            .addExtractPattern("message", "ERROR: (.*)")
            .addExtractPattern("nomatch", "NOTEXIST(.*)")
            .build();

        Map<String, Map<String, String>> results = tool.search();

        Map<String, String> test1Result = results.get("test1.txt");
        assertNotNull(test1Result.get("message"));
        assertEquals("", test1Result.get("nomatch"));
    }

    @Test
    void testFormatResults() {
        Map<String, Map<String, String>> results = new java.util.LinkedHashMap<>();
        Map<String, String> file1Data = new java.util.LinkedHashMap<>();
        file1Data.put("key1", "value1");
        file1Data.put("key2", "value2");
        results.put("test.txt", file1Data);

        String output = FileSearchTool.formatResults(results);

        assertTrue(output.contains("文件：test.txt"));
        assertTrue(output.contains("key1=value1"));
        assertTrue(output.contains("key2=value2"));
    }

    @Test
    void testFormatResultsEmpty() {
        Map<String, Map<String, String>> results = new java.util.LinkedHashMap<>();
        String output = FileSearchTool.formatResults(results);
        assertEquals("", output);
    }
}

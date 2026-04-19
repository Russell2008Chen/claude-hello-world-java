# FileSearchTool Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 创建一个命令行文件查找工具，支持正则表达式搜索和内容提取

**Architecture:** 使用构建器模式创建 FileSearchTool 类，通过 Apache Commons CLI 解析命令行参数，使用 Java NIO 遍历文件和正则表达式匹配内容

**Tech Stack:** Java 11, Apache Commons CLI 1.5.0, JUnit 5

---

## Task 1: 添加 Apache Commons CLI 依赖

**Files:**
- Modify: `pom.xml`

- [ ] **Step 1: 添加 Apache Commons CLI 依赖**

在 `pom.xml` 的 `<dependencies>` 部分添加：

```xml
<!-- Apache Commons CLI 命令行参数解析 -->
<dependency>
    <groupId>commons-cli</groupId>
    <artifactId>commons-cli</artifactId>
    <version>1.5.0</version>
</dependency>
```

- [ ] **Step 2: 验证依赖添加成功**

```bash
mvn dependency:tree
```
Expected: 输出中包含 `commons-cli:commons-cli:1.5.0`

- [ ] **Step 3: 编译验证**

```bash
mvn compile
```
Expected: BUILD SUCCESS

- [ ] **Step 4: 提交**

```bash
git add pom.xml
git commit -m "chore: add Apache Commons CLI dependency"
```

## Task 2: 创建 FileSearchTool 主类

**Files:**
- Create: `src/main/java/com/cjw/utils/FileSearchTool.java`

- [ ] **Step 1: 创建 FileSearchTool 类骨架**

```java
package com.cjw.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.cli.*;

/**
 * 文件查找工具，支持正则表达式搜索和内容提取
 */
public class FileSearchTool {

    private final String folderPath;
    private final Pattern queryPattern;
    private final LinkedHashMap<String, Pattern> extractPatterns;

    private FileSearchTool(Builder builder) {
        this.folderPath = builder.folderPath;
        this.queryPattern = Pattern.compile(builder.queryRegex);
        this.extractPatterns = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : builder.extractPatterns.entrySet()) {
            this.extractPatterns.put(entry.getKey(), Pattern.compile(entry.getValue()));
        }
    }
}
```

- [ ] **Step 2: 编译验证**

```bash
mvn compile
```
Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/cjw/utils/FileSearchTool.java
git commit -m "feat: add FileSearchTool class skeleton"
```

## Task 3: 实现 Builder 内部类

**Files:**
- Modify: `src/main/java/com/cjw/utils/FileSearchTool.java`

- [ ] **Step 1: 添加 Builder 内部类**

在 FileSearchTool 类中添加：

```java
/**
 * 构建器模式创建 FileSearchTool 实例
 */
public static class Builder {
    private final String folderPath;
    private final String queryRegex;
    private final Map<String, String> extractPatterns = new LinkedHashMap<>();

    public Builder(String folderPath, String queryRegex) {
        this.folderPath = folderPath;
        this.queryRegex = queryRegex;
    }

    /**
     * 添加提取正则表达式
     *
     * @param key 正则表达式名称
     * @param regex 正则表达式
     * @return 构建器
     */
    public Builder addExtractPattern(String key, String regex) {
        extractPatterns.put(key, regex);
        return this;
    }

    public FileSearchTool build() {
        return new FileSearchTool(this);
    }
}

/**
 * 静态工厂方法
 */
public static Builder builder(String folderPath, String queryRegex) {
    return new Builder(folderPath, queryRegex);
}
```

- [ ] **Step 2: 编译验证**

```bash
mvn compile
```
Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/cjw/utils/FileSearchTool.java
git commit -m "feat: add Builder pattern to FileSearchTool"
```

## Task 4: 实现 search() 方法

**Files:**
- Modify: `src/main/java/com/cjw/utils/FileSearchTool.java`

- [ ] **Step 1: 添加 search() 方法**

```java
/**
 * 执行文件搜索和提取
 *
 * @return 匹配的文件及其提取的内容
 * @throws IOException 读取文件失败时抛出
 */
public Map<String, Map<String, String>> search() throws IOException {
    Map<String, Map<String, String>> results = new LinkedHashMap<>();
    Path folder = Path.of(folderPath);

    if (!Files.isDirectory(folder)) {
        System.err.println("错误：文件夹不存在：" + folderPath);
        return results;
    }

    Files.list(folder)
        .filter(path -> path.toString().endsWith(".txt"))
        .filter(Files::isRegularFile)
        .forEach(path -> {
            try {
                String content = Files.readString(path);
                if (queryPattern.matcher(content).find()) {
                    Map<String, String> extracted = extractContent(content);
                    results.put(path.getFileName().toString(), extracted);
                }
            } catch (IOException e) {
                System.err.println("读取文件失败：" + path + ", 错误：" + e.getMessage());
            }
        });

    return results;
}
```

- [ ] **Step 2: 编译验证**

```bash
mvn compile
```
Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/cjw/utils/FileSearchTool.java
git commit -m "feat: implement search() method"
```

## Task 5: 实现 extractContent() 方法

**Files:**
- Modify: `src/main/java/com/cjw/utils/FileSearchTool.java`

- [ ] **Step 1: 添加 extractContent() 私有方法**

```java
/**
 * 从文件内容中提取匹配的内容
 *
 * @param content 文件内容
 * @return 提取的 key-value 映射
 */
private Map<String, String> extractContent(String content) {
    Map<String, String> extracted = new LinkedHashMap<>();
    for (Map.Entry<String, Pattern> entry : extractPatterns.entrySet()) {
        Matcher matcher = entry.getValue().matcher(content);
        if (matcher.find()) {
            if (matcher.groupCount() > 0) {
                extracted.put(entry.getKey(), matcher.group(1));
            } else {
                extracted.put(entry.getKey(), matcher.group());
            }
        } else {
            // 未匹配到，输出空值
            extracted.put(entry.getKey(), "");
        }
    }
    return extracted;
}
```

- [ ] **Step 2: 编译验证**

```bash
mvn compile
```
Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/cjw/utils/FileSearchTool.java
git commit -m "feat: implement extractContent() method"
```

## Task 6: 实现 formatResults() 静态方法

**Files:**
- Modify: `src/main/java/com/cjw/utils/FileSearchTool.java`

- [ ] **Step 1: 添加 formatResults() 方法**

```java
/**
 * 格式化输出结果
 *
 * @param results 搜索结果
 * @return 格式化的字符串
 */
public static String formatResults(Map<String, Map<String, String>> results) {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, Map<String, String>> entry : results.entrySet()) {
        sb.append("文件：").append(entry.getKey()).append("\n");
        for (Map.Entry<String, String> kv : entry.getValue().entrySet()) {
            sb.append("  ").append(kv.getKey()).append("=").append(kv.getValue()).append("\n");
        }
        sb.append("\n");
    }
    return sb.toString();
}
```

- [ ] **Step 2: 编译验证**

```bash
mvn compile
```
Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/cjw/utils/FileSearchTool.java
git commit -m "feat: implement formatResults() method"
```

## Task 7: 实现 main() 方法和命令行参数解析

**Files:**
- Modify: `src/main/java/com/cjw/utils/FileSearchTool.java`

- [ ] **Step 1: 添加 main() 方法**

```java
/**
 * 主方法，支持命令行参数
 * 参数格式：--folder <路径> --query <正则> --extract "key1=value1,key2=value2"
 */
public static void main(String[] args) {
    CommandLineParser parser = new DefaultParser();
    Options options = createOptions();

    try {
        CommandLine cmd = parser.parse(options, args);

        String folderPath = cmd.getOptionValue("folder");
        String queryRegex = cmd.getOptionValue("query");
        String extractArg = cmd.getOptionValue("extract");

        if (folderPath == null || queryRegex == null) {
            printUsage(options);
            return;
        }

        Builder builder = builder(folderPath, queryRegex);

        // 解析 extract 参数
        if (extractArg != null) {
            parseExtractPatterns(extractArg, builder);
        }

        FileSearchTool tool = builder.build();
        Map<String, Map<String, String>> results = tool.search();
        System.out.println(formatResults(results));

    } catch (ParseException e) {
        System.err.println("参数解析失败：" + e.getMessage());
        printUsage(options);
    } catch (IOException e) {
        System.err.println("搜索失败：" + e.getMessage());
    }
}

private static Options createOptions() {
    Options options = new Options();
    options.addRequiredOption("f", "folder", true, "文件夹路径");
    options.addRequiredOption("q", "query", true, "查询正则表达式");
    options.addOption("e", "extract", true, "提取正则表达式 (key=value,key2=value2)");
    return options;
}

private static void printUsage(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("FileSearchTool", options);
}

private static void parseExtractPatterns(String extractArg, Builder builder) {
    // 解析格式："key1=value1,key2=value2"
    String[] pairs = extractArg.split(",");
    for (String pair : pairs) {
        int equalsIndex = pair.indexOf('=');
        if (equalsIndex > 0) {
            String key = pair.substring(0, equalsIndex).trim();
            String value = pair.substring(equalsIndex + 1).trim();
            builder.addExtractPattern(key, value);
        }
    }
}
```

- [ ] **Step 2: 编译验证**

```bash
mvn compile
```
Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/cjw/utils/FileSearchTool.java
git commit -m "feat: implement main() with Apache Commons CLI"
```

## Task 8: 创建 FileSearchToolTest 测试类

**Files:**
- Create: `src/test/java/com/cjw/utils/FileSearchToolTest.java`

- [ ] **Step 1: 创建测试类骨架**

```java
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
}
```

- [ ] **Step 2: 添加搜索测试方法**

```java
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
```

- [ ] **Step 3: 添加内容提取测试方法**

```java
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
```

- [ ] **Step 4: 添加格式输出测试方法**

```java
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
```

- [ ] **Step 5: 编译验证**

```bash
mvn compile
```
Expected: BUILD SUCCESS

- [ ] **Step 6: 提交**

```bash
git add src/test/java/com/cjw/utils/FileSearchToolTest.java
git commit -m "test: add FileSearchToolTest with comprehensive test coverage"
```

## Task 9: 运行测试并验证

**Files:**
- N/A

- [ ] **Step 1: 运行所有测试**

```bash
mvn test
```
Expected: All tests pass, BUILD SUCCESS

- [ ] **Step 2: 手动测试命令行工具**

```bash
mvn compile
java -cp target/classes:~/.m2/repository/commons-cli/commons-cli/1.5.0/commons-cli-1.5.0.jar com.cjw.utils.FileSearchTool --folder src/test/java --query "Test" --extract "class=(\\w+)"
```
Expected: 输出匹配的 txt 文件和提取的内容

- [ ] **Step 3: 提交最终验证**

```bash
git status
```
Expected: 干净的工作目录

---

## 完成检查清单

- [ ] 所有测试通过 (`mvn test` 成功)
- [ ] 代码可以通过 `mvn compile` 编译
- [ ] 命令行工具可以正常执行
- [ ] 所有提交已完成

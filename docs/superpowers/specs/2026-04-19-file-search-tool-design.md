# FileSearchTool 设计文档

## 概述

创建一个命令行文件查找工具 `FileSearchTool`，用于在指定文件夹中搜索 `.txt` 文件，并根据正则表达式提取文件内容。

## 功能需求

1. **命令行参数**：
   - `--folder`：要搜索的文件夹路径
   - `--query`：查询正则表达式，用于匹配文件内容
   - `--extract`：提取正则表达式，格式为 `"key1=value1,key2=value2"`，value 可以包含 `=` 字符

2. **文件遍历**：
   - 仅遍历指定文件夹下的 `.txt` 文件（不递归子文件夹）
   - 跳过无法读取的文件

3. **内容匹配**：
   - 对每个文件，使用查询正则表达式匹配文件内容
   - 每个匹配的文件只在结果中出现一次

4. **内容提取**：
   - 对匹配的文件，使用提取正则表达式提取内容
   - 如果正则有捕获组，只提取 `group(1)`；否则提取整个匹配字符串
   - 未匹配到的 key 输出空值

5. **输出格式**：
   ```
   文件：example.txt
     timestamp=2024-01-15
     message=Error occurred
   ```

6. **错误处理**：
   - 文件夹不存在：输出错误信息并返回空结果
   - 文件读取失败：跳过该文件，继续处理

## 技术设计

### 类结构

```
com.cjw.utils.FileSearchTool
├── Fields:
│   ├── folderPath: String
│   ├── queryPattern: Pattern
│   └── extractPatterns: Map<String, Pattern>
├── Methods:
│   ├── main(String[] args): void
│   ├── search(): Map<String, Map<String, String>>
│   ├── extractContent(String): Map<String, String>
│   └── formatResults(Map): String
└── Builder (内部类)
    ├── addExtractPattern(String, String): Builder
    └── build(): FileSearchTool
```

### 命令行参数设计

使用 Apache Commons CLI 解析：

```java
Options options = new Options();
options.addRequiredOption("f", "folder", true, "文件夹路径");
options.addRequiredOption("q", "query", true, "查询正则表达式");
options.addOption("e", "extract", true, "提取正则表达式 (key=value,key2=value2)");
```

### 数据流

```
命令行参数 → 解析 → FileSearchTool → search() → 遍历文件 → 匹配查询 → 提取内容 → 格式化输出
```

## 依赖

添加 Apache Commons CLI 依赖到 `pom.xml`：

```xml
<dependency>
    <groupId>commons-cli</groupId>
    <artifactId>commons-cli</artifactId>
    <version>1.5.0</version>
</dependency>
```

## 使用示例

```bash
# 搜索包含 ERROR 的日志文件，提取时间戳和消息
java -cp target/classes com.cjw.utils.FileSearchTool \
  --folder ./logs \
  --query "ERROR" \
  --extract "timestamp=\d{4}-\d{2}-\d{2},message=ERROR: (.*)"

# 搜索包含 email 的文件，提取邮箱地址
java -cp target/classes com.cjw.utils.FileSearchTool \
  --folder ./data \
  --query "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+" \
  --extract "email=[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+"
```

## 测试策略

1. 单元测试覆盖：
   - 参数解析（有效参数、缺失参数、无效参数）
   - 文件搜索逻辑
   - 正则提取逻辑（有 group、无 group）
   - 错误处理（无效文件夹、不可读文件）
   - 输出格式化

2. 集成测试：
   - 创建测试文件夹和测试文件
   - 执行完整搜索流程
   - 验证输出结果

## YAGNI 边界

本版本不包含：
- 递归子文件夹搜索
- 多 group 提取支持
- 输出格式自定义
- 并发文件处理
- 非.txt 文件支持

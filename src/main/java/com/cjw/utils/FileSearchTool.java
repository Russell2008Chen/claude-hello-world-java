package com.cjw.utils;

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
}

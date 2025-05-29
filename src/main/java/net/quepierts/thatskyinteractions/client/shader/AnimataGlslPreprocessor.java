package net.quepierts.thatskyinteractions.client.shader;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// It will merge into Animata soon
public class AnimataGlslPreprocessor {
    private static final String COMMENT = "(//.*|/\\*[^*]*\\*+([^/*][^*]*\\*+)*/)";

    public static final String IDENTIFIER_PRAGMA = "pragma";
    public static final String IDENTIFIER_INCLUDE = "include";
    public static final String IDENTIFIER_VERSION = "version";
    public static final String IDENTIFIER_MULTI_COMPILE = "multi_branches";
    public static final String IDENTIFIER_BRANCHES = "branches";

    public static final String REGEX_MACRO = "#("
            + IDENTIFIER_PRAGMA + "|"
            + IDENTIFIER_INCLUDE + "|"
//            + IDENTIFIER_VERSION
            + ")(.*)?$";

    public static final String REGEX_PRAGMA_IDENTIFIER = "("
            + IDENTIFIER_MULTI_COMPILE + "|"
            + IDENTIFIER_BRANCHES
            + ")\\s*(?:\\(([^()]+)\\))?";

    public static final Pattern PATTERN_WORD = Pattern.compile("\\w+");
    public static final Pattern PATTERN_STRING = Pattern.compile("\"([^\"]*)\"");

    public static final Pattern PATTERN_MACRO = Pattern.compile(REGEX_MACRO, Pattern.MULTILINE);
    public static final Pattern PATTERN_INCLUDE = Pattern.compile("#include\\s+\"([^\"]+)\"\\s*");

    public static final Pattern PATTERN_PRAGMA_IDENTIFIER = Pattern.compile(REGEX_PRAGMA_IDENTIFIER);
    public static final Pattern PATTERN_CONTENT_PRAGMA_IDENTIFIER = Pattern.compile("(\\w+)\\s*=\\s*(\\w+|\\[[^\\[\\]]+\\])");

    private final SourceProvider sourceProvider;

    private final Map<String, Object> defines = new HashMap<>();

    private final List<String> parts = new ArrayList<>();

    private final List<Branch> branches = new ArrayList<>();
    private final Object2IntMap<String> masks = new Object2IntOpenHashMap<>(32);

    private boolean multiCompile = false;

    public AnimataGlslPreprocessor(String source, SourceProvider sourceProvider) {
        this.sourceProvider = sourceProvider;
        String noComment = removeComment(source);
        this.extractMacro(noComment);
    }

    public void result(ResultConsumer consumer) {
        List<Integer> validMasks = generateValidCombinationMasks(this.branches);
        List<String> combination = new ArrayList<>(this.branches.size());

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> define : this.defines.entrySet()) {
            builder.append("#define ")
                    .append(define.getKey())
                    .append(' ')
                    .append(define.getValue())
                    .append('\n');
        }

        for (String part : this.parts) {
            builder.append(part);
        }

        if (!this.multiCompile) {
            consumer.accept("$", builder.toString());
        } else {
            for (int mask1 : validMasks) {
                StringBuilder branchBuilder = new StringBuilder("$");
                StringBuilder resultBuilder = new StringBuilder();
                for (int i = 0; i < branches.size(); i++) {
                    if ((mask1 & (1 << i)) != 0) {
                        String name = branches.get(i).name;

                        if (branchBuilder.length() != 1) {
                            branchBuilder.append("_");
                        }
                        branchBuilder.append(name);
                        resultBuilder.append("#define ").append(name).append('\n');

                        combination.add(name);
                    }
                }

                resultBuilder.append(builder);
                System.out.println(combination);
                consumer.accept(branchBuilder.toString(), resultBuilder.toString());

                combination.clear();
            }
        }
    }

    private static int findNextNonEmpty(String string, int src) {
        while (src < string.length()) {
            char c = string.charAt(src);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return src;
            }
            src++;
        }
        return src;
    }

    private static List<Integer> generateValidCombinationMasks(List<Branch> branches) {
        List<Integer> result = new ArrayList<>();
        int n = branches.size();
        int totalCombinations = 1 << n;

        for (int mask = 0; mask < totalCombinations; mask++) {
            if (isValid(mask, branches)) {
                result.add(mask);
            }
        }
        return result;
    }

    private static boolean isValid(int mask, List<Branch> branches) {
        for (int i = 0; i < branches.size(); i++) {
            if ((mask & (1 << i)) != 0) {
                int req = branches.get(i).mask();
                if ((mask & req) != req) {
                    return false;
                }
            }
        }
        return true;
    }

    private static String removeComment(String source) {
        return source.replaceAll(COMMENT, "");
    }

    private void extractMacro(String source) {
        Matcher matcher = PATTERN_MACRO.matcher(source);

        int left = 0;
        int right;
        while (matcher.find()) {
            right = matcher.start();
            String name = matcher.group(1);
            String content = matcher.group(2);

            String substring = source.substring(left, right);
            if (!substring.isBlank()) {
                this.addPart(substring);
            }

            switch (name) {
                case IDENTIFIER_PRAGMA:
                    handlePragma(content);
                    break;
                case IDENTIFIER_INCLUDE:
                    for (String s : handleInclude(content)) {
                        this.addPart(s);
                    }
                    break;
            }

            left = matcher.end();
        }

        this.addPart(source.substring(left));
    }

    private void addPart(String part) {
        int left = findNextNonEmpty(part, 0);
        String part1 = part.substring(left);
        if (!part1.isEmpty() && !part1.isBlank()) {
            this.parts.add(part1);
        }
    }

    private void handlePragma(String content) {
        Matcher matcher = PATTERN_PRAGMA_IDENTIFIER.matcher(content);

        if (matcher.find()) {
            String identifier = matcher.group(1);
            String param = matcher.group(2);

            System.out.println(identifier + " " + param);
            switch (identifier) {
                case IDENTIFIER_MULTI_COMPILE:
                    this.multiCompile = true;
                    break;
                case IDENTIFIER_BRANCHES:
                    this.handleBranch(param);
                    break;
            }
        }
    }

    private void handleBranch(String content) {
        Map<String, List<String>> map = getParams(content);

        List<String> value = map.get("value");
        Set<String> require = Set.copyOf(map.getOrDefault("constraint", Collections.emptyList()));

        if (value == null) {
            return;
        }

        for (String name : value) {
            if (require.contains(name)) {
                throw new RuntimeException("Circular dependency!");
            }

            this.createBranch(name);
        }

        int mask = 0;

        for (String string : require) {
            if (!this.masks.containsKey(string)) {
                throw new RuntimeException("Undefined identifier " + string + "!");
            }

            mask |= this.masks.getInt(string);
        }

        for (String name : value) {
            this.branches.add(new Branch(name, mask));
        }
    }

    private static Map<String, List<String>> getParams(String content) {
        Matcher matcher = PATTERN_CONTENT_PRAGMA_IDENTIFIER.matcher(content);
        Map<String, List<String>> map = new HashMap<>();
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);

            List<String> values = new ArrayList<>();
            Matcher contextMatcher = PATTERN_WORD.matcher(value);
            while (contextMatcher.find()) {
                values.add(contextMatcher.group());
            }

            map.put(key, values);
        }
        return map;
    }

    private void createBranch(String name) {
        if (this.masks.containsKey(name)) {
            throw new RuntimeException("Duplicated Branches " + name + "!!");
        }

        if (this.masks.size() == 32) {
            throw new RuntimeException("Too many Branches!!");
        }

        int i = this.masks.size();
        this.masks.put(name, 1 << i);
    }

    private final Set<ProgramSource> included = new HashSet<>();

    private List<String> handleInclude(String content) {
        Matcher matcher = PATTERN_STRING.matcher(content);

        if (!matcher.find()) {
            return Collections.emptyList();
        }

        String file = matcher.group(1);
        ProgramSource programSource = this.sourceProvider.getSource(file, "");

        if (programSource == null) {
            throw new RuntimeException("File not found: " + file);
        }

        if (this.included.contains(programSource)) {
            return Collections.emptyList();
        }
        this.included.add(programSource);

        List<String> result = new ArrayList<>();
        processInclude(programSource, result);
        return result;
    }

    private void processInclude(ProgramSource psource, List<String> processed) {
        String source = psource.source();
        Matcher matcher = PATTERN_INCLUDE.matcher(source);

        int left = 0;
        int right;
        while (matcher.find()) {
            right = matcher.start();
            processed.add(source.substring(left, right));
            left = matcher.end();

            String sourceFile = matcher.group(1);

            ProgramSource programSource = sourceProvider.getSource(sourceFile, psource.directory());

            if (programSource == null) {
                throw new RuntimeException("File not found: " + sourceFile);
            }

            if (this.included.contains(programSource)) continue;
            this.included.add(programSource);

             processInclude(programSource, processed);
        }

        processed.add(source.substring(left));

    }

    public void setDefine(String name, boolean value) {
        if (value) {
            this.defines.put(name, true);
        } else {
            this.defines.remove(name);
        }
    }

    public void define(String name, Object value) {
        this.defines.put(name, value);
    }

    public void undefine(String name) {
        this.defines.remove(name);
    }

    private record Branch(
            String name,
            int mask
    ) {}
}

package io.github.gulliby.holefit;

import java.util.ArrayList;
import java.util.List;

/** Reads allocation scripts into validated, line-numbered steps. */
final class Script {
    private Script() {
    }

    static List<Step> parse(String text) throws Api.Failure {
        List<Step> steps = new ArrayList<>();
        String[] lines = text.split("\\n", -1);
        for (int index = 0; index < lines.length; index++) {
            String source = lines[index];
            if (source.endsWith("\r")) {
                source = source.substring(0, source.length() - 1);
            }
            String line = trimFields(source);
            if (line.isEmpty()) {
                continue;
            }
            String[] fields = line.split("[ \\t]+");
            if (fields[0].startsWith("#")) {
                continue;
            }
            int number = index + 1;
            switch (fields[0]) {
                case "alloc" -> {
                    if (fields.length != 3) {
                        throw failure(number, "alloc needs NAME SIZE");
                    }
                    if (!validName(fields[1])) {
                        throw failure(number, "bad name \"" + fields[1] + "\"");
                    }
                    if (!validSize(fields[2])) {
                        throw failure(number, "bad size \"" + fields[2] + "\"");
                    }
                    steps.add(new Alloc(number, fields[1], Integer.parseInt(fields[2])));
                }
                case "free" -> {
                    if (fields.length != 2) {
                        throw failure(number, "free needs NAME");
                    }
                    if (!validName(fields[1])) {
                        throw failure(number, "bad name \"" + fields[1] + "\"");
                    }
                    steps.add(new Free(number, fields[1]));
                }
                case "compact" -> {
                    if (fields.length != 1) {
                        throw failure(number, "compact takes no arguments");
                    }
                    steps.add(new Compact(number));
                }
                default -> throw failure(number, "unknown command " + fields[0]);
            }
        }
        return List.copyOf(steps);
    }

    private static String trimFields(String value) {
        int start = 0;
        int end = value.length();
        while (start < end && isSeparator(value.charAt(start))) {
            start++;
        }
        while (end > start && isSeparator(value.charAt(end - 1))) {
            end--;
        }
        return value.substring(start, end);
    }

    private static boolean isSeparator(char value) {
        return value == ' ' || value == '\t';
    }

    private static boolean validName(String value) {
        if (value.isEmpty() || value.length() > 16) {
            return false;
        }
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            if (!(character >= 'A' && character <= 'Z')
                    && !(character >= 'a' && character <= 'z')
                    && !(character >= '0' && character <= '9')
                    && character != '_') {
                return false;
            }
        }
        return true;
    }

    private static boolean validSize(String value) {
        if (value.isEmpty() || value.length() > 5) {
            return false;
        }
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            if (character < '0' || character > '9') {
                return false;
            }
        }
        return Integer.parseInt(value) >= 1;
    }

    private static Api.Failure failure(int line, String reason) {
        return new Api.Failure(1, "line " + line + ": " + reason);
    }

    sealed interface Step permits Alloc, Free, Compact {
        int line();
    }

    record Alloc(int line, String name, int size) implements Step {
    }

    record Free(int line, String name) implements Step {
    }

    record Compact(int line) implements Step {
    }
}

package io.github.gulliby.holefit;

import io.github.gulliby.holefit.Api.Command;
import io.github.gulliby.holefit.Api.Failure;
import io.github.gulliby.holefit.Api.Options;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Selects commands, validates their options and dispatches their input. */
public final class Cli {
    static final String USAGE = "usage: holefit run [--policy P] [--size N] [--align A]\n       holefit map [--policy P] [--size N] [--align A]\n       holefit stats [--policy P] [--size N] [--align A]\n       holefit compare [--size N] [--align A]\n";

    private Cli() {
    }

    public static boolean readsStdin(List<String> args) {
        if (args.isEmpty() || isInformational(args.get(0))) {
            return false;
        }
        try {
            validate(args);
            return true;
        } catch (Failure failure) {
            return false;
        }
    }

    public static Api.Result run(List<String> args, String stdin) {
        if (args.isEmpty()) {
            return new Api.Result(2, "", USAGE);
        }
        String first = args.get(0);
        if (first.equals("help") || first.equals("-h") || first.equals("--help")) {
            return new Api.Result(0, USAGE, "");
        }
        if (first.equals("--version")) {
            return new Api.Result(0, "holefit 0.1.0\n", "");
        }
        try {
            Invocation invocation = validate(args);
            return new Api.Result(0, invocation.command().handler().run(invocation.options(), stdin), "");
        } catch (Failure failure) {
            return new Api.Result(failure.status, "", "holefit: " + failure.getMessage() + "\n");
        }
    }

    private static Invocation validate(List<String> args) throws Failure {
        Command command = findCommand(args.get(0));
        if (command == null) {
            throw new Failure(2, "unknown command " + args.get(0));
        }
        String policy = "first";
        int size = 64;
        int align = 1;
        Set<String> seen = new HashSet<>();
        for (int index = 1; index < args.size(); index++) {
            String word = args.get(index);
            if (!word.startsWith("-") || word.equals("-")) {
                throw new Failure(2, "unexpected argument " + word);
            }
            if (!command.options().contains(word)) {
                throw new Failure(2, "unknown option " + word);
            }
            if (!seen.add(word)) {
                throw new Failure(2, word + " given twice");
            }
            if (index + 1 >= args.size()) {
                throw invalidValue(word);
            }
            String value = args.get(++index);
            switch (word) {
                case "--policy" -> {
                    if (!value.equals("first") && !value.equals("best")
                            && !value.equals("worst") && !value.equals("next")) {
                        throw invalidValue(word);
                    }
                    policy = value;
                }
                case "--size" -> {
                    if (!isSize(value)) {
                        throw invalidValue(word);
                    }
                    size = Integer.parseInt(value);
                }
                case "--align" -> {
                    if (!value.equals("1") && !value.equals("2") && !value.equals("4")
                            && !value.equals("8") && !value.equals("16")) {
                        throw invalidValue(word);
                    }
                    align = Integer.parseInt(value);
                }
                default -> throw new Failure(2, "unknown option " + word);
            }
        }
        if (size % align != 0) {
            throw new Failure(2, "--size must be a multiple of --align");
        }
        return new Invocation(command, new Options(policy, size, align));
    }

    private static Command findCommand(String name) {
        for (Command command : Registry.COMMANDS) {
            if (command.name().equals(name)) {
                return command;
            }
        }
        return null;
    }

    private static boolean isInformational(String word) {
        return word.equals("help") || word.equals("-h") || word.equals("--help")
                || word.equals("--version");
    }

    private static boolean isSize(String value) {
        if (value.isEmpty() || value.length() > 4) {
            return false;
        }
        for (int index = 0; index < value.length(); index++) {
            char digit = value.charAt(index);
            if (digit < '0' || digit > '9') {
                return false;
            }
        }
        int size = Integer.parseInt(value);
        return size >= 1 && size <= 4096;
    }

    private static Failure invalidValue(String option) {
        return switch (option) {
            case "--policy" -> new Failure(2, "--policy needs first, best, worst or next");
            case "--size" -> new Failure(2, "--size needs an integer from 1 to 4096");
            case "--align" -> new Failure(2, "--align needs 1, 2, 4, 8 or 16");
            default -> new Failure(2, "unknown option " + option);
        };
    }

    private record Invocation(Command command, Options options) {
    }
}

package io.github.gulliby.holefit;

import java.util.Set;

/** The shared types of holefit. */
public final class Api {
    private Api() {
    }

    /** What one command line produced: the exit status and the text for standard output and standard error. */
    public record Result(int status, String out, String err) {
    }

    /** Thrown by a command or the script reader: the exit status and the message without "holefit: " and the line feed. */
    public static final class Failure extends Exception {
        private static final long serialVersionUID = 1L;

        public final int status;

        public Failure(int status, String message) {
            super(message);
            this.status = status;
        }
    }

    /** The options of a command line, with the defaults filled in: policy first, size 64, align 1. */
    public record Options(String policy, int size, int align) {
    }

    /** A command returns the text for standard output or throws Failure. */
    @FunctionalInterface
    public interface Handler {
        String run(Options options, String stdin) throws Failure;
    }

    /** A command: its name, the options it accepts and its handler. */
    public record Command(String name, Set<String> options, Handler handler) {
    }
}

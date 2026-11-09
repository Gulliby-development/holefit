package io.github.gulliby.holefit;

import io.github.gulliby.holefit.Api.Command;
import java.util.List;
import java.util.Set;

/** The command table, in the order of the usage text. */
final class Registry {
    private Registry() {
    }

    static final List<Command> COMMANDS = List.of(
            new Command("run", Set.of("--policy", "--size", "--align"), RunCommand::run),
            new Command("map", Set.of("--policy", "--size", "--align"), MapCommand::run),
            new Command("stats", Set.of("--policy", "--size", "--align"), StatsCommand::run),
            new Command("compare", Set.of("--size", "--align"), CompareCommand::run));
}

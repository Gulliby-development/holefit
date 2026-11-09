package io.github.gulliby.holefit;

import static io.github.gulliby.holefit.Rows.row;

import java.util.List;
import org.junit.jupiter.api.Test;

class CliTest {
    @Test
    void rows() {
        row(List.of(), "", 2, "", "usage: holefit run [--policy P] [--size N] [--align A]\n       holefit map [--policy P] [--size N] [--align A]\n       holefit stats [--policy P] [--size N] [--align A]\n       holefit compare [--size N] [--align A]\n");
        row(List.of("help"), "", 0, "usage: holefit run [--policy P] [--size N] [--align A]\n       holefit map [--policy P] [--size N] [--align A]\n       holefit stats [--policy P] [--size N] [--align A]\n       holefit compare [--size N] [--align A]\n", "");
        row(List.of("-h", "x"), "", 0, "usage: holefit run [--policy P] [--size N] [--align A]\n       holefit map [--policy P] [--size N] [--align A]\n       holefit stats [--policy P] [--size N] [--align A]\n       holefit compare [--size N] [--align A]\n", "");
        row(List.of("--help"), "", 0, "usage: holefit run [--policy P] [--size N] [--align A]\n       holefit map [--policy P] [--size N] [--align A]\n       holefit stats [--policy P] [--size N] [--align A]\n       holefit compare [--size N] [--align A]\n", "");
        row(List.of("--version"), "", 0, "holefit 0.1.0\n", "");
        row(List.of("--version", "x"), "", 0, "holefit 0.1.0\n", "");
        row(List.of("frob"), "", 2, "", "holefit: unknown command frob\n");
        row(List.of(""), "", 2, "", "holefit: unknown command \n");
        row(List.of("Run"), "", 2, "", "holefit: unknown command Run\n");
        row(List.of("run", "extra"), "", 2, "", "holefit: unexpected argument extra\n");
        row(List.of("stats", "-x"), "", 2, "", "holefit: unknown option -x\n");
        row(List.of("compare", "--policy", "best"), "", 2, "", "holefit: unknown option --policy\n");
    }
}

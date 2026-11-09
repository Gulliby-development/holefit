package io.github.gulliby.holefit;

import static io.github.gulliby.holefit.Rows.row;

import java.util.List;
import org.junit.jupiter.api.Test;

class ScriptTest {
    @Test
    void rows() {
        row(List.of("run"), "", 0, "", "");
        row(List.of("run"), "\n   \n# note\nalloc A 4\n\n", 0, "alloc A 4 -> 0\n", "");
        row(List.of("run"), "alloc A 4\r\nfree A\r\n", 0, "alloc A 4 -> 0\nfree A -> 4\n", "");
        row(List.of("run"), "  alloc\tA  4  \n\tfree A", 0, "alloc A 4 -> 0\nfree A -> 4\n", "");
        row(List.of("run"), "alloc A 4 # no\n", 1, "", "holefit: line 1: alloc needs NAME SIZE\n");
        row(List.of("run"), "# alloc A\n  # free A\ncompact\n", 0, "compact -> moved 0\n", "");
        row(List.of("run"), "alloc A 4\ngrow A 8\n", 1, "", "holefit: line 2: unknown command grow\n");
        row(List.of("run"), "alloc A\n", 1, "", "holefit: line 1: alloc needs NAME SIZE\n");
        row(List.of("run"), "alloc a-b 4\n", 1, "", "holefit: line 1: bad name \"a-b\"\n");
        row(List.of("run"), "alloc aaaaaaaaaaaaaaaaa 4\n", 1, "", "holefit: line 1: bad name \"aaaaaaaaaaaaaaaaa\"\n");
        row(List.of("run"), "alloc aaaaaaaaaaaaaaaa 4\n", 0, "alloc aaaaaaaaaaaaaaaa 4 -> 0\n", "");
        row(List.of("run"), "alloc A 0\n", 1, "", "holefit: line 1: bad size \"0\"\n");
        row(List.of("run"), "alloc A 100000\n", 1, "", "holefit: line 1: bad size \"100000\"\n");
        row(List.of("run"), "alloc A 99999\n", 0, "alloc A 99999 -> fail (too big)\n", "");
        row(List.of("run"), "alloc A 007\n", 0, "alloc A 7 -> 0\n", "");
        row(List.of("run"), "alloc A 4\nalloc A 4\n", 1, "", "holefit: line 2: A is already allocated\n");
        row(List.of("run"), "alloc A 4\nfree A\nfree A\n", 1, "", "holefit: line 3: A is not allocated\n");
        row(List.of("run"), "free A\n", 1, "", "holefit: line 1: A is not allocated\n");
    }
}

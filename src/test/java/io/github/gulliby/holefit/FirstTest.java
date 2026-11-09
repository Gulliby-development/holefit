package io.github.gulliby.holefit;

import static io.github.gulliby.holefit.Rows.row;

import java.util.List;
import org.junit.jupiter.api.Test;

class FirstTest {
    @Test
    void rows() {
        row(List.of("run", "--size", "32"), "alloc A 8\nalloc B 4\nalloc C 6\nalloc D 4\nfree A\nfree C\nalloc E 5\nalloc F 3\n", 0, "alloc A 8 -> 0\nalloc B 4 -> 8\nalloc C 6 -> 12\nalloc D 4 -> 18\nfree A -> 8\nfree C -> 6\nalloc E 5 -> 0\nalloc F 3 -> 5\n", "");
        row(List.of("run", "--size", "32"), "alloc A 8\nalloc B 4\nalloc C 6\nalloc D 4\nfree A\nfree C\nalloc E 8\nalloc F 8\n", 0, "alloc A 8 -> 0\nalloc B 4 -> 8\nalloc C 6 -> 12\nalloc D 4 -> 18\nfree A -> 8\nfree C -> 6\nalloc E 8 -> 0\nalloc F 8 -> 22\n", "");
        row(List.of("run", "--size", "32"), "alloc A 8\nalloc B 4\nalloc C 6\nalloc D 4\nfree A\nfree C\nalloc E 11\n", 0, "alloc A 8 -> 0\nalloc B 4 -> 8\nalloc C 6 -> 12\nalloc D 4 -> 18\nfree A -> 8\nfree C -> 6\nalloc E 11 -> fail (fragmented)\n", "");
        row(List.of("run", "--size", "32"), "alloc A 8\nalloc B 4\nalloc C 6\nalloc D 4\nfree A\nfree C\nalloc E 10\nalloc F 6\n", 0, "alloc A 8 -> 0\nalloc B 4 -> 8\nalloc C 6 -> 12\nalloc D 4 -> 18\nfree A -> 8\nfree C -> 6\nalloc E 10 -> 22\nalloc F 6 -> 0\n", "");
        row(List.of("run"), "alloc A 10\nalloc B 20\nalloc C 5\nfree B\nalloc D 8\n", 0, "alloc A 10 -> 0\nalloc B 20 -> 10\nalloc C 5 -> 30\nfree B -> 20\nalloc D 8 -> 10\n", "");
        row(List.of("run", "--size", "16"), "alloc A 8\nalloc B 8\nalloc C 1\n", 0, "alloc A 8 -> 0\nalloc B 8 -> 8\nalloc C 1 -> fail (no space)\n", "");
        row(List.of("run", "--size", "16"), "alloc A 6\nalloc B 4\nalloc C 6\nfree B\nalloc D 5\nalloc E 5\n", 0, "alloc A 6 -> 0\nalloc B 4 -> 6\nalloc C 6 -> 10\nfree B -> 4\nalloc D 5 -> fail (no space)\nalloc E 5 -> fail (no space)\n", "");
        row(List.of("run", "--size", "16"), "alloc A 5\nalloc B 5\nalloc C 5\nfree A\nfree C\nalloc D 7\n", 0, "alloc A 5 -> 0\nalloc B 5 -> 5\nalloc C 5 -> 10\nfree A -> 5\nfree C -> 5\nalloc D 7 -> fail (fragmented)\n", "");
    }
}

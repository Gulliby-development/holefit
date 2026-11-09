package io.github.gulliby.holefit;

import static io.github.gulliby.holefit.Rows.row;

import java.util.List;
import org.junit.jupiter.api.Test;

class NextTest {
    @Test
    void rows() {
        row(List.of("run", "--policy", "next", "--size", "32"), "alloc A 8\nalloc B 4\nalloc C 6\nalloc D 4\nfree A\nfree C\nalloc E 5\nalloc F 5\n", 0, "alloc A 8 -> 0\nalloc B 4 -> 8\nalloc C 6 -> 12\nalloc D 4 -> 18\nfree A -> 8\nfree C -> 6\nalloc E 5 -> 22\nalloc F 5 -> 27\n", "");
        row(List.of("run", "--policy", "next", "--size", "32"), "alloc A 8\nalloc B 8\nfree A\nalloc C 4\n", 0, "alloc A 8 -> 0\nalloc B 8 -> 8\nfree A -> 8\nalloc C 4 -> 16\n", "");
        row(List.of("run", "--policy", "next", "--size", "32"), "alloc A 8\nalloc B 8\nalloc C 8\nfree A\nalloc D 4\nalloc E 4\n", 0, "alloc A 8 -> 0\nalloc B 8 -> 8\nalloc C 8 -> 16\nfree A -> 8\nalloc D 4 -> 24\nalloc E 4 -> 28\n", "");
        row(List.of("run", "--policy", "next", "--size", "16"), "alloc A 8\nalloc B 4\nfree A\nalloc C 4\nalloc D 4\nalloc E 4\n", 0, "alloc A 8 -> 0\nalloc B 4 -> 8\nfree A -> 8\nalloc C 4 -> 12\nalloc D 4 -> 0\nalloc E 4 -> 4\n", "");
        row(List.of("run", "--policy", "next", "--size", "16"), "alloc A 4\nalloc B 4\nalloc C 4\nalloc D 4\nfree B\nfree C\nalloc E 8\n", 0, "alloc A 4 -> 0\nalloc B 4 -> 4\nalloc C 4 -> 8\nalloc D 4 -> 12\nfree B -> 4\nfree C -> 4\nalloc E 8 -> 4\n", "");
        row(List.of("run", "--policy", "next", "--size", "32"), "alloc A 4\nalloc B 4\nalloc C 4\nfree C\nalloc D 2\n", 0, "alloc A 4 -> 0\nalloc B 4 -> 4\nalloc C 4 -> 8\nfree C -> 4\nalloc D 2 -> 8\n", "");
        row(List.of("run", "--policy", "next", "--size", "16"), "alloc A 8\nalloc B 8\nfree B\nalloc C 4\nalloc D 4\nalloc E 4\n", 0, "alloc A 8 -> 0\nalloc B 8 -> 8\nfree B -> 8\nalloc C 4 -> 8\nalloc D 4 -> 12\nalloc E 4 -> fail (no space)\n", "");
        row(List.of("run", "--policy", "next", "--size", "16"), "alloc A 6\nalloc B 4\nfree B\nalloc C 12\n", 0, "alloc A 6 -> 0\nalloc B 4 -> 6\nfree B -> 4\nalloc C 12 -> fail (no space)\n", "");
        row(List.of("run", "--policy", "next", "--size", "16"), "alloc A 6\nalloc B 4\nfree A\nalloc C 12\n", 0, "alloc A 6 -> 0\nalloc B 4 -> 6\nfree A -> 6\nalloc C 12 -> fail (fragmented)\n", "");
        row(List.of("run", "--policy", "next", "--size", "32"), "alloc A 4\nalloc B 4\nalloc C 4\nalloc D 4\nalloc E 4\nfree C\nalloc F 4\nalloc G 4\n", 0, "alloc A 4 -> 0\nalloc B 4 -> 4\nalloc C 4 -> 8\nalloc D 4 -> 12\nalloc E 4 -> 16\nfree C -> 4\nalloc F 4 -> 20\nalloc G 4 -> 24\n", "");
        row(List.of("map", "--policy", "next", "--size", "16"), "alloc A 8\nalloc B 4\nalloc C 4\nfree A\nalloc D 2\nfree C\nalloc E 4\n", 0, "0+2 D\n2+4 E\n6+2 free\n8+4 B\n12+4 free\n", "");
        row(List.of("run", "--policy", "next", "--size", "16"), "alloc A 4\nalloc B 4\ncompact\nalloc C 2\n", 0, "alloc A 4 -> 0\nalloc B 4 -> 4\ncompact -> moved 0\nalloc C 2 -> 8\n", "");
    }
}

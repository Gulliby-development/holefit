package io.github.gulliby.holefit;

import static io.github.gulliby.holefit.Rows.row;

import java.util.List;
import org.junit.jupiter.api.Test;

class CompactTest {
    @Test
    void rows() {
        row(List.of("run", "--size", "32"), "alloc A 8\nalloc B 4\nalloc C 6\nalloc D 4\nfree A\nfree C\ncompact\n", 0, "alloc A 8 -> 0\nalloc B 4 -> 8\nalloc C 6 -> 12\nalloc D 4 -> 18\nfree A -> 8\nfree C -> 6\ncompact -> moved 2\n", "");
        row(List.of("run", "--size", "32"), "alloc A 8\nalloc B 4\nalloc C 6\nalloc D 4\nfree A\nfree C\ncompact\nalloc E 20\n", 0, "alloc A 8 -> 0\nalloc B 4 -> 8\nalloc C 6 -> 12\nalloc D 4 -> 18\nfree A -> 8\nfree C -> 6\ncompact -> moved 2\nalloc E 20 -> 8\n", "");
        row(List.of("run", "--size", "32"), "alloc A 8\nalloc B 4\nalloc C 6\nalloc D 4\nfree A\nfree C\nalloc E 20\ncompact\nalloc F 20\n", 0, "alloc A 8 -> 0\nalloc B 4 -> 8\nalloc C 6 -> 12\nalloc D 4 -> 18\nfree A -> 8\nfree C -> 6\nalloc E 20 -> fail (fragmented)\ncompact -> moved 2\nalloc F 20 -> 8\n", "");
        row(List.of("map", "--size", "32"), "alloc A 8\nalloc B 4\nalloc C 6\nalloc D 4\nfree A\nfree C\ncompact\n", 0, "0+4 B\n4+4 D\n8+24 free\n", "");
        row(List.of("run", "--size", "16"), "alloc A 8\nalloc B 8\ncompact\n", 0, "alloc A 8 -> 0\nalloc B 8 -> 8\ncompact -> moved 0\n", "");
        row(List.of("run", "--size", "16"), "alloc A 4\nalloc B 4\nfree A\ncompact\ncompact\n", 0, "alloc A 4 -> 0\nalloc B 4 -> 4\nfree A -> 4\ncompact -> moved 1\ncompact -> moved 0\n", "");
        row(List.of("run", "--size", "16"), "alloc A 4\nalloc B 4\nalloc C 4\nfree B\ncompact\n", 0, "alloc A 4 -> 0\nalloc B 4 -> 4\nalloc C 4 -> 8\nfree B -> 4\ncompact -> moved 1\n", "");
        row(List.of("map", "--size", "16"), "alloc A 4\nalloc B 4\nalloc C 4\nfree A\nfree C\ncompact\nalloc D 1\n", 0, "0+4 B\n4+1 D\n5+11 free\n", "");
        row(List.of("run", "--policy", "next", "--size", "16"), "alloc A 4\nalloc B 4\nalloc C 4\nfree A\ncompact\nalloc D 2\nfree D\nalloc E 2\n", 0, "alloc A 4 -> 0\nalloc B 4 -> 4\nalloc C 4 -> 8\nfree A -> 4\ncompact -> moved 2\nalloc D 2 -> 8\nfree D -> 2\nalloc E 2 -> 8\n", "");
    }
}

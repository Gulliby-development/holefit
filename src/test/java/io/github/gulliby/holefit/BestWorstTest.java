package io.github.gulliby.holefit;

import static io.github.gulliby.holefit.Rows.row;

import java.util.List;
import org.junit.jupiter.api.Test;

class BestWorstTest {
    @Test
    void rows() {
        row(List.of("run", "--policy", "best", "--size", "32"), "alloc A 8\nalloc B 4\nalloc C 6\nalloc D 4\nfree A\nfree C\nalloc E 5\n", 0, "alloc A 8 -> 0\nalloc B 4 -> 8\nalloc C 6 -> 12\nalloc D 4 -> 18\nfree A -> 8\nfree C -> 6\nalloc E 5 -> 12\n", "");
        row(List.of("run", "--policy", "best", "--size", "32"), "alloc A 8\nalloc B 4\nalloc C 6\nalloc D 4\nfree A\nfree C\nalloc E 7\nalloc F 6\n", 0, "alloc A 8 -> 0\nalloc B 4 -> 8\nalloc C 6 -> 12\nalloc D 4 -> 18\nfree A -> 8\nfree C -> 6\nalloc E 7 -> 0\nalloc F 6 -> 12\n", "");
        row(List.of("run", "--policy", "best", "--size", "32"), "alloc A 8\nalloc B 4\nalloc C 6\nalloc D 4\nfree A\nfree C\nalloc E 6\nalloc F 6\n", 0, "alloc A 8 -> 0\nalloc B 4 -> 8\nalloc C 6 -> 12\nalloc D 4 -> 18\nfree A -> 8\nfree C -> 6\nalloc E 6 -> 12\nalloc F 6 -> 0\n", "");
        row(List.of("run", "--policy", "best", "--size", "32"), "alloc A 8\nalloc B 4\nalloc C 6\nalloc D 4\nfree A\nfree C\nalloc E 11\n", 0, "alloc A 8 -> 0\nalloc B 4 -> 8\nalloc C 6 -> 12\nalloc D 4 -> 18\nfree A -> 8\nfree C -> 6\nalloc E 11 -> fail (fragmented)\n", "");
        row(List.of("run", "--policy", "worst", "--size", "32"), "alloc A 8\nalloc B 4\nalloc C 6\nalloc D 4\nfree A\nfree C\nalloc E 5\n", 0, "alloc A 8 -> 0\nalloc B 4 -> 8\nalloc C 6 -> 12\nalloc D 4 -> 18\nfree A -> 8\nfree C -> 6\nalloc E 5 -> 22\n", "");
        row(List.of("run", "--policy", "worst", "--size", "32"), "alloc A 8\nalloc B 4\nalloc C 6\nalloc D 4\nfree A\nfree C\nalloc E 5\nalloc F 5\nalloc G 5\n", 0, "alloc A 8 -> 0\nalloc B 4 -> 8\nalloc C 6 -> 12\nalloc D 4 -> 18\nfree A -> 8\nfree C -> 6\nalloc E 5 -> 22\nalloc F 5 -> 0\nalloc G 5 -> 12\n", "");
        row(List.of("run", "--policy", "worst", "--size", "32"), "alloc A 8\nalloc B 4\nalloc C 6\nalloc D 4\nfree A\nfree C\nalloc E 11\n", 0, "alloc A 8 -> 0\nalloc B 4 -> 8\nalloc C 6 -> 12\nalloc D 4 -> 18\nfree A -> 8\nfree C -> 6\nalloc E 11 -> fail (fragmented)\n", "");
        row(List.of("map", "--policy", "worst"), "alloc A 10\nalloc B 20\nalloc C 5\nfree B\nalloc D 8\n", 0, "0+10 A\n10+20 free\n30+5 C\n35+8 D\n43+21 free\n", "");
        row(List.of("map", "--policy", "best"), "alloc A 10\nalloc B 20\nalloc C 5\nfree B\nalloc D 8\n", 0, "0+10 A\n10+8 D\n18+12 free\n30+5 C\n35+29 free\n", "");
    }
}

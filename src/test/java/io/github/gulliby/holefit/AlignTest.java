package io.github.gulliby.holefit;

import static io.github.gulliby.holefit.Rows.row;

import java.util.List;
import org.junit.jupiter.api.Test;

class AlignTest {
    @Test
    void rows() {
        row(List.of("run", "--align", "4", "--size", "32"), "alloc A 1\nalloc B 4\nalloc C 5\nalloc D 9\n", 0, "alloc A 1 -> 0\nalloc B 4 -> 4\nalloc C 5 -> 8\nalloc D 9 -> 16\n", "");
        row(List.of("run", "--align", "8", "--size", "32"), "alloc A 1\nalloc B 8\nalloc C 9\nfree A\nfree C\n", 0, "alloc A 1 -> 0\nalloc B 8 -> 8\nalloc C 9 -> 16\nfree A -> 8\nfree C -> 16\n", "");
        row(List.of("map", "--align", "8", "--size", "32"), "alloc A 3\nalloc B 3\nalloc C 3\n", 0, "0+8 A\n8+8 B\n16+8 C\n24+8 free\n", "");
        row(List.of("run", "--align", "4", "--size", "16"), "alloc A 13\nalloc B 4\n", 0, "alloc A 13 -> 0\nalloc B 4 -> fail (no space)\n", "");
        row(List.of("run", "--align", "4", "--size", "16"), "alloc A 17\n", 0, "alloc A 17 -> fail (too big)\n", "");
        row(List.of("run", "--align", "2", "--size", "9"), "alloc A 1\n", 2, "", "holefit: --size must be a multiple of --align\n");
        row(List.of("run", "--align", "2", "--size", "10"), "alloc A 3\nalloc B 3\nalloc C 3\nalloc D 3\n", 0, "alloc A 3 -> 0\nalloc B 3 -> 4\nalloc C 3 -> fail (no space)\nalloc D 3 -> fail (no space)\n", "");
        row(List.of("stats", "--align", "4", "--size", "32"), "alloc A 1\nalloc B 2\nalloc C 3\nfree B\n", 0, "size 32\nused 8\nfree 24\nblocks 2\nholes 2\nlargest 20\nfails 0\nfrag 16\n", "");
    }
}

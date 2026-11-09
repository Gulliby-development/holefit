package io.github.gulliby.holefit;

import static io.github.gulliby.holefit.Rows.row;

import java.util.List;
import org.junit.jupiter.api.Test;

class ReportTest {
    @Test
    void rows() {
        row(List.of("map"), "", 0, "0+64 free\n", "");
        row(List.of("map", "--size", "8"), "alloc A 3\nalloc B 2\nfree A\n", 0, "0+3 free\n3+2 B\n5+3 free\n", "");
        row(List.of("map", "--size", "8"), "alloc A 8\n", 0, "0+8 A\n", "");
        row(List.of("map"), "alloc A 10\nalloc B 20\nalloc C 5\nfree B\nalloc D 8\nfree C\n", 0, "0+10 A\n10+8 D\n18+46 free\n", "");
        row(List.of("stats"), "", 0, "size 64\nused 0\nfree 64\nblocks 0\nholes 1\nlargest 64\nfails 0\nfrag 0\n", "");
        row(List.of("stats"), "alloc A 10\nalloc B 20\nalloc C 5\nfree B\nalloc D 8\n", 0, "size 64\nused 23\nfree 41\nblocks 3\nholes 2\nlargest 29\nfails 0\nfrag 29\n", "");
        row(List.of("stats", "--size", "32"), "alloc A 8\nalloc B 4\nalloc C 6\nalloc D 4\nfree A\nfree C\n", 0, "size 32\nused 8\nfree 24\nblocks 2\nholes 3\nlargest 10\nfails 0\nfrag 58\n", "");
        row(List.of("stats", "--size", "8"), "alloc A 8\n", 0, "size 8\nused 8\nfree 0\nblocks 1\nholes 0\nlargest 0\nfails 0\nfrag 0\n", "");
        row(List.of("stats", "--size", "8"), "alloc A 9\nalloc B 4\nalloc C 5\n", 0, "size 8\nused 4\nfree 4\nblocks 1\nholes 1\nlargest 4\nfails 2\nfrag 0\n", "");
        row(List.of("compare"), "", 0, "first fails=0 holes=1 largest=64\nbest fails=0 holes=1 largest=64\nworst fails=0 holes=1 largest=64\nnext fails=0 holes=1 largest=64\n", "");
        row(List.of("compare", "--size", "32"), "alloc A 8\nalloc B 4\nalloc C 6\nalloc D 4\nfree A\nfree C\nalloc E 5\nalloc F 5\n", 0, "first fails=0 holes=3 largest=10\nbest fails=0 holes=3 largest=10\nworst fails=0 holes=3 largest=6\nnext fails=0 holes=2 largest=8\n", "");
        row(List.of("compare", "--size", "16"), "alloc A 4\nalloc B 4\nalloc C 4\nalloc D 4\nfree A\nfree C\nalloc E 4\nalloc F 6\n", 0, "first fails=1 holes=1 largest=4\nbest fails=1 holes=1 largest=4\nworst fails=1 holes=1 largest=4\nnext fails=1 holes=1 largest=4\n", "");
    }
}

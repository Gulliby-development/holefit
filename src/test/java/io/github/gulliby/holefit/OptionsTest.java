package io.github.gulliby.holefit;

import static io.github.gulliby.holefit.Rows.row;

import java.util.List;
import org.junit.jupiter.api.Test;

class OptionsTest {
    @Test
    void rows() {
        row(List.of("run", "--policy", "fast"), "", 2, "", "holefit: --policy needs first, best, worst or next\n");
        row(List.of("run", "--policy"), "", 2, "", "holefit: --policy needs first, best, worst or next\n");
        row(List.of("map", "--size", "0"), "", 2, "", "holefit: --size needs an integer from 1 to 4096\n");
        row(List.of("map", "--size", "4097"), "", 2, "", "holefit: --size needs an integer from 1 to 4096\n");
        row(List.of("stats", "--size", "12345"), "", 2, "", "holefit: --size needs an integer from 1 to 4096\n");
        row(List.of("stats", "--size", "-8"), "", 2, "", "holefit: --size needs an integer from 1 to 4096\n");
        row(List.of("run", "--align", "3"), "", 2, "", "holefit: --align needs 1, 2, 4, 8 or 16\n");
        row(List.of("run", "--align"), "", 2, "", "holefit: --align needs 1, 2, 4, 8 or 16\n");
        row(List.of("run", "--policy", "first", "--policy", "best"), "", 2, "", "holefit: --policy given twice\n");
        row(List.of("run", "--size", "10", "--align", "4"), "", 2, "", "holefit: --size must be a multiple of --align\n");
        row(List.of("run", "--size", "4096", "--align", "16"), "alloc A 4096\n", 0, "alloc A 4096 -> 0\n", "");
        row(List.of("run", "--size", "04", "--align", "2"), "alloc A 3\n", 0, "alloc A 3 -> 0\n", "");
    }
}

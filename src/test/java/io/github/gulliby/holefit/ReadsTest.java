package io.github.gulliby.holefit;

import static io.github.gulliby.holefit.Rows.reads;

import java.util.List;
import org.junit.jupiter.api.Test;

class ReadsTest {
    @Test
    void rows() {
        reads(List.of("run"), true);
        reads(List.of(), false);
        reads(List.of("help"), false);
        reads(List.of("map", "--policy", "next"), true);
        reads(List.of("stats", "--size", "4097"), false);
        reads(List.of("compare", "--align", "16"), true);
        reads(List.of("compare", "--policy", "next"), false);
        reads(List.of("run", "--size", "10", "--align", "4"), false);
    }
}

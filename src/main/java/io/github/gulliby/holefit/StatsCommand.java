package io.github.gulliby.holefit;

import io.github.gulliby.holefit.Api.Failure;
import io.github.gulliby.holefit.Api.Options;
import java.util.List;

/** Replays allocation scripts and formats their final memory statistics. */
final class StatsCommand {
    private StatsCommand() {
    }

    static String run(Options options, String stdin) throws Failure {
        List<Script.Step> steps = Script.parse(stdin);
        Heap heap = new Heap(options);
        for (Script.Step step : steps) {
            heap.execute(step);
        }
        Heap.Metrics metrics = heap.metrics();
        return new StringBuilder()
                .append("size ").append(metrics.size()).append('\n')
                .append("used ").append(metrics.used()).append('\n')
                .append("free ").append(metrics.free()).append('\n')
                .append("blocks ").append(metrics.blocks()).append('\n')
                .append("holes ").append(metrics.holes()).append('\n')
                .append("largest ").append(metrics.largest()).append('\n')
                .append("fails ").append(metrics.fails()).append('\n')
                .append("frag ").append(metrics.frag()).append('\n')
                .toString();
    }
}

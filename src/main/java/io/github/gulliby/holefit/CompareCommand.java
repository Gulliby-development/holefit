package io.github.gulliby.holefit;

import io.github.gulliby.holefit.Api.Failure;
import io.github.gulliby.holefit.Api.Options;
import java.util.List;

/** Replays allocation scripts independently under each placement policy. */
final class CompareCommand {
    private CompareCommand() {
    }

    static String run(Options options, String stdin) throws Failure {
        List<Script.Step> steps = Script.parse(stdin);
        List<String> policies = List.of("first", "best", "worst", "next");
        StringBuilder output = new StringBuilder();
        for (String policy : policies) {
            Heap heap = new Heap(new Options(policy, options.size(), options.align()));
            for (Script.Step step : steps) {
                heap.execute(step);
            }
            Heap.Metrics metrics = heap.metrics();
            output.append(policy).append(" fails=").append(metrics.fails())
                    .append(" holes=").append(metrics.holes())
                    .append(" largest=").append(metrics.largest()).append('\n');
        }
        return output.toString();
    }
}

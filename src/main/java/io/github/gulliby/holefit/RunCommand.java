package io.github.gulliby.holefit;

import io.github.gulliby.holefit.Api.Failure;
import io.github.gulliby.holefit.Api.Options;

/** Replays allocation scripts and formats their execution trace. */
final class RunCommand {
    private RunCommand() {
    }

    static String run(Options options, String stdin) throws Failure {
        java.util.List<Script.Step> steps = Script.parse(stdin);
        Heap heap = new Heap(options);
        StringBuilder output = new StringBuilder();
        for (Script.Step step : steps) {
            Heap.Outcome outcome = heap.execute(step);
            if (step instanceof Script.Alloc alloc) {
                output.append("alloc ").append(alloc.name()).append(' ').append(alloc.size()).append(" -> ");
                if (outcome instanceof Heap.Allocated allocated) {
                    output.append(allocated.address());
                } else if (outcome instanceof Heap.Rejected rejected) {
                    output.append("fail (").append(rejected.reason()).append(')');
                }
            } else if (step instanceof Script.Free free) {
                Heap.Freed freed = (Heap.Freed) outcome;
                output.append("free ").append(free.name()).append(" -> ").append(freed.length());
            } else {
                Heap.Compacted compacted = (Heap.Compacted) outcome;
                output.append("compact -> moved ").append(compacted.moved());
            }
            output.append('\n');
        }
        return output.toString();
    }
}

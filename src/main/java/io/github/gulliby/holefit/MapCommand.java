package io.github.gulliby.holefit;

import io.github.gulliby.holefit.Api.Failure;
import io.github.gulliby.holefit.Api.Options;

/** Replays allocation scripts and formats the complete memory map. */
final class MapCommand {
    private MapCommand() {
    }

    static String run(Options options, String stdin) throws Failure {
        java.util.List<Script.Step> steps = Script.parse(stdin);
        Heap heap = new Heap(options);
        for (Script.Step step : steps) {
            heap.execute(step);
        }

        java.util.List<Heap.Block> blocks = heap.blocks();
        java.util.List<Heap.Hole> holes = heap.holes();
        StringBuilder output = new StringBuilder();
        int blockIndex = 0;
        int holeIndex = 0;
        while (blockIndex < blocks.size() || holeIndex < holes.size()) {
            if (holeIndex >= holes.size()
                    || (blockIndex < blocks.size()
                    && blocks.get(blockIndex).start() < holes.get(holeIndex).start())) {
                Heap.Block block = blocks.get(blockIndex++);
                output.append(block.start()).append('+').append(block.length()).append(' ')
                        .append(block.name()).append('\n');
            } else {
                Heap.Hole hole = holes.get(holeIndex++);
                output.append(hole.start()).append('+').append(hole.length()).append(" free\n");
            }
        }
        return output.toString();
    }
}

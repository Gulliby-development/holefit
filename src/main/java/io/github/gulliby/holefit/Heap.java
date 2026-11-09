package io.github.gulliby.holefit;

import java.util.ArrayList;
import java.util.List;

/** Simulates allocated blocks and free holes under a selected placement policy. */
final class Heap {
    private final int size;
    private final int align;
    private final String policy;
    private final List<Block> allocated = new ArrayList<>();
    private int rover;
    private int fails;

    Heap(Api.Options options) {
        size = options.size();
        align = options.align();
        policy = options.policy();
    }

    Outcome execute(Script.Step step) throws Api.Failure {
        if (step instanceof Script.Alloc alloc) {
            return allocate(alloc);
        }
        if (step instanceof Script.Free free) {
            return release(free);
        }
        return compact();
    }

    List<Block> blocks() {
        return List.copyOf(allocated);
    }

    List<Hole> holes() {
        List<Hole> result = new ArrayList<>();
        int cursor = 0;
        for (Block block : allocated) {
            if (block.start() > cursor) {
                result.add(new Hole(cursor, block.start() - cursor));
            }
            cursor = block.start() + block.length();
        }
        if (cursor < size) {
            result.add(new Hole(cursor, size - cursor));
        }
        return List.copyOf(result);
    }

    Metrics metrics() {
        int used = 0;
        for (Block block : allocated) {
            used += block.length();
        }
        List<Hole> currentHoles = holes();
        int largest = 0;
        for (Hole hole : currentHoles) {
            largest = Math.max(largest, hole.length());
        }
        int free = size - used;
        int frag = free == 0 ? 0 : (free - largest) * 100 / free;
        return new Metrics(size, used, free, allocated.size(), currentHoles.size(),
                largest, fails, frag);
    }

    int rover() {
        return rover;
    }

    private Outcome allocate(Script.Alloc alloc) throws Api.Failure {
        if (containsName(alloc.name())) {
            throw runtimeFailure(alloc.line(), alloc.name() + " is already allocated");
        }
        int need = ((alloc.size() + align - 1) / align) * align;
        if (need > size) {
            fails++;
            return new Rejected("too big");
        }
        List<Hole> currentHoles = holes();
        Hole chosen = choose(currentHoles, need);
        if (chosen == null) {
            fails++;
            int free = size;
            for (Block block : allocated) {
                free -= block.length();
            }
            return new Rejected(free >= need ? "fragmented" : "no space");
        }
        Block block = new Block(alloc.name(), chosen.start(), need);
        int index = 0;
        while (index < allocated.size() && allocated.get(index).start() < block.start()) {
            index++;
        }
        allocated.add(index, block);
        rover = block.start() + need;
        return new Allocated(block.start(), need);
    }

    private Outcome release(Script.Free free) throws Api.Failure {
        for (int index = 0; index < allocated.size(); index++) {
            Block block = allocated.get(index);
            if (block.name().equals(free.name())) {
                allocated.remove(index);
                return new Freed(block.length());
            }
        }
        throw runtimeFailure(free.line(), free.name() + " is not allocated");
    }

    private Outcome compact() {
        int cursor = 0;
        int moved = 0;
        List<Block> packed = new ArrayList<>();
        for (Block block : allocated) {
            if (block.start() != cursor) {
                moved++;
            }
            packed.add(new Block(block.name(), cursor, block.length()));
            cursor += block.length();
        }
        allocated.clear();
        allocated.addAll(packed);
        rover = 0;
        return new Compacted(moved);
    }

    private Hole choose(List<Hole> currentHoles, int need) {
        Hole chosen = null;
        if (policy.equals("next")) {
            for (int group = 0; group < 2; group++) {
                for (Hole hole : currentHoles) {
                    boolean inGroup = group == 0 ? hole.start() >= rover : hole.start() < rover;
                    if (inGroup && hole.length() >= need) {
                        return hole;
                    }
                }
            }
            return null;
        }
        for (Hole hole : currentHoles) {
            if (hole.length() < need) {
                continue;
            }
            if (chosen == null || preferred(hole, chosen)) {
                chosen = hole;
            }
        }
        return chosen;
    }

    private boolean preferred(Hole candidate, Hole current) {
        return switch (policy) {
            case "best" -> candidate.length() < current.length()
                    || (candidate.length() == current.length() && candidate.start() < current.start());
            case "worst" -> candidate.length() > current.length()
                    || (candidate.length() == current.length() && candidate.start() < current.start());
            default -> candidate.start() < current.start();
        };
    }

    private boolean containsName(String name) {
        for (Block block : allocated) {
            if (block.name().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private static Api.Failure runtimeFailure(int line, String reason) {
        return new Api.Failure(1, "line " + line + ": " + reason);
    }

    record Block(String name, int start, int length) {
    }

    record Hole(int start, int length) {
    }

    record Metrics(int size, int used, int free, int blocks, int holes, int largest, int fails, int frag) {
    }

    sealed interface Outcome permits Allocated, Rejected, Freed, Compacted {
    }

    record Allocated(int address, int length) implements Outcome {
    }

    record Rejected(String reason) implements Outcome {
    }

    record Freed(int length) implements Outcome {
    }

    record Compacted(int moved) implements Outcome {
    }
}

<p align="center"><img src="docs/logo.svg" width="96" alt="" /></p>

# holefit

[![CI](https://github.com/Gulliby-development/holefit/actions/workflows/ci.yml/badge.svg)](https://github.com/Gulliby-development/holefit/actions/workflows/ci.yml) [![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

holefit is a Java command-line simulator for replaying allocation scripts against a fixed-size memory. It demonstrates how first-fit, best-fit, worst-fit and next-fit placement affect block addresses, allocation failures and fragmentation. It is intended for students studying memory management, authors of operating-systems exercises, and people evaluating pool-allocation strategies in scripts or CI.

## Requirements and quick start

Build and run the test suite with Java 21 and Maven:

```sh
mvn -B verify
```

The runtime uses only the Java standard library. From the repository directory, run the program with:

```sh
java -cp target/classes io.github.gulliby.holefit.Main COMMAND ...
```

The command synopsis is:

```text
usage: holefit run [--policy P] [--size N] [--align A]
       holefit map [--policy P] [--size N] [--align A]
       holefit stats [--policy P] [--size N] [--align A]
       holefit compare [--size N] [--align A]
```

All commands read a UTF-8 script from standard input. The defaults are policy `first`, memory size 64 cells, and alignment 1. The `run`, `map`, and `stats` commands accept `--policy` with `first`, `best`, `worst`, or `next`; `compare` runs all four policies and does not accept that option. All commands accept `--size` (a decimal integer from 1 through 4096) and `--align` (1, 2, 4, 8, or 16). Values are separate words after their options, leading zeroes are accepted for size, and options can appear in any order but not more than once. A size value must be one to four decimal digits with no sign; leading zeroes are allowed. Size must be a multiple of alignment.

Arguments are validated from left to right. A positional word produces `holefit: unexpected argument WORD`; an unsupported option produces `holefit: unknown option WORD`; repeating an option produces `holefit: OPTION given twice`. Missing or invalid values produce the corresponding `--policy needs first, best, worst or next`, `--size needs an integer from 1 to 4096`, or `--align needs 1, 2, 4, 8 or 16` message, prefixed with `holefit: `. An unknown first word produces `holefit: unknown command WORD` (with an empty word when the first argument is empty). A final size/alignment mismatch produces `holefit: --size must be a multiple of --align`. Command-line errors exit 2; these errors do not read standard input.

With no command, usage is written to standard error and the process exits 2. `help`, `-h`, and `--help` as the first word print usage and exit 0; `--version` prints `holefit 0.1.0` and exits 0. These informational forms ignore later words. Only a valid invocation of one of the four operational commands reads standard input.

## Script language and errors

A script consists of lines separated by LF. One CR immediately before an LF, or at the end of the text, is removed. Fields are separated by one or more spaces or tabs; leading and trailing spaces and tabs are ignored. Empty lines and lines whose first field begins with `#` are ignored, but still count when reporting line numbers. Comments are whole-line only; trailing text is not an inline comment.

The supported commands are:

- `alloc NAME SIZE` requests a block.
- `free NAME` releases an allocated block.
- `compact` packs allocated blocks toward address zero.

Names must contain 1–16 ASCII letters, digits, or underscores. Allocation sizes must contain 1–5 decimal digits and have a value of at least 1; leading zeroes are allowed. The size is validated as written, before any alignment rounding.

The complete script is parsed and validated before replay begins. Within each non-empty, non-comment line, validation checks the command first, then the number of fields, then names, then allocation size. Syntax errors therefore take precedence over runtime errors, even if an earlier command would fail during execution. A syntax error is reported as `holefit: line N: REASON`; reasons include `unknown command WORD`, `alloc needs NAME SIZE`, `free needs NAME`, `compact takes no arguments`, `bad name "NAME"`, and `bad size "SIZE"`. A script with no commands is valid.

During replay, allocating a name that is already in use or freeing a name that is not in use is a script runtime error, reported as `holefit: line N: NAME is already allocated` or `holefit: line N: NAME is not allocated`. Runtime errors exit 1. Allocation rejection is not a script error: it is recorded as a failed allocation and replay continues. All script errors are atomic with respect to standard output: the command emits no partial result.

## Memory and placement

Memory contains the configured number of cells, numbered from zero, and begins as one free hole. Each allocated block is a contiguous range owned by one name; adjacent free ranges form one maximal hole. With alignment `A`, each request is rounded up to the next multiple of `A` cells, and the block length is that rounded value. Blocks are placed at the beginning of the selected hole.

For a request that fits in total free memory, the selected policy chooses a suitable hole as follows:

- **First fit** chooses the fitting hole with the lowest start address.
- **Best fit** chooses the smallest fitting hole; ties go to the lowest start address.
- **Worst fit** chooses the largest hole; ties go to the lowest start address.
- **Next fit** keeps a rover, initially at address zero. It tries holes starting at or after the rover in ascending address order, then holes starting before the rover in ascending address order. A hole containing the rover but starting earlier belongs to the latter group. After a successful allocation, the rover moves to the end of the new block (possibly the memory size). Failed allocations and frees do not move it; compaction resets it to zero. Other policies maintain but do not use the rover.

A request larger than the entire memory fails with `too big`. If no individual hole fits, the reason is `fragmented` when total free space is sufficient, otherwise `no space`. A failed request leaves memory and the name unchanged. Freeing returns the block's cells to the available space. Compaction preserves block order by address, packs blocks from address zero, combines remaining free space into one hole, and reports the number of blocks whose start address changed.

## Reports

- **run** emits one line per script command: `alloc NAME SIZE -> ADDRESS`, `alloc NAME SIZE -> fail (REASON)`, `free NAME -> LENGTH`, or `compact -> moved K`. The displayed allocation size is the original request with leading zeroes removed. An empty script produces no output.
- **map** prints the final complete memory partition in address order, one `START+LENGTH NAME` line for each block and one `START+LENGTH free` line for each hole. An empty memory map is represented by its single free range.
- **stats** prints eight lines: `size` is the configured cell count; `used` is the number of allocated cells; `free` is the number of unallocated cells; `blocks` is the number of allocated blocks; `holes` is the number of maximal free ranges; `largest` is the length of the largest hole (0 when there is no hole); `fails` is the number of rejected allocation commands; and `frag` is the percentage of free cells outside the largest hole, rounded down as `(free - largest) * 100 / free`. Fragmentation is 0 when there are no free cells. Empty memory has `used 0`, `free size`, `blocks 0`, `holes 1`, `largest size`, `fails 0`, and `frag 0`; full memory has `used size`, `free 0`, `blocks` equal to the allocated block count, `holes 0`, `largest 0`, and `frag 0`.
- **compare** replays the script independently under first, best, worst, and next, in that order. Each line reports `POLICY fails=F holes=H largest=L`. If any policy encounters a runtime error, the first such policy in that order determines the error and nothing is written to standard output.

Successful commands write their report to standard output and nothing to standard error; script errors write one prefixed line to standard error and exit 1. Output is UTF-8 with LF line endings, yielding the same output bytes on Windows, macOS, and Linux.

## Examples

The following POSIX shell transcripts run in the repository directory after `mvn -B -q compile`:

```text
$ printf 'alloc A 10\nalloc B 20\nalloc C 5\nfree B\nalloc D 8\nalloc E 30\n' | java -cp target/classes io.github.gulliby.holefit.Main run --size 48
alloc A 10 -> 0
alloc B 20 -> 10
alloc C 5 -> 30
free B -> 20
alloc D 8 -> 10
alloc E 30 -> fail (no space)
$ printf 'alloc A 8\nalloc B 4\nalloc C 6\nalloc D 4\nfree A\nfree C\nalloc E 5\n' | java -cp target/classes io.github.gulliby.holefit.Main map --size 32 --policy best
0+8 free
8+4 B
12+5 E
17+1 free
18+4 D
22+10 free
$ printf 'alloc A 1\nalloc B 9\nalloc C 4\nfree A\ncompact\n' | java -cp target/classes io.github.gulliby.holefit.Main stats --size 32 --align 4
size 32
used 16
free 16
blocks 2
holes 1
largest 16
fails 0
frag 0
$ printf 'alloc A 8\nalloc B 4\nalloc C 6\nalloc D 4\nfree A\nfree C\nalloc E 5\nalloc F 5\n' | java -cp target/classes io.github.gulliby.holefit.Main compare --size 32; echo "exit $?"
first fails=0 holes=3 largest=10
best fails=0 holes=3 largest=10
worst fails=0 holes=3 largest=6
next fails=0 holes=2 largest=8
exit 0
```

## Development

The Maven project targets Java 21 and uses JUnit Jupiter for tests; the application itself has no third-party runtime dependencies. Run `mvn -B verify` to compile and execute the full test suite. The ten row-based test classes cover command-line behavior, parsing, allocation policies, alignment, compaction, and reports: CliTest (12), OptionsTest (12), ReadsTest (8), ScriptTest (18), FirstTest (8), BestWorstTest (9), NextTest (12), AlignTest (8), CompactTest (9), and ReportTest (12), for 108 rows total.

The fixed scaffold includes Api, Registry, Main, Rows, and the Maven configuration; preserve these shared interfaces and the project manifest when extending the program. Public `Cli.run(List<String>, String)` returns a result without accessing streams, and `Cli.readsStdin(List<String>)` determines whether the entry point should read input. Main alone reads standard input and writes standard output and standard error. Script.parse validates scripts into line-numbered steps; Heap owns allocation state, placement, metrics, and compaction. RunCommand, MapCommand, StatsCommand, and CompareCommand use the shared parser and heap execution rather than duplicating them.

Follow the existing Java conventions: four-space indentation, explicit imports, no unused imports or variables, final utility classes with private constructors, and a Javadoc line on each public class. See the [MIT License](LICENSE).

---

Built with [BAKED](https://github.com/Gulliby-development/baked), an open-source coding agent (bench round R90).

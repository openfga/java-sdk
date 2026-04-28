# TLA+ verification of `OAuth2Client.getAccessToken`

This directory model-checks the post-completion race in
`OAuth2Client.getAccessToken` / `acquireToken` and verifies that the
synchronized-with-snapshot-recheck fix closes it.

## Files

| File | Purpose |
|---|---|
| `OAuth2Client.tla` | PlusCal model (one spec, two variants behind the `BUGGY` flag) |
| `buggy.cfg` | TLC config with `BUGGY = TRUE` — exercises the original CAS-only logic |
| `fixed.cfg` | TLC config with `BUGGY = FALSE` — exercises the synchronized re-check fix |
| `tla2tools.jar` | The standalone TLA+ tools (translator + TLC model checker) — gitignored; download yourself |

## Setup

Grab `tla2tools.jar` once (the file is gitignored to keep the repo small):

```bash
cd tla
curl -sSL -o tla2tools.jar \
  https://github.com/tlaplus/tlaplus/releases/latest/download/tla2tools.jar
```

## Properties checked

- `AtMostOneExchange` (safety invariant): with a single expiry cycle, no more
  than one HTTP token exchange is ever started — this is exactly the contract
  the original `OAuth2Client` javadoc promises.

## How to run

```bash
cd tla
# (Re)translate PlusCal to TLA+ after editing the algorithm:
java -cp tla2tools.jar pcal.trans OAuth2Client.tla

# Buggy variant — TLC will report a counterexample:
java -cp tla2tools.jar tlc2.TLC -workers auto -config buggy.cfg OAuth2Client.tla

# Fixed variant — TLC explores all states and reports no violations:
java -cp tla2tools.jar tlc2.TLC -workers auto -config fixed.cfg OAuth2Client.tla
```

## Last verified results (3 threads)

| Variant | TLC verdict | Distinct states |
|---|---|---|
| `BUGGY = TRUE`  | ❌ `Invariant AtMostOneExchange is violated` (counterexample ending with `exchanges = 2` after `valid = TRUE`) | 152 |
| `BUGGY = FALSE` | ✅ `Model checking completed. No error has been found.` | 391 |

The buggy counterexample mirrors the post-completion race we identified by
hand: thread 1 completes the exchange, thread 2 acquires the (no-op-in-buggy)
gate after `valid` is already `TRUE`, sees `inFlight = NONE`, and starts a
second exchange. The fix's snapshot re-check inside the `synchronized` block
makes this state unreachable.

## Caveats

- TLC checks **finite, small** instances (3 threads, 1 expiry). Concurrency
  bugs of this shape are dominated by ordering, not by counts; small-instance
  exhaustive checking is essentially as informative as a hand proof for this
  class of problem.
- The model abstracts the HTTP exchange to an atomic transition that publishes
  `valid := TRUE` and clears `inFlight`. This faithfully captures the only
  shared-state effects the race depends on.
- Java Memory Model details are abstracted as sequential consistency. This is
  sound here because the production code uses `synchronized` + `volatile` /
  `AtomicReference`, which give SC on the variables in question.



# Notice

**Rapidyne** is an unofficial, unaffiliated NeoForge 26.1.2 port of
[GPUBooster](https://github.com/ITsMrToad/GPUBooster) by **Mr.Toad**, originally
released for Fabric on Minecraft 1.21.1 under the GNU General Public License v3.0.
The name was changed from "GPUBooster" specifically to avoid implying this is an
official release or endorsed by the original author; the mod icon also carries a
visible "unofficial" overlay for the same reason.

This unofficial port is authored by **qiyuanqing**. All credit for the original
mod's design and implementation belongs to Mr.Toad.

This port is licensed under the same license, GPL-3.0-only (see `LICENSE`).

## What changed from the original

Modified starting 2026-08-12; renamed SwiftMath → Rapidyne 2026-08-12 (the
initial name "SwiftMath" was found to collide with an existing open-source
Swift math-rendering library and was changed to avoid confusion).

Minecraft's renderer was substantially rewritten between 1.21.1 and 26.1.2 (raw
OpenGL calls in `Framebuffer`, `VertexBuffer`, `BufferBuilder`, and
`ShaderProgram` were replaced by a backend-agnostic `GpuDevice`/`CommandEncoder`
abstraction). That alone ruled out the original mod's headline feature — the
DSA (direct-state-access) VBO/EBO/FBO system, RBO depth buffers, and bindless
textures, plus the `GivensPair` fast-normalize mixin (class removed entirely)
and the `ShaderProgram`/`BufferRenderer` uniform-matrix mixin (classes
removed). `MathHelper.floorMod`/`ceilLog2` were also skipped — the current
`Mth` equivalents already use a comparable fast implementation.

That left two loader-agnostic pieces to actually port: fast matrix math and a
fast Gaussian random generator. Both turned out to need a lot more than a
straight port.

## Fast-math matrix substitution: broken, removed, then correctly rebuilt

**First pass (2026-08-12):** the original `Matrix3f`/`Matrix4f` implementation
(applied to `PoseStack.Pose`) was ported faithfully. A user report of bed
block models rendering with missing or floating geometry led to writing a
verification test comparing every overridden method's output against real
JOML output on a non-trivial starting matrix. It found two bugs in `invert()`
(wrong element read, and a whole row written to the wrong destination), and
after fixing those, **9 of 13 checked operations still diverged from real
JOML**: `rotateX`, `rotateY`, `rotateZ`, `rotate(angle, axis)`, `rotateXYZ`,
and `mul` on both matrix types, plus a residual bug in `Matrix4f.invert()`.
Given the scale of the divergence, the whole feature was removed rather than
patched piecemeal — a wrong "fast" result is strictly worse than no
optimization.

**Second pass (2026-08-12, same day):** rather than re-deriving hand-unrolled
matrix arithmetic again (the same approach that produced the bugs above), the
rotation methods were rewritten to only replace the *trigonometry* — using
Minecraft's own lookup-table `Mth.sin`/`Mth.cos` instead of `Math.sin`/`Math.cos`
to build a quaternion — and then delegate the actual matrix composition to
JOML's own real, already-correct `rotate(Quaternionfc)`. `mul`, `invert`, and
`transpose` are no longer overridden at all; there's no trig to swap out in
them, and the original versions of those were exactly the ones proven wrong.

This version was verified element-for-element against real JOML output (9/9
checks passed) and benchmarked against it directly:

| Operation | Real JOML | Rapidyne | Result |
|---|---|---|---|
| `rotateXYZ` | 53.4 ns/op | 43.1 ns/op | ~24% faster |
| `rotateX` (single axis) | 13.7 ns/op | 14.2 ns/op | ~4% slower |

The single-axis regression was traced to allocation: each call built a `new
Quaternionf(...)`. Since `Matrix4f.rotate(Quaternionfc)` only reads the
quaternion and never retains a reference to it, this was replaced with a
single reused instance field per `GBFMatrix4f`/`GBFMatrix3f`, set fresh before
each call instead of allocated. Re-verified (10/10 checks, including a new
"repeated calls on the same instance" case specifically targeting aliasing
bugs from the reused field) and re-benchmarked:

| Operation | Real JOML | Rapidyne (alloc-free) | Result |
|---|---|---|---|
| `rotateXYZ` | 46.2 ns/op | 33.2 ns/op | ~39% faster |
| `rotateX` (single axis) | 14.3 ns/op | 10.1 ns/op | ~41% faster |

Both operations are now clear wins. That said, benchmark numbers on this test
environment show real run-to-run variance (a later run showed `rotateXYZ` at
0.95x and `rotateX` at 1.2x on the same code) — JIT warmup, GC timing, and
scheduling noise all move the numbers around, and a different machine's JIT
and GPU driver will move them further. Rather than pick one machine's numbers
and ship a single on/off switch, single-axis and combined-XYZ rotation are
**independently toggleable** in the config screen (`fastRotateSingleAxis`,
`fastRotateCombined`), both defaulting to enabled, so a user whose machine
doesn't benefit from one can turn just that one off without losing the other.

## Fast random: broken beyond justifying a fix, removed

The table-based Gaussian generator (`TableGaussianGenerator`, replacing
vanilla's `MarsagliaPolarGaussian` in `LegacyRandomSource`,
`XoroshiroRandomSource`, and `SingleThreadedRandomSource`) was ported, then
checked with the same rigor once the fast-math bugs raised doubt about the
rest of the ported code. Two bugs turned up in its `GBFMath` support code:

- `fastExp`: the lookup table is built to cover x ∈ [-8, 0], but the runtime
  code's scale constant assumed it covered [-8, 8], so any positive input read
  past the end of the array (`ArrayIndexOutOfBoundsException`). Never
  triggered in practice, since every call site in `TableGaussianGenerator`
  only ever passes values ≤ 0 — but a real landmine.
- `fastLog`: the polynomial approximation's coefficient doesn't satisfy its
  own boundary condition (it should reach `log2(2) = 1` at the top of each
  octave; it only reaches ~0.65), producing up to **266x relative error**
  depending on input.

The downstream effect: sampling 500,000 values from `TableGaussianGenerator`
gave mean 1.28 and variance 1.39, versus vanilla's correct mean ≈0, variance
≈1 — a badly skewed distribution, not a performance/precision tradeoff.

Given that, and that `nextGaussian()` isn't a hot path in vanilla Minecraft to
begin with (a handful of AI/motion/worldgen call sites, not per-tick or
per-frame), a correct reimplementation was judged not worth the engineering
risk for a negligible real-world payoff. Removed entirely, along with
`GBFMath` and the three random-source mixins.

## Compatibility hardening

- `rapidyne.mixins.json` sets `defaultRequire: 0` instead of the default `1`:
  if a future conflict with another mod's mixin ever prevents one of
  Rapidyne's injectors from applying, it now fails silently (that one
  optimization just doesn't apply) instead of throwing a hard error that
  would crash mod loading for the entire modpack.
- A one-time toast (`WelcomeNotice`, tracked via a marker file in `config/`)
  points new players at the config screen on first world join.
- English and Simplified Chinese localization (`assets/rapidyne/lang/en_us.json`,
  `zh_cn.json`) for the config screen's option titles/tooltips and the toast,
  added since a meaningful share of this mod's users are Chinese-speaking.

This mod also does not depend on the original's `toadlib` utility library
(no NeoForge build of it exists past Minecraft 1.20.1); a small standalone
`ModConfigSpec`-based config replaces it.

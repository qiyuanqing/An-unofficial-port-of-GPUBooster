# Rapidyne — an unofficial GPUBooster port for NeoForge 26.1.2

> **This is an unofficial, community port.** It is not created, endorsed, or
> maintained by GPUBooster's original author, and is not affiliated with the
> original project in any way. The name was deliberately changed from
> "GPUBooster" to avoid any confusion with an official release, and the icon
> below carries a visible "unofficial" marker for the same reason.

<p align="center"><img src="src/main/resources/icon.png" alt="Rapidyne icon: the original GPUBooster logo with a diagonal red UNOFFICIAL ribbon overlaid" width="388"></p>

Rapidyne is an unofficial port of [GPUBooster](https://github.com/ITsMrToad/GPUBooster)
by **Mr.Toad** — originally a Fabric mod for Minecraft 1.21.1 — to **NeoForge on
Minecraft 26.1.2**. The original project has not been updated to a NeoForge build
or to a current Minecraft version, so this port fills that gap for the parts of
it that could reasonably survive the jump.

- **Unofficial port author:** qiyuanqing
- **Original mod design and implementation:** Mr.Toad

All credit for the original design and implementation goes to Mr.Toad. Any bugs
introduced in this port are the porter's, not the original author's.

## What this port includes

Minecraft's renderer was substantially rewritten between 1.21.1 and 26.1.2:
raw OpenGL calls that used to live directly in `Framebuffer`, `VertexBuffer`,
`BufferBuilder`, and `ShaderProgram` were replaced by a backend-agnostic
`GpuDevice`/`CommandEncoder` abstraction (built to support both OpenGL and
Vulkan). That removed the hook points the original mod's headline feature
depended on. What's left after that, and after a full correctness rewrite
(see below), is:

- **Fast rotation math** — `PoseStack.Pose`'s `pose`/`normal` matrices
  (`Matrix4f`/`Matrix3f`) get a `rotateX`/`rotateY`/`rotateZ`/`rotate`/`rotateXYZ`
  override that uses Minecraft's own lookup-table `Mth.sin`/`Mth.cos` instead
  of `Math.sin`/`Math.cos`, reuses a single scratch `Quaternionf` instead of
  allocating one per call, then hands off to JOML's own real, verified-correct
  `rotate(Quaternionfc)` for the actual matrix composition — rather than
  hand-deriving matrix arithmetic, which is what went wrong the first time
  (see below). Verified element-for-element against real JOML output on every
  run. Benchmarked ~20–40% faster on the reference machine, though the exact
  number varies noticeably run-to-run (JIT/GC/scheduling noise) — which is
  exactly why it's split into **two independently toggleable config options**:
  single-axis rotation (`rotateX`/`Y`/`Z`, arbitrary-axis `rotate`) and
  combined XYZ rotation (`rotateXYZ`). Both default to **on**; if one doesn't
  help (or hurts) on your machine, turn just that one off.
- **A one-time in-game reminder** — the first time you join a world, a toast
  points you to the config screen.
- **English and Chinese localization** for the config screen and the toast
  (`en_us.json`, `zh_cn.json`).

Toggleable in-game via NeoForge's built-in config screen
(Mods → Rapidyne (Unofficial GPUBooster Port) → Config).

## What this port does *not* include

These either have no equivalent hook in 26.1.2's renderer, their target class
was removed outright, or a rigorous correctness check found the original
implementation to be unfixably wrong:

- The **DSA (direct-state-access) VBO/EBO/FBO system**, RBO depth buffers, and
  bindless textures — the original mod's main feature. It hooked
  `Framebuffer`/`VertexBuffer`/`BufferBuilder` methods that no longer exist in
  this form under the `GpuDevice` rewrite.
- The **`GivensPair`** fast-normalize/from-angle mixin — the `GivensPair`
  class no longer exists anywhere in Minecraft's source.
- The **`ShaderProgram`/`BufferRenderer`** uniform-matrix mixin — both classes
  were removed as part of the renderer rewrite.
- **`MathHelper.floorMod`/`ceilLog2`** — the current `Mth` equivalents already
  use the same or a comparable fast implementation, so porting this mixin
  would not have provided a measurable benefit.
- **`mul`/`invert`/`transpose`** on the pose/normal matrices are no longer
  overridden at all (left as plain JOML) — the original hand-rolled versions
  of these were found to be wrong (see below), and there's no fast-trig angle
  to exploit in them the way there is for rotation.
- **The fast Gaussian random generator** (`TableGaussianGenerator`, applied to
  `LegacyRandomSource`/`XoroshiroRandomSource`/`SingleThreadedRandomSource`) —
  removed entirely. A statistical test found it produced a badly skewed
  distribution (mean 1.28 instead of 0, variance 1.39 instead of 1) due to a
  broken `fastLog` approximation in its dependency, `GBFMath`. Beyond being
  wrong, `nextGaussian()` isn't a hot path in vanilla Minecraft, so even a
  correct reimplementation wouldn't provide a measurable performance benefit —
  not worth the correctness risk for negligible payoff.

See [`NOTICE.md`](NOTICE.md) for the full technical rationale behind each of
these decisions, including the two rounds of verification testing that found
the original mod's math to be unreliable and led to the current design.

This mod also does not depend on the original's `toadlib` utility library —
no NeoForge build of it exists past Minecraft 1.20.1 — so config, logging, and
the couple of math helpers it provided were reimplemented standalone here.

## Building

Requires JDK 25 (Minecraft 26.1.2 ships Java 25 to end users).

```
./gradlew build
```

The built jar will be at `build/libs/rapidyne-<version>.jar`.

To launch a test client:

```
./gradlew runClient
```

## License

GPL-3.0-only, same as the original GPUBooster (see [`LICENSE`](LICENSE)).
Per GPL §5, this counts as a modified version of the original work — see
[`NOTICE.md`](NOTICE.md) for what was changed and when.

## Changelog

See [`CHANGELOG.md`](CHANGELOG.md). The same changelog also appears in-game,
in the mod info panel under Mods → Rapidyne (NeoForge's mod list screen has
no dedicated changelog field, so it's appended to the description shown
there — `src/main/templates/META-INF/neoforge.mods.toml`).

## Links

- Original mod: https://github.com/ITsMrToad/GPUBooster
- Original mod's issue tracker (for upstream bugs, not this port):
  https://github.com/ITsMrToad/GPUBooster/issues

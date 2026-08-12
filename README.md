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
depended on, so this port only carries over the pieces that don't rely on that
now-gone direct-GL surface:

- **Fast math** — a faster JOML `Matrix3f`/`Matrix4f` implementation, applied
  to `PoseStack.Pose` (26.1.2's equivalent of the old `MatrixStack.Entry`).
- **Fast random** — a table-based Gaussian generator, applied to
  `LegacyRandomSource`, `XoroshiroRandomSource`, and `SingleThreadedRandomSource`
  (26.1.2's equivalents of the old `CheckedRandom`/`LocalRandom`/
  `Xoroshiro128PlusPlusRandom`), replacing vanilla's `MarsagliaPolarGaussian`.

Both are toggleable in-game via NeoForge's built-in config screen
(Mods → Rapidyne (Unofficial GPUBooster Port) → Config).

## What this port does *not* include

These either have no equivalent hook in 26.1.2's renderer, or their target
class was removed outright:

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

See [`NOTICE.md`](NOTICE.md) for the full technical rationale behind each of
these decisions.

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

## Links

- Original mod: https://github.com/ITsMrToad/GPUBooster
- Original mod's issue tracker (for upstream bugs, not this port):
  https://github.com/ITsMrToad/GPUBooster/issues

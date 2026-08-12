# Notice

This project is an unofficial, unaffiliated NeoForge 26.1.2 port of
[GPUBooster](https://github.com/ITsMrToad/GPUBooster) by **Mr.Toad**, originally
released for Fabric on Minecraft 1.21.1 under the GNU General Public License v3.0.

This port is licensed under the same license, GPL-3.0-only (see `LICENSE`).

## What changed from the original

Modified starting 2026-08-12. Minecraft's renderer was substantially rewritten
between 1.21.1 and 26.1.2 (raw OpenGL calls in `Framebuffer`, `VertexBuffer`,
`BufferBuilder`, and `ShaderProgram` were replaced by a backend-agnostic
`GpuDevice`/`CommandEncoder` abstraction). As a result, this port only carries
over the parts of GPUBooster that don't depend on that removed direct-GL surface:

- **Fast math**: a faster JOML `Matrix3f`/`Matrix4f` implementation, applied to
  `PoseStack.Pose` (the 26.1.2 equivalent of the old `MatrixStack.Entry`).
- **Fast random**: a table-based Gaussian generator, applied to
  `LegacyRandomSource`, `XoroshiroRandomSource`, and `SingleThreadedRandomSource`
  (26.1.2's equivalents of the old `CheckedRandom`/`LocalRandom`/
  `Xoroshiro128PlusPlusRandom`), replacing vanilla's `MarsagliaPolarGaussian`.

**Not ported** (no equivalent hook exists in 26.1.2's renderer, or the original
target class was removed entirely):

- The DSA (direct-state-access) VBO/EBO/FBO system, RBO depth buffers, and
  bindless textures — these hooked `Framebuffer`/`VertexBuffer`/`BufferBuilder`
  methods that no longer exist in this form.
- The `GivensPair` fast-normalize/from-angle mixin — the `GivensPair` class no
  longer exists in Minecraft's source.
- The `ShaderProgram`/`BufferRenderer` uniform-matrix mixin — both classes were
  removed as part of the renderer rewrite.
- `MathHelper.floorMod`/`ceilLog2` — the current `Mth` equivalents already use
  the same or a comparable fast implementation, so porting this mixin would not
  have provided a measurable benefit.

This mod also does not depend on the original's `toadlib` utility library
(no NeoForge build of it exists past Minecraft 1.20.1); a small standalone
`ModConfigSpec`-based config replaces it.

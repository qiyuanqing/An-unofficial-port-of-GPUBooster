# Changelog

This mirrors the changelog shown in-game (Mods → Rapidyne → mod info panel).
Every version bump gets an entry here and in `src/main/templates/META-INF/neoforge.mods.toml`.

## 0.3.0

- Split the single `fastMath` toggle into two independent config options:
  `fastRotateSingleAxis` and `fastRotateCombined`. Both default to enabled.
- Removed a per-call `Quaternionf` allocation in the rotation methods (now a
  reused instance field), turning a small single-axis regression into a
  measured ~41% speedup and improving the combined-rotation speedup to ~39%.
- Added English and Simplified Chinese localization (`en_us.json`, `zh_cn.json`)
  for the config screen's titles/tooltips and the welcome toast.

## 0.2.1

- Renamed **SwiftMath** to **Rapidyne** (the previous name collided with an
  existing open-source Swift math-rendering library) and added author credit
  (qiyuanqing, unofficial port; Mr.Toad, original mod).
- Fixed a bed-rendering bug (missing/floating geometry) by removing the
  originally-ported fast-math matrix code. A verification test against real
  JOML found it produced wrong output on 9 of 13 checked operations.
- Rewrote fast-math from scratch: instead of hand-derived matrix arithmetic,
  only the trigonometry is replaced (`Mth.sin`/`Mth.cos`), delegating actual
  matrix composition to JOML's own verified-correct `rotate(Quaternionfc)`.
  Verified correct and benchmarked faster than vanilla JOML.
- Removed the fast Gaussian random generator after a statistical test found
  it produced a badly skewed distribution (mean 1.28, variance 1.39, instead
  of the correct ~0/~1) due to a broken `fastLog` approximation.
- Compatibility: mixins now fail gracefully (`defaultRequire: 0`) instead of
  crashing the whole game if a future conflict with another mod occurs.
- Added a one-time toast on first world join pointing to the config screen.

## 0.2.0

- Renamed from "GPUBooster Port (Unofficial)" to **SwiftMath**.
- Added the mod icon: the original GPUBooster logo with a vibrant "UNOFFICIAL"
  ribbon overlay.

## 0.1.0

- Initial unofficial NeoForge 26.1.2 port of [GPUBooster](https://github.com/ITsMrToad/GPUBooster).

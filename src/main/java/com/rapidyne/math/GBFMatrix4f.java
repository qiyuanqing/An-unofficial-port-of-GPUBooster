package com.rapidyne.math;

import com.rapidyne.RapidyneConfig;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

// Only the trigonometry is replaced (Mth's lookup-table sin/cos instead of Math.sin/cos).
// The actual matrix composition is left to JOML's own rotate(Quaternionfc), which is
// already correct and already optimized - reimplementing it by hand is exactly what
// produced the bugs in the original ported code.
//
// The scratch quaternion is a reused instance field (not a fresh allocation per call):
// super.rotate(Quaternionfc) only reads it to compute the matrix, never retains a
// reference to it, so overwriting its components before each call is safe and avoids
// one allocation per rotation on what is otherwise a per-frame hot path.
//
// Single-axis and combined-XYZ rotation are independently toggleable in config (their
// measured performance profiles can differ by machine/JIT), so rotateXYZ's fast path
// calls the private *Fast helpers directly rather than the public rotateX/Y/Z, which
// would apply the single-axis toggle to what should be an independent setting.
public class GBFMatrix4f extends Matrix4f {

    private final Quaternionf scratch = new Quaternionf();

    public GBFMatrix4f() {
        super();
    }

    public GBFMatrix4f(Matrix4f src) {
        super(src);
    }

    @Override
    public GBFMatrix4f rotateX(float ang) {
        if (!RapidyneConfig.fastRotateSingleAxisEnabled()) {
            super.rotateX(ang);
            return this;
        }
        return rotateXFast(ang);
    }

    @Override
    public GBFMatrix4f rotateY(float ang) {
        if (!RapidyneConfig.fastRotateSingleAxisEnabled()) {
            super.rotateY(ang);
            return this;
        }
        return rotateYFast(ang);
    }

    @Override
    public GBFMatrix4f rotateZ(float ang) {
        if (!RapidyneConfig.fastRotateSingleAxisEnabled()) {
            super.rotateZ(ang);
            return this;
        }
        return rotateZFast(ang);
    }

    @Override
    public GBFMatrix4f rotate(float ang, float x, float y, float z) {
        if (!RapidyneConfig.fastRotateSingleAxisEnabled()) {
            super.rotate(ang, x, y, z);
            return this;
        }
        float invLen = Mth.invSqrt(x * x + y * y + z * z);
        x *= invLen;
        y *= invLen;
        z *= invLen;
        float half = ang * 0.5f;
        float s = Mth.sin(half);
        super.rotate(this.scratch.set(x * s, y * s, z * s, Mth.cos(half)));
        return this;
    }

    @Override
    public GBFMatrix4f rotateXYZ(float angleX, float angleY, float angleZ) {
        if (!RapidyneConfig.fastRotateCombinedEnabled()) {
            super.rotateXYZ(angleX, angleY, angleZ);
            return this;
        }
        rotateXFast(angleX);
        rotateYFast(angleY);
        rotateZFast(angleZ);
        return this;
    }

    private GBFMatrix4f rotateXFast(float ang) {
        float half = ang * 0.5f;
        super.rotate(this.scratch.set(Mth.sin(half), 0f, 0f, Mth.cos(half)));
        return this;
    }

    private GBFMatrix4f rotateYFast(float ang) {
        float half = ang * 0.5f;
        super.rotate(this.scratch.set(0f, Mth.sin(half), 0f, Mth.cos(half)));
        return this;
    }

    private GBFMatrix4f rotateZFast(float ang) {
        float half = ang * 0.5f;
        super.rotate(this.scratch.set(0f, 0f, Mth.sin(half), Mth.cos(half)));
        return this;
    }
}

package com.rapidyne.math;

import com.rapidyne.RapidyneConfig;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Quaternionf;

// Same approach as GBFMatrix4f: only the trigonometry is replaced (Mth's lookup-table
// sin/cos), matrix composition is left to JOML's own verified-correct rotate(Quaternionfc),
// and single-axis vs combined-XYZ rotation are independently toggleable in config.
public class GBFMatrix3f extends Matrix3f {

    private final Quaternionf scratch = new Quaternionf();

    public GBFMatrix3f() {
        super();
    }

    public GBFMatrix3f(Matrix3f src) {
        super(src);
    }

    @Override
    public GBFMatrix3f rotateX(float ang) {
        if (!RapidyneConfig.fastRotateSingleAxisEnabled()) {
            super.rotateX(ang);
            return this;
        }
        return rotateXFast(ang);
    }

    @Override
    public GBFMatrix3f rotateY(float ang) {
        if (!RapidyneConfig.fastRotateSingleAxisEnabled()) {
            super.rotateY(ang);
            return this;
        }
        return rotateYFast(ang);
    }

    @Override
    public GBFMatrix3f rotateZ(float ang) {
        if (!RapidyneConfig.fastRotateSingleAxisEnabled()) {
            super.rotateZ(ang);
            return this;
        }
        return rotateZFast(ang);
    }

    @Override
    public GBFMatrix3f rotateXYZ(float angleX, float angleY, float angleZ) {
        if (!RapidyneConfig.fastRotateCombinedEnabled()) {
            super.rotateXYZ(angleX, angleY, angleZ);
            return this;
        }
        rotateXFast(angleX);
        rotateYFast(angleY);
        rotateZFast(angleZ);
        return this;
    }

    private GBFMatrix3f rotateXFast(float ang) {
        float half = ang * 0.5f;
        super.rotate(this.scratch.set(Mth.sin(half), 0f, 0f, Mth.cos(half)));
        return this;
    }

    private GBFMatrix3f rotateYFast(float ang) {
        float half = ang * 0.5f;
        super.rotate(this.scratch.set(0f, Mth.sin(half), 0f, Mth.cos(half)));
        return this;
    }

    private GBFMatrix3f rotateZFast(float ang) {
        float half = ang * 0.5f;
        super.rotate(this.scratch.set(0f, 0f, Mth.sin(half), Mth.cos(half)));
        return this;
    }
}

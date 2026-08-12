package com.rapidyne.mixin;

import com.rapidyne.math.GBFMatrix3f;
import com.rapidyne.math.GBFMatrix4f;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Always installs the GBF variants; each individual optimization (single-axis vs
// combined-XYZ rotation) checks its own config toggle per call - see GBFMatrix4f/3f.
@Mixin(PoseStack.Pose.class)
public abstract class PoseMixin {

    @Redirect(method = "<init>", at = @At(value = "NEW", target = "org/joml/Matrix4f"))
    private Matrix4f rapidyne$fastPose() {
        return new GBFMatrix4f();
    }

    @Redirect(method = "<init>", at = @At(value = "NEW", target = "org/joml/Matrix3f"))
    private Matrix3f rapidyne$fastNormal() {
        return new GBFMatrix3f();
    }
}

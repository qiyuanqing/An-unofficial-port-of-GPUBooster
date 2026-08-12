package com.gpuboosterport.mixin;

import com.gpuboosterport.GPUBoosterConfig;
import com.gpuboosterport.math.GBFMatrix3f;
import com.gpuboosterport.math.GBFMatrix4f;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PoseStack.Pose.class)
public abstract class PoseMixin {

    @Redirect(method = "<init>", at = @At(value = "NEW", target = "org/joml/Matrix4f"))
    private Matrix4f gpuboosterport$fastPose() {
        return GPUBoosterConfig.fastMathEnabled() ? new GBFMatrix4f() : new Matrix4f();
    }

    @Redirect(method = "<init>", at = @At(value = "NEW", target = "org/joml/Matrix3f"))
    private Matrix3f gpuboosterport$fastNormal() {
        return GPUBoosterConfig.fastMathEnabled() ? new GBFMatrix3f() : new Matrix3f();
    }
}

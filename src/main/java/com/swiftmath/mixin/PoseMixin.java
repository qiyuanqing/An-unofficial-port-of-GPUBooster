package com.swiftmath.mixin;

import com.swiftmath.SwiftMathConfig;
import com.swiftmath.math.GBFMatrix3f;
import com.swiftmath.math.GBFMatrix4f;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PoseStack.Pose.class)
public abstract class PoseMixin {

    @Redirect(method = "<init>", at = @At(value = "NEW", target = "org/joml/Matrix4f"))
    private Matrix4f swiftmath$fastPose() {
        return SwiftMathConfig.fastMathEnabled() ? new GBFMatrix4f() : new Matrix4f();
    }

    @Redirect(method = "<init>", at = @At(value = "NEW", target = "org/joml/Matrix3f"))
    private Matrix3f swiftmath$fastNormal() {
        return SwiftMathConfig.fastMathEnabled() ? new GBFMatrix3f() : new Matrix3f();
    }
}

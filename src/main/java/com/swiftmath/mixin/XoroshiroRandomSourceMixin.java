package com.swiftmath.mixin;

import com.swiftmath.SwiftMathConfig;
import com.swiftmath.math.TableGaussianGenerator;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.MarsagliaPolarGaussian;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(XoroshiroRandomSource.class)
public abstract class XoroshiroRandomSourceMixin {

    @Redirect(
        method = {"<init>(J)V", "<init>(JJ)V"},
        at = @At(value = "NEW", target = "net/minecraft/world/level/levelgen/MarsagliaPolarGaussian")
    )
    private MarsagliaPolarGaussian swiftmath$fastGaussian(RandomSource randomSource) {
        return SwiftMathConfig.fastRandomEnabled() ? new TableGaussianGenerator(randomSource) : new MarsagliaPolarGaussian(randomSource);
    }
}

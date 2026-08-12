package com.swiftmath.mixin;

import com.swiftmath.SwiftMathConfig;
import com.swiftmath.math.TableGaussianGenerator;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.MarsagliaPolarGaussian;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LegacyRandomSource.class)
public abstract class LegacyRandomSourceMixin {

    @Redirect(method = "<init>(J)V", at = @At(value = "NEW", target = "net/minecraft/world/level/levelgen/MarsagliaPolarGaussian"))
    private MarsagliaPolarGaussian swiftmath$fastGaussian(RandomSource randomSource) {
        return SwiftMathConfig.fastRandomEnabled() ? new TableGaussianGenerator(randomSource) : new MarsagliaPolarGaussian(randomSource);
    }
}

package com.gpuboosterport.mixin;

import com.gpuboosterport.GPUBoosterConfig;
import com.gpuboosterport.math.TableGaussianGenerator;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.MarsagliaPolarGaussian;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LegacyRandomSource.class)
public abstract class LegacyRandomSourceMixin {

    @Redirect(method = "<init>(J)V", at = @At(value = "NEW", target = "net/minecraft/world/level/levelgen/MarsagliaPolarGaussian"))
    private MarsagliaPolarGaussian gpuboosterport$fastGaussian(RandomSource randomSource) {
        return GPUBoosterConfig.fastRandomEnabled() ? new TableGaussianGenerator(randomSource) : new MarsagliaPolarGaussian(randomSource);
    }
}

package com.rapidyne.mixin;

import com.rapidyne.RapidyneConfig;
import com.rapidyne.math.TableGaussianGenerator;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.MarsagliaPolarGaussian;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SingleThreadedRandomSource.class)
public abstract class SingleThreadedRandomSourceMixin {

    @Redirect(method = "nextGaussian", at = @At(value = "NEW", target = "net/minecraft/world/level/levelgen/MarsagliaPolarGaussian"))
    private MarsagliaPolarGaussian rapidyne$fastGaussian(RandomSource randomSource) {
        return RapidyneConfig.fastRandomEnabled() ? new TableGaussianGenerator(randomSource) : new MarsagliaPolarGaussian(randomSource);
    }
}

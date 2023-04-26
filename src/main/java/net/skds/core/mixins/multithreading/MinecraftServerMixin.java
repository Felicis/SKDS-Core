package net.skds.core.mixins.multithreading;

import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.skds.core.multithreading.MTHooks;

@Mixin(value = { MinecraftServer.class })
public class MinecraftServerMixin {

    @Inject(method = "tickChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", ordinal = 1))
    private void tickHook(CallbackInfo ci) {
        ProfilerFiller asdf;
        MTHooks.afterWorldsTick();
    }
}
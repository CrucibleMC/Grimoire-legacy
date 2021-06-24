package io.github.crucible.grimoire.common.test.mixins.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.crucible.grimoire.common.GrimoireCore;

@Pseudo
@Mixin(targets = "net.minecraft.client.Minecraft")
public class MixinMinecraft {

    @Inject(method = "startGame", at = @At("HEAD"))
    public void onStartGame(CallbackInfo info) {
        new RuntimeException("Hello from stack!").fillInStackTrace().printStackTrace();
    }

}

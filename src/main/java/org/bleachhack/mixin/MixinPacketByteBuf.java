package org.bleachhack.mixin;

import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.network.PacketByteBuf;
import org.bleachhack.module.ModuleManager;
import org.bleachhack.module.mods.AntiChunkBan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PacketByteBuf.class)
public class MixinPacketByteBuf {
    @ModifyArg(method = "readNbt()Lnet/minecraft/nbt/NbtCompound;", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;readNbt(Lnet/minecraft/nbt/NbtSizeTracker;)Lnet/minecraft/nbt/NbtElement;"))
    private NbtSizeTracker increaseLimit(NbtSizeTracker in) {
        return ModuleManager.getModule(AntiChunkBan.class).isEnabled() ? NbtSizeTracker.ofUnlimitedBytes() : in;
    }
}

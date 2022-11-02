package me.wolfie.doggoclient.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketCallbacks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// "inspired" by https://www.youtube.com/watch?v=V4_5x4QtHVg
@Mixin(ClientConnection.class)
public interface GoofyAhhPackets {

    @Invoker("sendImmediately")
    public void sendIm(Packet<?> packet, PacketCallbacks callbacks);
}

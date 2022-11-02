package me.wolfie.doggoclient.util;

import me.wolfie.doggoclient.client.DoggoclientClient;
import me.wolfie.doggoclient.mixin.GoofyAhhPackets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class Packetutil {

    static Vec3d lowerPos(Vec3d pos){
        pos = new Vec3d(pos.x, pos.y -0.04333, pos.z);
        return pos;
    }

    public static void sendPos(Vec3d pos){
        MinecraftClient client = DoggoclientClient.self;
        GoofyAhhPackets quandale = (GoofyAhhPackets) client.player.networkHandler.getConnection();
        pos = Packetutil.lowerPos(pos);
        Packet packet = new PlayerMoveC2SPacket.PositionAndOnGround(pos.x,pos.y,pos.z,false);
        quandale.sendIm(packet,null); // no callback, if we did we would have " -> { CONTAINED LAMBDA CODE } " to test it
    }
}

package me.wolfie.doggoclient.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@Environment(EnvType.CLIENT)
public class DoggoclientClient implements ClientModInitializer {

    private static KeyBinding Fly;
    private static KeyBinding SlowDown;
    private static KeyBinding SpeedUP;
    private static KeyBinding Tracker;
    @Override
    public void onInitializeClient() {
        Fly = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.doggoclient.fly", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_APOSTROPHE, // The keycode of the key
                "category.doggoclient.binds"
                // The translation key of the keybinding's category.

        ));
        Tracker = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.doggoclient.track",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_SEMICOLON,
                "category.doggoclient.binds"

        ));
        //if you have anything that needs to happen repeatedly, put stuff here.
        //one-time code shouldn't be here, just call MinecraftClient



            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if(MinecraftClient.getInstance().world != null) {
                if(client.player.getHealth() <= 0){
                    client.player.sendMessage(Text.of("You have died at: " + client.player.getBlockPos().toShortString()));
                }

                while (Fly.wasPressed()){

                    client.player.getAbilities().setFlySpeed(0.05f);
                    client.player.getAbilities().flying = !client.player.getAbilities().flying;
                }

                while (Tracker.wasPressed()){
                    for (int i = 0; i < client.player.world.getPlayers().size(); i++){
                        List<? extends PlayerEntity> players = client.player.world.getPlayers();
                        //don't use overlay, it makes it hard to find what's what
                        client.player.sendMessage(Text.of("located player at coords: " + players.get(i).getBlockPos().toShortString() + ", name: " + players.get(i).getName().getString()),false);
                    }
                }
            }
            });

    }
}

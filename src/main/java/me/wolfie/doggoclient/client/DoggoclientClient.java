package me.wolfie.doggoclient.client;

import me.wolfie.doggoclient.util.Packetutil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraft.block.CropBlock.AGE;

@Environment(EnvType.CLIENT)
public class DoggoclientClient implements ClientModInitializer {

    //grab instance
    public static MinecraftClient self;
    private static KeyBinding Tractor;
    private static KeyBinding Fly;
    private static KeyBinding SlowDown;
    private static KeyBinding SpeedUP;
    private static KeyBinding Tracker;
    private static KeyBinding plow;
    // DONT USE MinecraftClient!!! 😳😳😳
    @Override
    public void onInitializeClient() {

        AtomicBoolean tractor = new AtomicBoolean(false);

        HudRenderCallback.EVENT.register(((matrixStack, tickDelta) -> {
            TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
            if(tractor.get()){
                renderer.draw(matrixStack,Text.of("Tractor mode enabled..."),15,15,0x31A4DE);
            }
        }));

        SlowDown = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.doggoclient.spdup", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_UP, // The keycode of the key
                "category.doggoclient.binds"
                // The translation key of the keybinding's category.

        ));
        SpeedUP = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.doggoclient.spddown", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_DOWN, // The keycode of the key
                "category.doggoclient.binds"
                // The translation key of the keybinding's category.

        ));


        self = MinecraftClient.getInstance();


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
        Tractor = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.doggoclient.tractor",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_GRAVE_ACCENT,
                "category.doggoclient.binds"

        ));
        plow = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.doggoclient.plow",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_MINUS,
                "category.doggoclient.binds"

        ));
        //if you have anything that needs to happen repeatedly, put stuff here.
        //one-time code shouldn't be here, just call MinecraftClient



        final AtomicReference<Vec3d> refrence = new AtomicReference<>();
        final AtomicBoolean harvest = new AtomicBoolean(false);
        AtomicInteger ticks = new AtomicInteger(0);
        AtomicBoolean delay = new AtomicBoolean(false);

            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if(client.player != null){

                    while (plow.wasPressed()){
                        Killaura(client);
                    }

                    while (Tractor.wasPressed()){
                        tractor.set(!tractor.get());
                    }

                    if(tractor.get()) {
                        for (int x = -4; x <= 4; x++) {
                            for (int y = -4; y <= 4; y++) {
                                for (int z = -4; z <= 4; z++) {

                                        plant(client, client.player.getBlockPos().add(x, y, z));

                                    Harvest(client, client.player.getBlockPos().add(x, y, z));


                                }
                            }
                        }
                    }

                    // less to prevent packet issues
                    if(tractor.get()) {
                        for (int x = -2; x <= 2; x++) {
                            for (int y = -1; y <= 1; y++) {
                                for (int z = -2; z <= 2; z++) {

                                    if(!delay.get()) {
                                        plowBlocks(client, client.player.getBlockPos().add(x, y, z));
                                    }
                                    delay.set(!delay.get());


                                }
                            }
                        }
                    }


                    if(refrence.get() != null){
                        if(client.player.getPos().y >= refrence.get().y - 0.04333 && !client.player.isOnGround()){

                            ticks.set(ticks.get() + 1);
                        }else {
                            ticks.set(ticks.get() + 0);
                        }
                    }



                    refrence.set(client.player.getPos());

                    if(ticks.get() >= 20){
                    Packetutil.sendPos(client.player.getPos().subtract(0,0.04333,0));
                    ticks.set(0);
                    }


                        if(MinecraftClient.getInstance().world != null) {
                        if (client.player.getHealth() <= 0) {
                        client.player.sendMessage(Text.of("You have died at: " + client.player.getBlockPos().toShortString()));
                        client.player.getAbilities().flying = false; // prevents kicks if you die during flight
                        }

                        while (Fly.wasPressed()) {


                        client.player.getAbilities().flying = !client.player.getAbilities().flying;
                    }

                        while (SpeedUP.wasPressed()){
                            client.player.getAbilities().setFlySpeed(client.player.getAbilities().getFlySpeed() + 0.05f);
                            client.player.sendMessage(Text.of("Speed at: " + client.player.getAbilities().getFlySpeed()));
                        }
                            while (SlowDown.wasPressed()){
                                client.player.getAbilities().setFlySpeed(client.player.getAbilities().getFlySpeed() - 0.05f);
                                client.player.sendMessage(Text.of("Speed at: " + client.player.getAbilities().getFlySpeed()));
                            }


                    while (Tracker.wasPressed()) {
                        for (int i = 0; i < client.player.world.getPlayers().size(); i++) {
                            List<? extends PlayerEntity> players = client.player.world.getPlayers();
                            if (players.get(i).getName().getString() != client.getSession().getUsername()) {
                                client.player.sendMessage(Text.of("located player at coords: " + players.get(i).getBlockPos().toShortString() + ", name: " + players.get(i).getName().getString()), false);
                            }
                            //don't use overlay, it makes it hard to find what's what
                        }
                    }
                }
            }
            });


    }
    Item[] FarmSeeditems = {Items.WHEAT_SEEDS,Items.BEETROOT_SEEDS,Items.POTATO,Items.CARROT}; //Don't include melons or pumpkins, unnecessary
    public void plant(MinecraftClient client, BlockPos Position) {
        BlockState State = client.world.getBlockState(Position);
        if(State.getBlock() instanceof FarmlandBlock){
            BlockState UP = client.world.getBlockState(Position.up());
            if(UP.getBlock() instanceof AirBlock){
                Item item = client.player.getOffHandStack().getItem();
                if(Arrays.stream(FarmSeeditems).anyMatch(item::equals)){ //check if item is in array
                    Vec3d BPos = new Vec3d(Position.getX(),Position.getY(),Position.getZ());
                    BlockHitResult hitRes = new BlockHitResult(BPos, Direction.UP,Position,false);
                    client.interactionManager.interactBlock(client.player, Hand.OFF_HAND,hitRes); // plant the thing
                }
            }
        }
    }

    Block[] FarmBlocks = {Blocks.WHEAT,Blocks.BEETROOTS,Blocks.POTATOES,Blocks.CARROTS, Blocks.GRASS, Blocks.TALL_GRASS, Blocks.LARGE_FERN,Blocks.FERN,Blocks.DEAD_BUSH}; // blocks like melons & pumpkins shouldn't get this treatment due to mining being needed
    public void Harvest(MinecraftClient client, BlockPos Position){
        BlockState State = client.world.getBlockState(Position.up());

        if(Arrays.stream(FarmBlocks).anyMatch(State.getBlock()::equals)){

            if(State.getBlock() == Blocks.WHEAT && State.get(AGE) == 7){
                Destroy(Position,client);
            }else if(State.getBlock() == Blocks.BEETROOTS && State.get(AGE) == 3){
                Destroy(Position,client);
            }else if(State.getBlock() == Blocks.POTATOES && State.get(AGE) == 7 || State.getBlock() == Blocks.CARROTS && State.get(AGE) == 7){
                Destroy(Position,client);
            }else if(State.getBlock() == Blocks.GRASS || State.getBlock() == Blocks.TALL_GRASS || State.getBlock() == Blocks.FERN || State.getBlock() == Blocks.LARGE_FERN || State.getBlock() == Blocks.DEAD_BUSH){
                Destroy(Position,client);
            }
            // just "else" BREAKS EVERYTHING
        }
    }


    Item[] Tools = { Items.STONE_HOE,Items.DIAMOND_HOE,Items.IRON_HOE,Items.NETHERITE_HOE,Items.WOODEN_HOE,Items.GOLDEN_HOE };
    public void plowBlocks(MinecraftClient client, BlockPos Position){


        Item item = client.player.getMainHandStack().getItem();
        if(Arrays.stream(Tools).anyMatch(item::equals)){
            if (client.world.getBlockState(Position).getBlock().asItem() == Items.DIRT || client.world.getBlockState(Position).getBlock().asItem() == Items.GRASS_BLOCK) {



                    BlockHitResult hitRes = new BlockHitResult(new Vec3d(Position.getX(), Position.getY(), Position.getZ()), Direction.UP, Position, true);
                    client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, hitRes);


            }
        }
    }

    public void Destroy(BlockPos Position, MinecraftClient client){
        Vec3d BPos = new Vec3d(Position.getX()+1,Position.getY()+1,Position.getZ()+1);
        BlockHitResult hitRes = new BlockHitResult(BPos, Direction.UP,Position,false);
        client.interactionManager.attackBlock(Position.up(), Direction.UP); // DESTROY the thing
    }

    public void Killaura(MinecraftClient client){
        for (int i = 0; i < client.player.world.getPlayers().size(); i++) {
            List<? extends PlayerEntity> players = client.player.world.getPlayers();
            Entity e = players.get(i);


            client.interactionManager.attackEntity(client.player,e);
        }
    }
}

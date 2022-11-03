// 99% of all code is in client, 1% being almost all mixins
package me.wolfie.doggoclient;

import net.fabricmc.api.ModInitializer;

public class Doggoclient implements ModInitializer {
    @Override
    public void onInitialize() {
        System.out.println("Client Loaded");
    }
}

package com.fuzifuziii.classes;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(ClassesMod.MODID)
public class ClassesMod {
    public static final String MODID = "classes";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ClassesMod(IEventBus modBus) {
        // register data attachments
        ModAttachments.ATTACHMENTS.register(modBus);
        // register server-side event listeners
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // load classes from json
        ClassManager.load();
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ClassCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        // open the menu for players without a class
        if (event.getEntity() instanceof ServerPlayer player
                && !ClassManager.all().isEmpty()
                && ClassManager.getClassOf(player) == null) {
            ClassMenu.open(player);
        }
    }
}

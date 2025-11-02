package net.r4mble;

import net.fabricmc.api.ClientModInitializer;
import net.r4mble.event.PlayerEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class TabPlayerHighlighter implements ClientModInitializer {
    public static final String MOD_ID = "tab_player_highlighter";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Map<String, String> players_prefixes;

    @Override
    public void onInitializeClient() {
        ModConfig.HANDLER.load();
        PlayerEvents.registerEvents();
        LOGGER.info("Mod is initialized!");
    }
}
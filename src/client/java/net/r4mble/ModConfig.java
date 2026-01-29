package net.r4mble;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.*;
import java.util.HashMap;

public class ModConfig {
    public static ConfigClassHandler<ModConfig> HANDLER = ConfigClassHandler.createBuilder(ModConfig.class)
            .id(Identifier.of("tab_player_highlighter", "config"))
                    .serializer(config -> GsonConfigSerializerBuilder.create(config)
                            .setPath(FabricLoader.getInstance().getConfigDir().resolve("tab_player_highlighter.json"))
                            .build())
                    .build();

    @SerialEntry
    public boolean priorityMode = true;
    @SerialEntry
    public boolean onlineMod = true;
    @SerialEntry
    public String API_URL = "https://tph-api.energyproject.dev/api/prefixes";
    @SerialEntry
    public HashMap<String, String> playersPrefixes = new HashMap<>();

}

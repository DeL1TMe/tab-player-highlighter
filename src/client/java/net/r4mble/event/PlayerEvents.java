package net.r4mble.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.r4mble.TabPlayerHighlighter;
import net.r4mble.util.TabPlayerHighlighterAPI;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Environment(EnvType.CLIENT)
public class PlayerEvents {

    private static final AtomicReference<CompletableFuture<Void>> currentTask = new AtomicReference<>();

    public static void registerEvents() {
        onPlayerJoinedServer();
    }

    private static void onPlayerJoinedServer() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            synchronized (currentTask) {
                CompletableFuture<Void> prevTask = currentTask.get();

                if (prevTask != null) {
                    prevTask.cancel(true);
                    currentTask.set(null);
                }
            }

            var task = TabPlayerHighlighterAPI.getPlayersWithRolesAsync()
                    .thenAccept(prefixes -> {
                        MinecraftClient.getInstance().execute(() -> {
                            TabPlayerHighlighter.players_prefixes = prefixes;
                        });
                    })
                    .exceptionally(e -> {
                        e.printStackTrace();
                        return null;
                    })
                    .whenComplete((v, e) -> {
                        synchronized (currentTask) {
                            currentTask.set(null);
                        }
                    });

            synchronized (currentTask) {
                currentTask.set(task);
            }
        });
    }
}

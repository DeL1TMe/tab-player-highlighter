package net.r4mble.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.r4mble.ModConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TabPlayerHighlighterAPI {
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static Map<String, String> getPlayersWithRoles() {
        if (ModConfig.HANDLER.instance().onlineMod) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ModConfig.HANDLER.instance().API_URL))
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                JsonObject playersArray = JsonParser.parseString(response.body())
                        .getAsJsonObject()
                        .get("players")
                        .getAsJsonObject();
                Gson gson = new Gson();
                Map<String, String> players = gson.fromJson(playersArray, Map.class);
                return players;
            } catch (Exception ex) {
                return null;
            }
        } else {
            return ModConfig.HANDLER.instance().playersPrefixes;
        }
    }

    public static CompletableFuture<Map<String, String>> getPlayersWithRolesAsync() {
        if (!ModConfig.HANDLER.instance().onlineMod) {
            return CompletableFuture.completedFuture(ModConfig.HANDLER.instance().playersPrefixes);
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ModConfig.HANDLER.instance().API_URL))
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(body -> {
                        JsonObject playersJson = JsonParser.parseString(body)
                                .getAsJsonObject()
                                .getAsJsonObject("players");
                        Map<String, String> players = new Gson().fromJson(playersJson, Map.class);
                        return players;
                    });
        } catch (Exception ex) {
            return null;
        }
    }
}

package net.r4mble.mixin;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.r4mble.ModConfig;
import net.r4mble.TabPlayerHighlighter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tab.bettertab.Tools;

import java.util.*;


@Mixin(Tools.class)
public class BetterTabMixin {

    @Inject(method = "getPlayerEntries", at = @At("RETURN"), cancellable = true)
    private static void afterThem(MinecraftClient client, boolean ENABLE_MOD, boolean USE_EXAMPLES, int EXAMPLE_AMOUNT, String EXAMPLE_TEXT, Comparator<PlayerListEntry> ENTRY_ORDERING, CallbackInfoReturnable<List<PlayerListEntry>> cir) {
        List<PlayerListEntry> originalList = cir.getReturnValue();
        if (!ModConfig.HANDLER.instance().priorityMode || TabPlayerHighlighter.players_prefixes == null) {
            cir.setReturnValue(originalList);
            cir.cancel();
            return;
        }
        HashMap<String, PlayerListEntry> tabList = new HashMap<>();
        for (PlayerListEntry entry : originalList) {
            tabList.put(entry.getProfile().id().toString(), entry);
        }
        List<PlayerListEntry> modifiedList = new ArrayList<>();
        List<PlayerListEntry> priorityList = new ArrayList<>();
        for (Map.Entry<String, String> entry : TabPlayerHighlighter.players_prefixes.entrySet()) {
            if (tabList.containsKey(entry.getKey())) {
                priorityList.add(tabList.get(entry.getKey()));
            }
        }
        for (PlayerListEntry entry : originalList) {
            if (!TabPlayerHighlighter.players_prefixes.containsKey(entry.getProfile().id().toString())) {
                modifiedList.add(entry);
            }
        }
        priorityList.addAll(modifiedList);
        cir.setReturnValue(priorityList);
        cir.cancel();
    }

    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    private static void onGetPlayerName(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        Text displayName = entry.getDisplayName() != null
                ? entry.getDisplayName()
                : Text.literal(entry.getProfile().name());

        if (TabPlayerHighlighter.players_prefixes == null) {
            return;
        }
        String uuid = entry.getProfile().id().toString();

        String prefix = TabPlayerHighlighter.players_prefixes.get(uuid);
        if (prefix != null && !prefix.isEmpty()) {
            Text modifiedName = Text.empty()
                    .append(Text.literal(prefix))
                    .append(" ")
                    .append(displayName);

            cir.setReturnValue(modifiedName);
            cir.cancel();
        }
    }
}

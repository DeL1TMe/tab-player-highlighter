package net.r4mble.mixin;

import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.r4mble.ModConfig;
import net.r4mble.TabPlayerHighlighter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Mixin(PlayerListHud.class)
public abstract class VanillaPlayerListHudMixin {

    @Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
    private void onGetPlayerName(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
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

    @Inject(method = "collectPlayerEntries", at = @At("RETURN"), cancellable = true)
    private void sortPlayers(CallbackInfoReturnable<List<PlayerListEntry>> cir) {
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
}

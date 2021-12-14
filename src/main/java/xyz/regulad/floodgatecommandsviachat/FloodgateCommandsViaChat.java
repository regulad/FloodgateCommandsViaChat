package xyz.regulad.floodgatecommandsviachat;

import org.bstats.bukkit.Metrics;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;


public class FloodgateCommandsViaChat extends JavaPlugin implements Listener {
    private @Nullable String chatPrefix = null;

    @Override
    public void onEnable() {
        final @NotNull Metrics metrics = new Metrics(this, 13583);

        this.saveDefaultConfig();

        this.getServer().getPluginManager().registerEvents(this, this);
    }

    public @NotNull String getChatPrefix() {
        if (this.chatPrefix == null) {
            this.chatPrefix = this.getConfig().getString("chat_prefix", "$$");
        }
        return Objects.requireNonNull(this.chatPrefix);
    }

    @EventHandler
    public void onAsyncChatEvent(final @NotNull PlayerChatEvent chatEvent) {
        final boolean isFloodgatePlayer = FloodgateApi.getInstance().isFloodgatePlayer(chatEvent.getPlayer().getUniqueId());
        final boolean hasChatPrefix = chatEvent.getMessage().startsWith(this.getChatPrefix());
        if (isFloodgatePlayer && hasChatPrefix) {
            chatEvent.setCancelled(true);
            this.getServer().dispatchCommand(chatEvent.getPlayer(), chatEvent.getMessage().substring(this.getChatPrefix().length()));
        }
    }
}

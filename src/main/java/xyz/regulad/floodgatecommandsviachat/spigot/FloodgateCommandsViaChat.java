package xyz.regulad.floodgatecommandsviachat.spigot;

import org.bstats.bukkit.Metrics;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;


public class FloodgateCommandsViaChat extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        final @NotNull Metrics metrics = new Metrics(this, 13583);

        this.saveDefaultConfig();

        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onAsyncChatEvent(final @NotNull PlayerChatEvent chatEvent) {
        final boolean isFloodgatePlayer = FloodgateApi.getInstance().isFloodgatePlayer(chatEvent.getPlayer().getUniqueId());
        final boolean hasChatPrefix = chatEvent.getMessage().startsWith(this.getConfig().getString("chat_prefix", "$$"));
        if (isFloodgatePlayer && hasChatPrefix) {
            chatEvent.setCancelled(true);
            this.getServer().dispatchCommand(chatEvent.getPlayer(), chatEvent.getMessage().substring(this.getConfig().getString("chat_prefix", "$$").length()));
        }
    }
}

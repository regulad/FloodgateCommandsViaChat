package xyz.regulad.floodgatecommandsviachat.bungee;

import net.md_5.bungee.api.connection.ConnectedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import org.bstats.bungeecord.Metrics;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;

public class FloodgateCommandsViaChat extends Plugin implements Listener {
    private @Nullable Configuration configuration;

    @EventHandler
    public void onChatEvent(final @NotNull ChatEvent chatEvent) {
        if (chatEvent.getMessage().startsWith(this.getConfig().getString("chat_prefix")) && chatEvent.getSender() instanceof final @NotNull ConnectedPlayer connectedPlayer) {
            final boolean hasChatPrefix = chatEvent.getMessage().startsWith(this.getConfig().getString("chat_prefix", "$$"));
            final boolean isFloodgatePlayer = FloodgateApi.getInstance().isFloodgatePlayer(connectedPlayer.getUniqueId());
            if (hasChatPrefix && isFloodgatePlayer) {
                if (this.getProxy().getPluginManager().dispatchCommand(connectedPlayer, chatEvent.getMessage().substring(this.getConfig().getString("chat_prefix", "$$").length()))) {
                    chatEvent.setCancelled(true); // Got handled by the proxy. If it didn't, we needn't do anything.
                }
            }
        }
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        final @NotNull Metrics metrics = new Metrics(this, 13635);

        this.getProxy().getPluginManager().registerListener(this, this);
    }

    public void saveDefaultConfig() {
        if (!this.getDataFolder().exists()) this.getDataFolder().mkdir();

        final @NotNull File file = new File(this.getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = this.getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void reloadConfig() {
        try {
            final @NotNull ConfigurationProvider configurationProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);

            final @NotNull Configuration defaultConfiguration;
            try (InputStream defaultConfig = this.getResourceAsStream("config.yml")) {
                defaultConfiguration = configurationProvider.load(defaultConfig); // Will this break? It is barely documented.
            }

            this.configuration = configurationProvider.load(new File(this.getDataFolder(), "config.yml"), defaultConfiguration);
        } catch (IOException exception) {
            this.configuration = null;
        }
    }

    public @NotNull Configuration getConfig() {
        if (this.configuration == null) this.reloadConfig();
        return Objects.requireNonNull(this.configuration);
    }
}

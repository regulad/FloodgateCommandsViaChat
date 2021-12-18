package xyz.regulad.floodgatecommandsviachat.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import io.netty.util.concurrent.CompleteFuture;
import org.bstats.velocity.Metrics;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@Plugin(id = "floodgatecommandsviachat", name = "FloodgateCommandsViaChat", version = "1.0.0",
        description = "Allows GeyserMC players unable to send commands due to being eligible to earn achievements to " +
                "send commands by sending them in chat prefixed with a known string.", authors = {"regulad"}, dependencies = {@Dependency(id = "floodgate")})
public class FloodgateCommandsViaChat {
    @Inject
    private @NotNull ProxyServer server;
    @Inject
    private @NotNull Logger logger;
    @Inject
    private @NotNull Metrics.Factory metricsFactory;
    @Inject
    @DataDirectory
    private @NotNull Path path;

    @Subscribe
    public void onProxyInitialization(final @NotNull ProxyInitializeEvent proxyInitializeEvent) {
        final @NotNull Metrics metrics = this.metricsFactory.make(this, 13637);
    }

    @Subscribe
    public @NotNull EventTask onPlayerChatEvent(final @NotNull PlayerChatEvent playerChatEvent) {
        return EventTask.async(() -> {
            final @NotNull String commandPrefix = "$$"; // TODO: load this from config
            if (playerChatEvent.getMessage().startsWith(commandPrefix) && FloodgateApi.getInstance().isFloodgatePlayer(playerChatEvent.getPlayer().getUniqueId())) {
                final @NotNull CompletableFuture<Boolean> completeFuture = this.server.getCommandManager().executeAsync(playerChatEvent.getPlayer(), playerChatEvent.getMessage().substring(commandPrefix.length()));
                completeFuture.thenApply((final @Nullable Boolean result) -> {
                    if (Boolean.TRUE.equals(result)) {
                        playerChatEvent.setResult(PlayerChatEvent.ChatResult.denied());
                    }
                   return result;
                });
            }
        });
    }
}

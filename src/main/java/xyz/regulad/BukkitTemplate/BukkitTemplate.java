package xyz.regulad.BukkitTemplate;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * A template plugin to be used in Minecraft plugins.
 */
public class BukkitTemplate extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getLogger().info("Loaded template plugin!");
    }
}

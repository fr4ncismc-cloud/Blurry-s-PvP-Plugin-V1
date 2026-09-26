package com.example.bedrockparity;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BedrockParityPlugin extends JavaPlugin {

    private boolean floodgatePresent = false;
    final Map<UUID, BossBar> hungerBars = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("floodgate") != null) {
            floodgatePresent = true;
            getLogger().info("Floodgate detected - shield/totem/cooldown/hunger parity enabled for Bedrock players.");
        } else {
            getLogger().warning("Floodgate not found. This plugin needs Geyser + Floodgate installed; it will do nothing without them.");
            return;
        }

        getServer().getPluginManager().registerEvents(new ShieldBlockListener(this), this);
        getServer().getPluginManager().registerEvents(new TotemSwapListener(this), this);
        getServer().getPluginManager().registerEvents(new PvpHudManager(this), this);
    }

    @Override
    public void onDisable() {
        for (Player p : getServer().getOnlinePlayers()) {
            BossBar bar = hungerBars.remove(p.getUniqueId());
            if (bar != null) p.hideBossBar(bar);
        }
    }

    public boolean isBedrock(HumanEntity player) {
        return floodgatePresent && FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
    }
}

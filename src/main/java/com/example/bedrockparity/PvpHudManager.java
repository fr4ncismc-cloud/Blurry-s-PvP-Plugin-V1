package com.example.bedrockparity;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PvpHudManager implements Listener {

    private static final int BAR_SEGMENTS = 10;

    private final BedrockParityPlugin plugin;

    public PvpHudManager(BedrockParityPlugin plugin) {
        this.plugin = plugin;

        for (org.bukkit.entity.Player p : plugin.getServer().getOnlinePlayers()) {
            if (plugin.isBedrock(p)) createHungerBar(p);
        }

        plugin.getServer().getScheduler().runTaskTimer(plugin, this::tickCooldownIndicator, 1L, 2L);
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::tickHungerBar, 1L, 10L);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (plugin.isBedrock(event.getPlayer())) {
            createHungerBar(event.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        BossBar bar = plugin.hungerBars.remove(event.getPlayer().getUniqueId());
        if (bar != null) event.getPlayer().hideBossBar(bar);
    }

    private void createHungerBar(org.bukkit.entity.Player player) {
        BossBar bar = BossBar.bossBar(Component.text("Hunger"), 1.0f, BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS);
        plugin.hungerBars.put(player.getUniqueId(), bar);
        player.showBossBar(bar);
    }

    private void tickCooldownIndicator() {
        for (org.bukkit.entity.Player bukkitPlayer : plugin.getServer().getOnlinePlayers()) {
            if (!plugin.isBedrock(bukkitPlayer)) continue;

            Player nmsPlayer = ((CraftPlayer) bukkitPlayer).getHandle();
            float scale = nmsPlayer.getAttackStrengthScale(0.0f);

            if (scale >= 1.0f) continue; // fully charged: let the bar fade, don't stick it at 100%

            bukkitPlayer.sendActionBar(buildCooldownBar(scale));
        }
    }

    private Component buildCooldownBar(float scale) {
        int filled = Math.round(scale * BAR_SEGMENTS);
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < BAR_SEGMENTS; i++) {
            bar.append(i < filled ? '|' : '.');
        }
        NamedTextColor color = scale >= 1.0f ? NamedTextColor.GREEN
                : scale >= 0.5f ? NamedTextColor.YELLOW
                : NamedTextColor.RED;

        return Component.text("Attack ", NamedTextColor.GRAY)
                .append(Component.text(bar.toString(), color))
                .append(Component.text(" " + Math.round(scale * 100) + "%", NamedTextColor.GRAY));
    }

    private void tickHungerBar() {
        for (org.bukkit.entity.Player bukkitPlayer : plugin.getServer().getOnlinePlayers()) {
            BossBar bar = plugin.hungerBars.get(bukkitPlayer.getUniqueId());
            if (bar == null) continue;

            int food = bukkitPlayer.getFoodLevel();
            float saturation = bukkitPlayer.getSaturation();

            bar.progress(Math.max(0.0f, Math.min(1.0f, food / 20.0f)));

            BossBar.Color color = food <= 6 ? BossBar.Color.RED
                    : food <= 14 ? BossBar.Color.YELLOW
                    : BossBar.Color.GREEN;
            bar.color(color);

            bar.name(Component.text("Hunger " + food + "/20", NamedTextColor.GOLD)
                    .append(Component.text("  |  Saturation " + String.format("%.1f", saturation), NamedTextColor.AQUA)));
        }
    }
}

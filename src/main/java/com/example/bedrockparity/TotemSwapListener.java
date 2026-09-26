package com.example.bedrockparity;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class TotemSwapListener implements Listener {

    private final BedrockParityPlugin plugin;

    public TotemSwapListener(BedrockParityPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return; // only care about the main-hand item

        Action action = event.getAction();
        boolean rightClickAir = action == Action.RIGHT_CLICK_AIR;
        boolean rightClickBlockWhileSneaking = action == Action.RIGHT_CLICK_BLOCK && event.getPlayer().isSneaking();
        if (!rightClickAir && !rightClickBlockWhileSneaking) return;

        Player player = event.getPlayer();
        if (!plugin.isBedrock(player)) return;

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand.getType() != Material.TOTEM_OF_UNDYING) return;

        ItemStack offHand = player.getInventory().getItemInOffHand();

        player.getInventory().setItemInOffHand(mainHand);
        player.getInventory().setItemInMainHand(offHand);

        event.setCancelled(true);
    }
}

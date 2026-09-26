package com.example.bedrockparity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ShieldBlockListener implements Listener {

    private final BedrockParityPlugin plugin;

    public ShieldBlockListener(BedrockParityPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> { /* keep going */ }
            default -> { return; }
        }

        HumanEntity bukkitPlayer = event.getPlayer();
        if (!plugin.isBedrock(bukkitPlayer)) return;

        EquipmentSlot slot = event.getHand();
        if (slot == null) return;

        ItemStack stack = slot == EquipmentSlot.OFF_HAND
                ? bukkitPlayer.getInventory().getItemInOffHand()
                : bukkitPlayer.getInventory().getItemInMainHand();

        if (stack.getType() != Material.SHIELD) return;

        Player nmsPlayer = ((CraftPlayer) bukkitPlayer).getHandle();
        InteractionHand hand = slot == EquipmentSlot.OFF_HAND
                ? InteractionHand.OFF_HAND
                : InteractionHand.MAIN_HAND;

        if (nmsPlayer.isUsingItem() && nmsPlayer.getUseItem() == nmsPlayer.getItemInHand(hand)) {
            return; // already blocking with this exact stack
        }

        nmsPlayer.startUsingItem(hand);
    }
}

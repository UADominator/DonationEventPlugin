package org.dominator.donationEvents.events;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.dominator.donationEvents.DonationEvents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CustomEvents {
    private final DonationEvents plugin;

    public CustomEvents(DonationEvents plugin) {
        this.plugin = plugin;
    }

    public void shuffleInventory(Player player) {
        plugin.getLogger().info("Перемішую інвентар");

        ItemStack[] inventoryContents = player.getInventory().getContents();

        List<ItemStack> inventoryItems = new ArrayList<>(Arrays.asList(inventoryContents));

        Collections.shuffle(inventoryItems);

        player.getInventory().clear();

        for (int i = 0; i < inventoryItems.size(); i++) {
            player.getInventory().setItem(i, inventoryItems.get(i));
        }

        for (int i = inventoryItems.size(); i < inventoryContents.length; i++) {
            player.getInventory().setItem(i, null);
        }
    }

    public void manyRandDonations(float sum){
        plugin.getLogger().info("Перемішую інвентар");
        plugin.customEventHandler.eventsCrossroads(10); //TODO: rand sum

    }
}

package org.dominator.donationEvents.events;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CustomEvents {
    public static void shuffleInventory(Player player) {
        System.out.println("Перемішую інвентар");

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

    public static void manyRandEvents(float sum){
        System.out.println("Перемішую інвентар");
        CustomEventHandler.eventsCrossroads(10); //TODO: rand sum

    }
}

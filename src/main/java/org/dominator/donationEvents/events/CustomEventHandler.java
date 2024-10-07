package org.dominator.donationEvents.events;

import org.bukkit.entity.EntityType;
import org.dominator.donationEvents.DonationEvents;

public class CustomEventHandler {
    private final DonationEvents plugin;

    public CustomEventHandler(DonationEvents plugin){
        this.plugin = plugin;
    }
    public void eventsCrossroads(double amount){
        plugin.getLogger().info("Вхідна сума: " + amount);
        if (amount >= 0 && amount <= 100) plugin.mobSpawner.spawnMob(EntityType.ZOMBIE);  plugin.getLogger().info("Викликаю івент із сумою: 0 - 100");
    }
}

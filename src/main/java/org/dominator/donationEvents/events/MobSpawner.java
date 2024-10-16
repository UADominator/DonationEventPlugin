package org.dominator.donationEvents.events;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.dominator.donationEvents.DonationEvents;

public class MobSpawner {
        private final DonationEvents plugin;

        public MobSpawner(DonationEvents plugin) {
            this.plugin = plugin;
        }

        public void spawnMob(EntityType mobType) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Location spawnLocation = player.getLocation(); // Зсув на 2 блоки вперед від позиції гравця
                    player.getWorld().spawnEntity(spawnLocation, mobType); // Спавним моба
                    plugin.getLogger().info("Спавн моба " + mobType.name() + " біля гравця " + player.getName());
                }
            });
        }
}




package org.dominator.donationEvents.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class MobSpawner {

    private final JavaPlugin plugin;

    public MobSpawner(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // Метод для спаунду моба
    public void spawnMob(EntityType entityType, Location location) {
        World world = location.getWorld();
        if (world != null) {
            LivingEntity mob = (LivingEntity) world.spawnEntity(location, entityType);
            Bukkit.getLogger().info("Спавн моба: " + entityType.name() + " на позиції " + location.toString());
        }
    }

    // Метод для періодичного спаунду
    public void startMobSpawnTask(final EntityType entityType, final Location location) {
        new BukkitRunnable() {
            @Override
            public void run() {
                spawnMob(entityType, location);
            }
        };
    }
}



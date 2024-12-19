package org.dominator.donationEvents.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.dominator.donationEvents.DonationEvents;

public class EventExecute {
    private final DonationEvents plugin;
    public final CustomEvents evList;

    public EventExecute(DonationEvents plugin) {
        this.plugin = plugin;
        this.evList = new CustomEvents(plugin);
    }

    public void commandUseStringCordsString(EventsArrays.Events.Commands command, Player play){
        Location location = play.getLocation();
        int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();

        String newCommand = String.format(command.command, x, y, z, command.addition);

        plugin.getLogger().info("Викликав команду " + newCommand);

        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), newCommand);
    }

    public void commandUseStringPlayerString(EventsArrays.Events.Commands command, Player play){
        String newCommand = String.format(command.command, play.getName(), command.addition);
        plugin.getLogger().info("Викликав команду " + newCommand);
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), newCommand);
    }

    public void commandUseStringPlayerCordsString(EventsArrays.Events.Commands command, Player play){
        Location location = play.getLocation();
        int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();

        String newCommand = String.format(command.command, play, x, y, z, command.addition);
        plugin.getLogger().info("Викликав команду " + newCommand);
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), newCommand);
    }

    public void commandUseCustomEvent(EventsArrays.Events.Commands command, Player play){
        // TODO: тут тре буде ще щось
        switch (command.command){
            case "inventoryShake" -> evList.shuffleInventory(play);
            case "manyEvents" -> {}
        }
    }
}

package org.dominator.donationEvents.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.dominator.donationEvents.DonationEvents;

public class EventExecute {

    public static void commandUseStringCordsString(EventsArrays.Events.Commands command, Player play){
        Location location = play.getLocation();
        int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();

        String newCommand = String.format(command.command, x, y, z, command.addition);
        DonationEvents.instance.getServer().dispatchCommand(DonationEvents.instance.getServer().getConsoleSender(), newCommand);
    }

    public static void commandUseStringPlayerString(EventsArrays.Events.Commands command, Player play){
        String newCommand = String.format(command.command, play.getName(), command.addition);
        DonationEvents.instance.getServer().dispatchCommand(DonationEvents.instance.getServer().getConsoleSender(), newCommand);
    }

    public static void commandUseStringPlayerCordsString(EventsArrays.Events.Commands command, Player play){
        Location location = play.getLocation();
        int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();

        String newCommand = String.format(command.command, play, x, y, z, command.addition);
        DonationEvents.instance.getServer().dispatchCommand(DonationEvents.instance.getServer().getConsoleSender(), newCommand);
    }

    public static void commandUseCustomEvent(EventsArrays.Events.Commands command, Player play){
        // TODO: тут тре буде ще щось
        switch (command.command){
            case "inventoryShake" -> CustomEvents.shuffleInventory(play);
            case "manyEvents" -> {}
        }
    }
}

package org.dominator.donationEvents.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import java.util.Random;
import org.bukkit.entity.Player;
import org.dominator.donationEvents.DonationEvents;

import java.util.ArrayList;
import java.util.List;

public class CustomEventHandler {
    private final DonationEvents plugin;

    public CustomEventHandler(DonationEvents plugin){
        this.plugin = plugin;
    }

    public void eventsCrossroads(double amount) {
        plugin.getLogger().info("Вхідна сума: " + amount);

        plugin.getLogger().info("Кількість всіх івентів: " + plugin.eventsArraysList.size());

        List<EventsArrays> newEvents = new ArrayList<>();
        for (EventsArrays oth : plugin.eventsArraysList){
            if (oth.inRangePrice(amount)){
                newEvents.add(oth);
            }
        }
        int randInt = getRandInt(newEvents.size());
        plugin.getLogger().info("Кількість доступних івентів: " + newEvents.size());
        plugin.getLogger().info("Випадкове число: " + randInt);
        commandUse(newEvents.get(randInt));
    }

    private void commandUse(EventsArrays event){
        for (EventsArrays.Events.Commands command : event.events.commands) {
            switch (command.type){
                case STRING_CORDS_STRING -> commandUseStringCordsString(command);
                case STRING_PLAYER_STRING -> commandUseStringPlayerString(command);
                case STRING_PLAYER_CORDS_STRING -> commandUseStringPlayerCordsString(command);
                case CUSTOM_EVENT -> commandUseCustomEvent(command);
            }
        }
    }

    public void commandUseStringCordsString(EventsArrays.Events.Commands command){
        Player play = getFirstOnlinePlayer();
        Location location = play.getLocation();
        int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();

        String newCommand = String.format(command.command,  x, y, z, command.addition);

        plugin.getLogger().info("Викликав команду " + newCommand);

        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), newCommand);
    }

    public void commandUseStringPlayerString(EventsArrays.Events.Commands command){
        Player play = getFirstOnlinePlayer();

        String newCommand = String.format(command.command, play.getName(), command.addition);

        plugin.getLogger().info("Викликав команду " + newCommand);

        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), newCommand);

    }

    public void commandUseStringPlayerCordsString(EventsArrays.Events.Commands command){
        Player play = getFirstOnlinePlayer();
        Location location = play.getLocation();
        int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();

        String newCommand = String.format(command.command, play,  x, y, z, command.addition);

        plugin.getLogger().info("Викликав команду " + newCommand);

        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), newCommand);

    }

    public void commandUseCustomEvent(EventsArrays.Events.Commands command){
        // TODO: тут тре буде щось
    }

    public Player getFirstOnlinePlayer() {
        Player[] onlinePlayers = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        if (onlinePlayers.length > 0) {
            return  onlinePlayers[0];
        } else {
            return null;
        }
    }

    public int getRandInt(int endInt){
        return new Random().nextInt(endInt);
    }
}

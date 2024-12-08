package org.dominator.donationEvents.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.*;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.dominator.donationEvents.DonationEvents;

public class CustomEventHandler {

    private final DonationEvents plugin;

    public CustomEventHandler(DonationEvents plugin){
        this.plugin = plugin;
    }

    public void eventsCrossroads(double amount) {
        plugin.getLogger().info("Обробка суми: " + amount);


        List<EventsArrays> newEvents = new ArrayList<>();
        for (EventsArrays oth : plugin.eventsArraysList){
            if (oth.inRangePrice(amount)){
                newEvents.add(oth);
            }
        }
        if (newEvents.size() > 0) {
            int randInt = getRandInt(newEvents.size());
            plugin.getLogger().info("Кількість доступних івентів: " + newEvents.size());
            plugin.getLogger().info("Випадкове число: " + randInt);
            commandUse(newEvents.get(randInt));
        } else {
            plugin.getLogger().info("Відсутні доступні івенти");
        }
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
        //for (Player play : Bukkit.getOnlinePlayers()) {
            Player play = Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.getName().equalsIgnoreCase("Kvadratnyk")) // TODO: Horfixed execute only Kva
                    .findFirst()
                    .orElse(null);
            Location location = play.getLocation();
            int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();

            String newCommand = String.format(command.command, x, y, z, command.addition);

            plugin.getLogger().info("Викликав команду " + newCommand);

            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), newCommand);
        //}
    }

    public void commandUseStringPlayerString(EventsArrays.Events.Commands command){
        //for (Player play : Bukkit.getOnlinePlayers()) {
            Player play = Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.getName().equalsIgnoreCase("Kvadratnyk")) // TODO: Horfixed execute only Kva
                    .findFirst()
                    .orElse(null);

            String newCommand = String.format(command.command, play.getName(), command.addition);
            plugin.getLogger().info("Викликав команду " + newCommand);
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), newCommand);
        //}
    }

    public void commandUseStringPlayerCordsString(EventsArrays.Events.Commands command){
        //for (Player play : Bukkit.getOnlinePlayers()) {
        Player play = Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getName().equalsIgnoreCase("Kvadratnyk")) // TODO: Horfixed execute only Kva
                .findFirst()
                .orElse(null);

            Location location = play.getLocation();
            int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();

            String newCommand = String.format(command.command, play, x, y, z, command.addition);
            plugin.getLogger().info("Викликав команду " + newCommand);
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), newCommand);
        //}
    }

    public void commandUseCustomEvent(EventsArrays.Events.Commands command){
        // TODO: тут тре буде ще щось
        switch (command.command){
            case "inventoryShake" -> {
                Player play = Bukkit.getOnlinePlayers().stream()
                        .filter(player -> player.getName().equalsIgnoreCase("Kvadratnyk")) // TODO: Horfixed execute only Kva
                        .findFirst()
                        .orElse(null);
                //for (Player player : Bukkit.getOnlinePlayers()){
                    shuffleInventory(play);
                }
            case "manyEvents" -> {

            }
        }
    }

    public int getRandInt(int endInt){
        return new Random().nextInt(endInt);
    }


    ///////////////////////CUSTOM EVENTS//////////////////////////////////


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
        eventsCrossroads(10); //TODO: rand sum

    }
}

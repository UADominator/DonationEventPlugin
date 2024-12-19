package org.dominator.donationEvents.events;

import org.bukkit.Bukkit;
import java.util.*;
import org.bukkit.entity.Player;
import org.dominator.donationEvents.DonationEvents;

public class CustomEventHandler {

    private final DonationEvents plugin;
    private final EventExecute execute;

    public CustomEventHandler(DonationEvents plugin){
        this.plugin = plugin;
        this.execute = new EventExecute(plugin);
    }

    public void eventsCrossroads(double amount) {
        plugin.getLogger().info("Обробка суми: " + amount);
        List<EventsArrays> newEvents = new ArrayList<>();
        for (EventsArrays oth : plugin.eventsArraysList){
            if (oth.inRangePrice(amount)){
                newEvents.add(oth);
            }
        }

        if (!newEvents.isEmpty()) {
            int randInt = getRandInt(newEvents.size());
            plugin.getLogger().info("Кількість доступних івентів: " + newEvents.size());
            if (!DonationEvents.useTargetName) {
                for (Player play : Bukkit.getOnlinePlayers()){
                    commandUse(newEvents.get(randInt), play);
                }
            } else {
                Player play = Bukkit.getOnlinePlayers().stream()
                        .filter(player -> player.getName().equalsIgnoreCase(DonationEvents.targetName))
                        .findFirst()
                        .orElse(null);
                commandUse(newEvents.get(randInt), play);
            }
        } else {
            plugin.getLogger().info("Відсутні доступні івенти");
        }
    }

    private void commandUse(EventsArrays event, Player player){
        for (EventsArrays.Events.Commands command : event.events.commands) {
            switch (command.type){
                case STRING_CORDS_STRING -> execute.commandUseStringCordsString(command, player);
                case STRING_PLAYER_STRING -> execute.commandUseStringPlayerString(command, player);
                case STRING_PLAYER_CORDS_STRING -> execute.commandUseStringPlayerCordsString(command, player);
                case CUSTOM_EVENT -> execute.commandUseCustomEvent(command, player);
            }
        }
    }


    public int getRandInt(int endInt){
        return new Random().nextInt(endInt);
    }



}

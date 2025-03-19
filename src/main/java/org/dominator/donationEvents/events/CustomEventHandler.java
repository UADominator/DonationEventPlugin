package org.dominator.donationEvents.events;

import org.bukkit.Bukkit;
import java.util.*;
import org.bukkit.entity.Player;
import org.dominator.donationEvents.DonationEvents;

public class CustomEventHandler {
    public static void eventsCrossroads(double amount) {
        List<EventsArrays> newEvents = new ArrayList<>();
        for (EventsArrays oth : DonationEvents.eventsArraysList){
            if (oth.inRangePrice(amount)){
                newEvents.add(oth);
            }
        }

        if (!newEvents.isEmpty()) {
            int randInt = getRandInt(newEvents.size());
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
            System.out.println("Відсутні доступні івенти");
        }
    }

    private static void commandUse(EventsArrays event, Player player){
        for (EventsArrays.Events.Commands command : event.events.commands) {
            switch (command.type){
                case STRING_CORDS_STRING -> EventExecute.commandUseStringCordsString(command, player);
                case STRING_PLAYER_STRING -> EventExecute.commandUseStringPlayerString(command, player);
                case STRING_PLAYER_CORDS_STRING -> EventExecute.commandUseStringPlayerCordsString(command, player);
                case CUSTOM_EVENT -> EventExecute.commandUseCustomEvent(command, player);
            }
        }
    }


    public static int getRandInt(int endInt){
        return new Random().nextInt(endInt);
    }
}

package org.dominator.donationEvents.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.dominator.donationEvents.useAPI.APIManager;
import org.dominator.donationEvents.useAPI.DonatelloAPI;

public class GetLastDonationsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("donationevents.view")) {
            sender.sendMessage("Ви не маєте прав для виконання цієї команди.");
            return false;
        }

        String  token = APIManager.getAPIkey(1);
        if (token == null) {
            System.out.println("Токен для Donatello API == null.");
            return false;
        }

        DonatelloAPI.getJsonDonators(token).thenAccept(response -> {
            if (response != null) {
                sender.sendMessage(response);
            } else {
                sender.sendMessage("Не вдалося отримати дані донатів.");
            }
        });

        return true;
    }
}


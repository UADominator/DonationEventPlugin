package org.dominator.donationEvents.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.dominator.donationEvents.DonationEvents;

public class GetLastDonationsCommand implements CommandExecutor {

    private final DonationEvents plugin;

    public GetLastDonationsCommand(DonationEvents plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("donationevents.view")) {
            sender.sendMessage("Ви не маєте прав для виконання цієї команди.");
            return false;
        }

        String  token = plugin.api.getAPIkey(2);
        if (token == null) {
            plugin.getLogger().severe("Токен для Donatello API == null.");
            return false;
        }

        plugin.donatelloAPI.getJsonDonators(token).thenAccept(response -> {
            if (response != null) {
                sender.sendMessage(response);
            } else {
                sender.sendMessage("Не вдалося отримати дані донатів.");
            }
        });

        return true;
    }
}


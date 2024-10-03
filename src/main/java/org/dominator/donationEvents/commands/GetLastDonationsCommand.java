package org.dominator.donationEvents.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.dominator.donationEvents.DonationEvents;
import org.dominator.donationEvents.workWithApi.APIusageType.DonatelloAPI;
import org.dominator.donationEvents.workWithApi.APIusageType.DyakaAPI;

public class GetLastDonationsCommand implements CommandExecutor {

    private final DonationEvents plugin;

    public GetLastDonationsCommand(DonationEvents plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Перевірка наявності прав
        if (!sender.hasPermission("donationevents.view") && plugin.api.getAPIkey().equals("")) {
            sender.sendMessage("Ви не маєте прав для виконання цієї команди.");
            return false;
        }

        if (DonationEvents.api.getAPIkey().equals("")) {
            sender.sendMessage("Встановіть ключ.");
            return false;
        }

        new DyakaAPI(plugin).getJsonDonators(DonationEvents.api.getAPIkey());
        new DonatelloAPI(plugin).getJsonDonators(DonationEvents.api.getAPIkey());
        return true;
    }
}


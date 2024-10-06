package org.dominator.donationEvents.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.dominator.donationEvents.DonationEvents;

public class SetApiKeyCommand implements CommandExecutor {
    private final DonationEvents plugin;

    public SetApiKeyCommand(DonationEvents plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("setAPIKey.view")) {
            sender.sendMessage("Ви не маєте прав для виконання цієї команди.");
            return false;
        }

        if(args.length < 2 ){
            sender.sendMessage("Встановіть ключ 1-Dyaka 2-Donatello 3-Mono");
            return false;
        }

        plugin.api.setAPIkey(args[0], Integer.parseInt(args[1]));

        return true;
    }
}

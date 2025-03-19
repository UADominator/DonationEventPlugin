package org.dominator.donationEvents.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.dominator.donationEvents.useAPI.APIManager;

public class SetApiKeyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("setAPIKey.view")) {
            sender.sendMessage("Ви не маєте прав для виконання цієї команди.");
            return false;
        }

        if(args.length < 2){
            sender.sendMessage("Встановіть ключ 1-Donatello");
            return false;
        }

        APIManager.setAPIkey(args[0], Integer.parseInt(args[1]));
        return true;
    }
}

package org.dominator.donationEvents.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.dominator.donationEvents.events.CustomEventHandler;

public class StartRandEvent implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            CustomEventHandler.eventsCrossroads(Double.parseDouble(args[0]));
            sender.sendMessage("Викликано випадковий івент із сумою: " + Double.parseDouble(args[0]));
        } else {
            CustomEventHandler.eventsCrossroads(1);
            sender.sendMessage("Викликано випадковий івент із сумою: " + 1);
        }

        return true;
    }
}
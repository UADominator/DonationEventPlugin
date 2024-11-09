package org.dominator.donationEvents.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.dominator.donationEvents.DonationEvents;

public class StartRandEvent implements CommandExecutor {
    private final DonationEvents plugin;

    public StartRandEvent(DonationEvents plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            plugin.customEventHandler.eventsCrossroads(Double.parseDouble(args[0]));
            sender.sendMessage("Викликано випадковий івент із сумою: " + Double.parseDouble(args[0]));
        }else {
            plugin.customEventHandler.eventsCrossroads(1);
            sender.sendMessage("Викликано випадковий івент із сумою: " + 1);
        }

        return true;
    }
}
package org.dominator.donationEvents.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.dominator.donationEvents.DonationEvents;

public class ReLoadEvents implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        DonationEvents.instance.loadJsonFile();
        DonationEvents.instance.saveJsonFile();
        sender.sendMessage(ChatColor.GREEN + "Івенти оновлено");
        return true;
    }
}

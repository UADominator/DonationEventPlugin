package org.dominator.donationEvents.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.dominator.donationEvents.DonationEvents;

public class ReLoadEvents implements CommandExecutor {
    private final DonationEvents plugin;

    public ReLoadEvents(DonationEvents plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        plugin.loadJsonFile();
        plugin.saveJsonFile();
        sender.sendMessage(ChatColor.GREEN + "Івенти оновлено");
        return true;
    }
}

package org.dominator.donationEvents.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dominator.donationEvents.DonationEvents;
import org.dominator.donationEvents.menu.CustomMenu;

import java.util.Arrays;

public class OpenSettingsMenu implements CommandExecutor {
    private final DonationEvents plugin;

    public OpenSettingsMenu(DonationEvents plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            CustomMenu menu = new CustomMenu(plugin);
            player.openInventory(menu.getInventory());
            return true;
        }
        sender.sendMessage(ChatColor.RED + "Цю команду можна використовувати тільки гравцями!");
        return false;
    }
}

package org.dominator.donationEvents.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.dominator.donationEvents.menu.CustomMenu;

public class OpenSettingsMenu implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player) {
            player.openInventory(CustomMenu.getInventory());
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Цю команду можна використовувати тільки гравцями!");
        return false;
    }
}

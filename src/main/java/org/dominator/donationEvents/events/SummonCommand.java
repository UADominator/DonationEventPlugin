package org.dominator.donationEvents.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.dominator.donationEvents.DonationEvents;

public class SummonCommand {
    private final DonationEvents plugin;

    public SummonCommand(DonationEvents plugin) {
        this.plugin = plugin;
    }

    public void summonCommand(String command){
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    /**
     * @param command Example: command = "summon zombie %d %d %d %s";
     * @param location
     * @param  params addition params(effects)
     */
    public void summonCommand(String command, Location location, String params){


        String formattedCommand = String.format(command, location.getBlockX(), location.getBlockY(), location.getBlockZ(), params);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formattedCommand);
    }
}

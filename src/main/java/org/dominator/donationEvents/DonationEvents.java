package org.dominator.donationEvents;

import org.bukkit.plugin.java.JavaPlugin;
import org.dominator.donationEvents.commands.GetLastDonationsCommand;
import org.dominator.donationEvents.workWithApi.APIMenager;

public final class DonationEvents extends JavaPlugin {
    public APIMenager api;
    @Override
    public void onEnable() {
        getLogger().info("DonationEvents плагін увімкнено!");
        api = new APIMenager("");
        this.getCommand("setAPIkey").setExecutor(new GetLastDonationsCommand(this));
        this.getCommand("lastdonations").setExecutor(new GetLastDonationsCommand(this));
    }

    @Override
    public void onDisable() {
        // Логіка вимкнення плагіна
        getLogger().info("DonationEvents плагін вимкнено.");
    }
}

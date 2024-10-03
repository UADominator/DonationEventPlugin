package org.dominator.donationEvents;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.dominator.donationEvents.commands.GetLastDonationsCommand;
import org.dominator.donationEvents.commands.SetApiKeyCommand;
import org.dominator.donationEvents.workWithApi.APIMenager;
import org.dominator.donationEvents.workWithApi.APIusageType.DonatelloAPI;
import org.dominator.donationEvents.workWithApi.APIusageType.DyakaAPI;

public final class DonationEvents extends JavaPlugin {
    public static APIMenager api;
    private DyakaAPI dyakaAPI = new DyakaAPI(this);
    private DonatelloAPI donatelloAPI = new DonatelloAPI(this);

    @Override
    public void onEnable() {
        getLogger().info("DonationEvents плагін увімкнено!");
        api = new APIMenager("", "0");
        this.getCommand("setAPIkey").setExecutor(new SetApiKeyCommand(this));
        this.getCommand("lastdonations").setExecutor(new GetLastDonationsCommand(this));

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            switch(api.getKeyType()){
                case "1": dyakaAPI.checkForNewDonations();
                case "2": donatelloAPI.checkForNewDonations();
                case "3": ;
            }
        }, 0L, 20L);
    }

    @Override
    public void onDisable() {
        // Логіка вимкнення плагіна
        getLogger().info("DonationEvents плагін вимкнено.");
    }
}

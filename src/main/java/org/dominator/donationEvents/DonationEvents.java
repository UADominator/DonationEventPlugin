package org.dominator.donationEvents;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.dominator.donationEvents.commands.GetLastDonationsCommand;
import org.dominator.donationEvents.commands.SetApiKeyCommand;
import org.dominator.donationEvents.events.MobSpawner;
import org.dominator.donationEvents.workWithApi.APIMenager;
import org.dominator.donationEvents.workWithApi.APIusageType.DonatelloAPI;
import org.dominator.donationEvents.workWithApi.APIusageType.DyakaAPI;
import org.dominator.donationEvents.workWithApi.APIusageType.MonobankAPI;

public final class DonationEvents extends JavaPlugin {
    public APIMenager api = new APIMenager();;
    public DyakaAPI dyakaAPI;
    public DonatelloAPI donatelloAPI;
    public MonobankAPI monobankAPI;
    public MobSpawner mobSpawner;


    @Override
    public void onEnable() {
        getLogger().info("DonationEvents плагін увімкнено!");

        saveDefaultConfig();

        api.setAPIkey(getConfig().getString("settings.dyakaAPI"), 1);
        api.setAPIkey(getConfig().getString("settings.donatelloAPI"), 2);
        api.setAPIkey(getConfig().getString("settings.monoAPI"), 3);

        dyakaAPI = new DyakaAPI(this);
        donatelloAPI = new DonatelloAPI(this);
        monobankAPI = new MonobankAPI(this);
        mobSpawner = new MobSpawner(this);

        donatelloAPI.startDonationCheckTask();

        this.getCommand("setAPIkey").setExecutor(new SetApiKeyCommand(this));
        this.getCommand("lastdonations").setExecutor(new GetLastDonationsCommand(this));


    }

    @Override
    public void onDisable() {
        getLogger().info("DonationEvents плагін вимкнено.");
    }
}

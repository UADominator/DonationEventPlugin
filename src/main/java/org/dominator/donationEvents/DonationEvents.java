package org.dominator.donationEvents;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.dominator.donationEvents.commands.GetLastDonationsCommand;
import org.dominator.donationEvents.commands.SetApiKeyCommand;
import org.dominator.donationEvents.events.CustomEventHandler;
import org.dominator.donationEvents.events.MobSpawner;
import org.dominator.donationEvents.lastDonators.DonationsInformation;
import org.dominator.donationEvents.workWithApi.APIMenager;
import org.dominator.donationEvents.workWithApi.APIusageType.DonatelloAPI;
import org.dominator.donationEvents.workWithApi.APIusageType.DyakaAPI;
import org.dominator.donationEvents.workWithApi.APIusageType.MonobankAPI;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class DonationEvents extends JavaPlugin {
    public APIMenager api = new APIMenager();
    public DyakaAPI dyakaAPI;
    public DonatelloAPI donatelloAPI;
    public MonobankAPI monobankAPI;
    public MobSpawner mobSpawner;
    public CustomEventHandler customEventHandler;

    public List<DonationsInformation> donationsInformation = new ArrayList<>(10);


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

        customEventHandler = new CustomEventHandler(this);
        mobSpawner = new MobSpawner(this);

        startDonationCheckTask();

        this.getCommand("setAPIkey").setExecutor(new SetApiKeyCommand(this));
        this.getCommand("lastdonations").setExecutor(new GetLastDonationsCommand(this));


    }

    public void startDonationCheckTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                donatelloAPI.getJsonDonators(api.getAPIkey(2)).thenAccept(response -> {
                    if (response != null) {
                        addDonate(response);
                    }
                }).exceptionally(throwable -> {
                    DonationEvents.this.getLogger().info("Виникла помилка при отриманні донатів: " + throwable.getMessage());
                    return null;
                });

            }
        }.runTaskTimer(this, 0L, 20L * 20);
    }


    public void addDonate(String response){
        Gson gson = new Gson();
        JsonObject json = gson.fromJson(response, JsonObject.class);
        JsonArray contentArray = json.getAsJsonArray("content");

        for (int i = 0; i < contentArray.size(); i++) {
            JsonObject donationJson = contentArray.get(i).getAsJsonObject();
            if (!donationsInformation.contains(new DonationsInformation(donationJson))) {
                if (donationsInformation.size() == 10 ) {
                    donationsInformation.remove(0);
                }
                donationsInformation.add(new DonationsInformation(donationJson));
                customEventHandler.eventsCrossroads(donationJson.get("amount").getAsDouble());
                DonationEvents.this.getLogger().info("Додано донат: " + contentArray.get(i).getAsJsonObject().toString());
            }
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("DonationEvents плагін вимкнено.");
    }
}

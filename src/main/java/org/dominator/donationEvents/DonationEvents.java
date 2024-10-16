package org.dominator.donationEvents;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.dominator.donationEvents.commands.GetLastDonationsCommand;
import org.dominator.donationEvents.commands.SetApiKeyCommand;
import org.dominator.donationEvents.events.CustomEventHandler;
import org.dominator.donationEvents.events.EventsArrays;
import org.dominator.donationEvents.events.MobSpawner;
import org.dominator.donationEvents.events.SummonCommand;
import org.dominator.donationEvents.lastDonators.DonationsInformation;
import org.dominator.donationEvents.workWithApi.APIMenager;
import org.dominator.donationEvents.workWithApi.APIusageType.DonatelloAPI;
import org.dominator.donationEvents.workWithApi.APIusageType.DyakaAPI;
import org.dominator.donationEvents.workWithApi.APIusageType.MonobankAPI;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;


public final class DonationEvents extends JavaPlugin {
    public APIMenager api = new APIMenager();
    public DyakaAPI dyakaAPI;
    public DonatelloAPI donatelloAPI;
    public MonobankAPI monobankAPI;
    public MobSpawner mobSpawner;
    public CustomEventHandler customEventHandler;
    public SummonCommand summonCommand;
    public List<DonationsInformation> donationsInformation = new ArrayList<>(10);
    public DonationsInformation.DateTime startTime;
    private File jsonFile;
    private Gson gson;
    public List<EventsArrays> eventsArraysList;

    @Override
    public void onEnable() {
        getLogger().info("DonationEvents плагін увімкнено!");

        startTime = new DonationsInformation.DateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        DonationEvents.this.getLogger().info("Час запуску серверу: " + startTime.toString());

        gson = new GsonBuilder().setPrettyPrinting().create();

        jsonFile = new File(getDataFolder(), "events.json");
        if (!jsonFile.exists()) {
            try {
                jsonFile.getParentFile().mkdirs();
                jsonFile.createNewFile();
                eventsArraysList = new ArrayList<>();
                saveJsonFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            loadJsonFile();
        }

        saveDefaultConfig();

        api.setAPIkey(getConfig().getString("settings.dyakaAPI"), 1);
        api.setAPIkey(getConfig().getString("settings.donatelloAPI"), 2);
        api.setAPIkey(getConfig().getString("settings.monoAPI"), 3);

        dyakaAPI = new DyakaAPI(this);
        donatelloAPI = new DonatelloAPI(this);
        monobankAPI = new MonobankAPI(this);
        customEventHandler = new CustomEventHandler(this);
        mobSpawner = new MobSpawner(this);
        summonCommand = new SummonCommand(this);

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
            if (!donationsInformation.contains(new DonationsInformation(donationJson)) &&
                new DonationsInformation.DateTime(donationJson.get("createdAt").getAsString()).isOlder(startTime)
                ) {
                if (donationsInformation.size() == 10 ) {
                    donationsInformation.remove(0);
                }
                donationsInformation.add(new DonationsInformation(donationJson));
                DonationEvents.this.getLogger().info("Додано донат: " + contentArray.get(i).getAsJsonObject().toString());
                Bukkit.getScheduler().runTask(this, () -> {
                    customEventHandler.eventsCrossroads(donationJson.get("amount").getAsDouble());
                });
            }
        }
    }

    private void saveJsonFile() {
        try (FileWriter writer = new FileWriter(jsonFile)) {
            gson.toJson(eventsArraysList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadJsonFile() {
        try (FileReader reader = new FileReader(jsonFile)) {
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            eventsArraysList = gson.fromJson(jsonObject.getAsJsonArray("eventsArrays"), new TypeToken<List<EventsArrays>>() {}.getType());
            if (eventsArraysList == null) {
                eventsArraysList = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        saveJsonFile();
        getLogger().info("DonationEvents плагін вимкнено.");
    }
}

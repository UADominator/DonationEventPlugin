package org.dominator.donationEvents;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.dominator.donationEvents.commands.*;
import org.dominator.donationEvents.events.CustomEventHandler;
import org.dominator.donationEvents.events.EventsArrays;
import org.dominator.donationEvents.lastDonators.DonationsInformation;
import org.dominator.donationEvents.menu.CustomMenu;
import org.dominator.donationEvents.useAPI.APIManager;
import org.dominator.donationEvents.useAPI.DonatelloAPI;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;


public final class DonationEvents extends JavaPlugin {
    public static DonationEvents instance;

    private File jsonFile;
    private Gson gson;

    public List<DonationsInformation> donationsInformation = new ArrayList<>(10);
    public static List<EventsArrays> eventsArraysList;

    public static boolean acceptEvents = true;
    public static boolean useTargetName = false;
    public static DonationsInformation.DateTime startTime;
    public static String targetName = "";



    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("DonationEvents плагін увімкнено!");

        updateStartTime(); //Set zero point

        gson = new GsonBuilder().setPrettyPrinting().create();

        saveResource("usage.txt", true);
        saveResource("events.json", false);

        jsonFile = new File(getDataFolder(), "events.json");
        if (!jsonFile.exists()) {
            try {
                jsonFile.getParentFile().mkdirs();
                jsonFile.createNewFile();
                eventsArraysList = new ArrayList<>();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            loadJsonFile();
        }

        saveDefaultConfig();

        targetName = getConfig().getString("settings.targetName"); //Use event only for this player
        useTargetName = getConfig().getBoolean("settings.useTargetName"); //Use event only for one player

        APIManager.setAPIkey(getConfig().getString("settings.donatelloAPI"), 1);

        startDonationCheckTask();

        this.getCommand("setAPIkey").setExecutor(new SetApiKeyCommand());
        this.getCommand("lastDonations").setExecutor(new GetLastDonationsCommand());
        this.getCommand("openSettingsMenu").setExecutor(new OpenSettingsMenu());
        this.getCommand("randEventSum").setExecutor(new StartRandEvent());
        this.getCommand("reloadEvents").setExecutor(new ReLoadEvents());

        getServer().getPluginManager().registerEvents(new CustomMenu(), this);
    }

    public void updateStartTime(){
        startTime = new DonationsInformation.DateTime(LocalDateTime.now().plusHours(getConfig().getInt("settings.offsetFromDonatelloTime")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        DonationEvents.this.getLogger().info("Час запуску сервера: " + startTime.toString());
    }

    public void startDonationCheckTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (acceptEvents) {
                    DonatelloAPI.getJsonDonators(APIManager.getAPIkey(1)).thenAccept(response -> {
                        if (response != null) {
                            addDonate(response);
                        }
                    }).exceptionally(throwable -> {
                        DonationEvents.this.getLogger().info("Виникла помилка при отриманні донатів: " + throwable.getMessage());
                        return null;
                    });
                } else {
                }
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
                new DonationsInformation.DateTime(donationJson.get("createdAt").getAsString()).isOlder(startTime)) {
                if (donationsInformation.size() == 10 ) {
                    donationsInformation.removeFirst();
                }
                donationsInformation.add(new DonationsInformation(donationJson));

                String createdAt = donationJson.get("createdAt").getAsString();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime createdAtTime = LocalDateTime.parse(createdAt, formatter);

                LocalDateTime currentTime = LocalDateTime.now().plusHours(getConfig().getInt("settings.offsetFromDonatelloTime"));

                long delay = ChronoUnit.MILLIS.between(currentTime, createdAtTime.plusSeconds(20));
                if (delay < 0 || delay > 20000) {
                    delay = new Random().nextInt(19999);
                }


                Bukkit.getScheduler().runTaskLater(this, () -> {
                    CustomEventHandler.eventsCrossroads(donationJson.get("amount").getAsDouble());
                }, delay / 50);
            }
        }
    }

    public void saveJsonFile() {
        try (FileWriter writer = new FileWriter(jsonFile)) {
            JsonObject jsonObject = new JsonObject();
            JsonArray jsonArray = gson.toJsonTree(eventsArraysList).getAsJsonArray();

            jsonObject.add("eventsArrays", jsonArray);
            gson.toJson(jsonObject, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadJsonFile() {
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

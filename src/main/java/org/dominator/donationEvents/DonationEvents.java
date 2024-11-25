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
import org.dominator.donationEvents.workWithApi.APIManager;
import org.dominator.donationEvents.workWithApi.APIusageType.DonatelloAPI;
import org.dominator.donationEvents.workWithApi.APIusageType.DyakaAPI;
import org.dominator.donationEvents.workWithApi.APIusageType.MonobankAPI;
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
    public APIManager api = new APIManager();
    public DyakaAPI dyakaAPI;
    public DonatelloAPI donatelloAPI;
    public MonobankAPI monobankAPI;

    public CustomEventHandler customEventHandler;

    public List<DonationsInformation> donationsInformation = new ArrayList<>(10);
    public DonationsInformation.DateTime startTime;

    private File jsonFile;
    private Gson gson;
    public List<EventsArrays> eventsArraysList;


    public OpenSettingsMenu openSettingsMenu = new OpenSettingsMenu(this);

    @Override
    public void onEnable() {
        getLogger().info("DonationEvents плагін увімкнено!");

        startTime = new DonationsInformation.DateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        DonationEvents.this.getLogger().info("Час запуску серверу: " + startTime.toString());

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


        api.setAPIkey(getConfig().getString("settings.dyakaAPI"), 1);
        api.setAPIkey(getConfig().getString("settings.donatelloAPI"), 2);
        api.setAPIkey(getConfig().getString("settings.monoAPI"), 3);

        dyakaAPI = new DyakaAPI(this);
        donatelloAPI = new DonatelloAPI(this);
        monobankAPI = new MonobankAPI(this);

        customEventHandler = new CustomEventHandler(this);

        startDonationCheckTask();

        this.getCommand("setAPIkey").setExecutor(new SetApiKeyCommand(this));
        this.getCommand("lastDonations").setExecutor(new GetLastDonationsCommand(this));
        this.getCommand("openSettingsMenu").setExecutor(new OpenSettingsMenu(this));
        this.getCommand("randEventSum").setExecutor(new StartRandEvent(this));
        this.getCommand("reloadEvents").setExecutor(new ReLoadEvents(this));
        getServer().getPluginManager().registerEvents(new CustomMenu(this), this);
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
                    donationsInformation.removeFirst();
                }
                donationsInformation.add(new DonationsInformation(donationJson));
                DonationEvents.this.getLogger().info("Додано донат: Ім'я: `"+ donationsInformation.getLast().getName() + "` Сума: `" + donationsInformation.getLast().getAmount() + "` Повідовлення: `" + donationsInformation.getLast().getMessage() + "` Час створення: `" + donationsInformation.getFirst().getDateTimeString() + "`");

                String createdAt = donationJson.get("createdAt").getAsString();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime createdAtTime = LocalDateTime.parse(createdAt, formatter);
                LocalDateTime currentTime = LocalDateTime.now();

                // Вираховуємо різницю в мілісекундах між поточним часом і часом створення донату
                long delay = ChronoUnit.MILLIS.between(currentTime, createdAtTime.plusSeconds(20));
                this.getLogger().info("Затримка до обробки" + delay / 1000.0 + " с.");
                if (delay < 0) {
                    delay = 0;
                }
                if (delay > 20000){
                    delay = new Random().nextInt(19999);
                }
                this.getLogger().info("Затримка після обробки " + delay / 1000.0 + " с.");

                Bukkit.getScheduler().runTaskLater(this, () -> {
                    this.getLogger().info("Викликано івенти");
                    customEventHandler.eventsCrossroads(donationJson.get("amount").getAsDouble());
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

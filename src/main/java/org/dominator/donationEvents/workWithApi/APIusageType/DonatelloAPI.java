package org.dominator.donationEvents.workWithApi.APIusageType;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.dominator.donationEvents.DonationEvents;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DonatelloAPI {

    private final DonationEvents plugin;
    private String lastDonationId = "";

    private final List<JSONObject> recentDonations = new ArrayList<>();
    private static final int MAX_DONATIONS = 5;

    public DonatelloAPI(DonationEvents plugin) {
        this.plugin = plugin;
    }

    public void startDonationCheckTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                checkForNewDonations(); // Викликаємо метод перевірки донатів

                Player firstPlayer = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);

                if (firstPlayer != null) {
                    Location spawnLocation = firstPlayer.getLocation();
                    plugin.mobSpawner.startMobSpawnTask(EntityType.ZOMBIE, spawnLocation);
                } else {
                    plugin.getLogger().warning("На сервері немає жодного гравця для спавну моба.");
                }
            }
        }.runTaskTimer(plugin, 0L, 20L * 10L); //20L - tick. 10L - seconds
    }

    public void checkForNewDonations() {
        String token = plugin.api.getAPIkey(2);
        getJsonDonators(token).thenAccept(json -> {
            if (json != null) {
                JSONArray donations = new JSONObject(json).getJSONArray("content");
                if (donations.length() > 0) {
                    JSONObject lastDonation = donations.getJSONObject(0);
                    String donationId = lastDonation.getString("createdAt");

                    if (!donationId.equals(lastDonationId)) {
                        lastDonationId = donationId;  // Оновлюємо останній донат
                        String clientName = lastDonation.getString("clientName");
                        String amount = lastDonation.getString("amount");
                        String message = lastDonation.optString("message", "");

                        addDonation(lastDonation);

                        Bukkit.broadcastMessage(clientName + " " + amount + ". \n" + message);
                    }
                }
            }
        });
    }

    private void addDonation(JSONObject donation) {
        recentDonations.add(donation);

        if (recentDonations.size() > MAX_DONATIONS) {
            recentDonations.remove(0);
        }
    }

    public CompletableFuture<String> getJsonDonators(String token) {
        String baseUrl = "https://donatello.to/api/v1/donates";
        int page = 0;
        int size = 5;

        String url = String.format("%s?page=%d&size=%d", baseUrl, page, size);

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-Token", token)
                .header("User-Agent", "Mozilla/5.0")
                .header("Content-Type", "application/json")
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    plugin.getLogger().info("Останні донатори: " + response.body());

                    if (response.statusCode() == 401) {
                        plugin.getLogger().severe("Помилка авторизації. Неправильний токен.");
                        return null;
                    }

                    return response.body();
                })
                .handle((responseBody, throwable) -> {
                    if (throwable != null) {
                        plugin.getLogger().severe("Помилка при отриманні донатів: " + throwable.getMessage());
                    }
                    return responseBody;
                });
    }

    public String getJsonStringDonators(String token) {
        return getJsonDonators(token).join();
    }

    public List<JSONObject> getRecentDonations() {
        return new ArrayList<>(recentDonations);
    }
}

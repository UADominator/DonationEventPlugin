package org.dominator.donationEvents.workWithApi.APIusageType;

import org.bukkit.Bukkit;
import org.dominator.donationEvents.DonationEvents;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class DonatelloAPI {

    private final DonationEvents plugin;
    private String lastDonationId = "";  // Ідентифікатор останнього донату

    public DonatelloAPI(DonationEvents plugin) {
        this.plugin = plugin;
    }

    // Метод для перевірки та відправки повідомлення про новий донат
    public void checkForNewDonations() {
        String token = plugin.api.getAPIkey();
        getJsonDonators(token).thenAccept(json -> {
            if (json != null) {
                JSONArray donations = new JSONObject(json).getJSONArray("content");
                if (donations.length() > 0) {
                    JSONObject lastDonation = donations.getJSONObject(0);
                    String donationId = lastDonation.getString("pubId");

                    // Якщо є новий донат, надсилаємо повідомлення в чат
                    if (!donationId.equals(lastDonationId)) {
                        lastDonationId = donationId; // Оновлюємо останній отриманий донат
                        String clientName = lastDonation.getString("clientName");
                        String amount = lastDonation.getString("amount");
                        String message = lastDonation.optString("message", "");

                        // Відправляємо повідомлення в чат Minecraft
                        Bukkit.broadcastMessage("Прийшов донат від " + clientName + ": " + amount + " UAH. " + message);
                    }
                }
            }
        });
    }

    // Отримуємо JSON з донатами
    public CompletableFuture<String> getJsonDonators(String token) {
        String baseUrl = "https://donatello.to/api/v1/donates";
        int page = 0;
        int size = 5;

        String url = String.format("%s?page=%d&size=%d", baseUrl, page, size);

        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Minecraft-DonationEvents")
                .header("X-Token", token)
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .handle((responseBody, throwable) -> {
                    if (throwable != null) {
                        plugin.getLogger().severe("Помилка при отриманні донатів: " + throwable.getMessage());
                        return null;
                    }
                    return responseBody;
                });
    }
}

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

public class DyakaAPI {

    private final DonationEvents plugin;
    private String lastDonationId = "";

    public DyakaAPI(DonationEvents plugin) {
        this.plugin = plugin;
    }

    public void checkForNewDonations() {
        String token = plugin.api.getAPIkey(1);
        getJsonDonators(token).thenAccept(json -> {
            if (json != null) {
                JSONArray donations = new JSONObject(json).getJSONArray("donations");
                if (donations.length() > 0) {
                    JSONObject lastDonation = donations.getJSONObject(0);
                    String donationId = lastDonation.getString("id");
                    lastDonationId = donationId;
                    String clientName = lastDonation.getString("name");
                    String amount = lastDonation.getString("amount");
                    String message = lastDonation.optString("message", "");

                    Bukkit.broadcastMessage("Прийшов донат від " + clientName + ": " + amount + " UAH. " + message);
                }
            }
        });
    }

    // Отримуємо JSON з останніми донатами
    public CompletableFuture<String> getJsonDonators(String token) {
        String baseUrl = "https://dyaka.com/api/v1/message/stats";
        String action = "recent";
        int limit = 5;
        int test = 1;

        String url = String.format("%s?action=%s&conveyorHash=%s&params[limit]=%d&params[test]=%d",
                baseUrl, action, token, limit, test);

        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Mozilla/5.0")
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

    public  String getJsonStringDonators(String token){
       return  getJsonDonators(token).join();
    }
}

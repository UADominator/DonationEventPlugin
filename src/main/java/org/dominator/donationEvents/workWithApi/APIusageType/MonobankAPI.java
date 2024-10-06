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

public class MonobankAPI {

    private final DonationEvents plugin;
    private String lastTransactionId = "";

    public MonobankAPI(DonationEvents plugin) {
        this.plugin = plugin;
    }

    public void checkForNewDonations() {
        String token = plugin.api.getAPIkey(3);
        String account = "0";
        long from = System.currentTimeMillis() / 1000L - 3600;
        long to = System.currentTimeMillis() / 1000L;
        getJsonDonators(token, account, from, to).thenAccept(json -> {
            if (json != null) {
                JSONArray transactions = new JSONArray(json);
                if (transactions.length() > 0) {
                    JSONObject lastTransaction = transactions.getJSONObject(0);
                    String transactionId = lastTransaction.getString("id");
                    lastTransactionId = transactionId;
                    String description = lastTransaction.optString("description", "Без опису");
                    String amount = lastTransaction.getJSONObject("amount").getString("amount");
                    String currency = lastTransaction.getJSONObject("amount").getString("currency");
                    Bukkit.broadcastMessage("Прийшов донат: " + description + ": " + amount + " " + currency);
                }
            }
        });
    }

    public CompletableFuture<String> getJsonDonators(String token, String account, long from, long to) {
        String baseUrl = "https://api.monobank.ua/personal/statement";
        String url = String.format("%s/%s/%d/%d", baseUrl, account, from, to);

        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-Token", token)
                .header("User-Agent", "Mozilla/5.0")
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .handle((responseBody, throwable) -> {
                    if (throwable != null) {
                        plugin.getLogger().severe("Помилка при отриманні транзакцій: " + throwable.getMessage());
                        return null;
                    }
                    return responseBody;
                });
    }

    public CompletableFuture<String> getJsonDonators(String token){
        long from = System.currentTimeMillis() / 1000L - 3600;
        long to = System.currentTimeMillis() / 1000L;
        return getJsonDonators(token, "0", from, to);
    }

    public  String getJsonStringDonators(String token){
        return  getJsonDonators(token).join();
    }
}
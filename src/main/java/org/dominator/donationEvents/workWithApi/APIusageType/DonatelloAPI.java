package org.dominator.donationEvents.workWithApi.APIusageType;

import org.dominator.donationEvents.DonationEvents;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class DonatelloAPI {

    private final DonationEvents plugin;

    public DonatelloAPI(DonationEvents plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param token
     * @return json list of 10 last donators
     */

    public CompletableFuture<String> getJsonDonators(String token) {
        String baseUrl = "https://donatello.to/api/v1/donates";
        int size = 10;

        String url = String.format("%s?page=%d&size=%d", baseUrl, 0, size);

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
                    if (response.statusCode() == 401) {
                        plugin.getLogger().severe("Помилка авторизації. Неправильний токен.");
                        return null;
                    }

                    plugin.getLogger().info("Донати успішно отримані");
                    return response.body();
                })
                .handle((responseBody, throwable) -> {
                    if (throwable != null) {
                        plugin.getLogger().severe("Помилка при отриманні донатів: " + throwable.getMessage());
                    }
                    return responseBody;
                });
    }

}

package org.dominator.donationEvents.workWithApi.APIusageType;


import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.dominator.donationEvents.DonationEvents;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class DyakaAPI {

    private final DonationEvents plugin;

    public DyakaAPI(DonationEvents plugin) {
        this.plugin = plugin;
    }

    public void getRecentDonations(CommandSender sender) {
            sender.sendMessage("Останні донати: " + getJsonDonators());
    }

    public CompletableFuture<String> getJsonDonators (){
        String baseUrl = "https://dyaka.com/api/v1/message/stats";
        String action = "recent";
        String conveyorHash = plugin.api.getAPIkey(); // Заміни на свій конвеєр
        int limit = 5; // Кількість подяк
        int test = 1;  // 1 - сплачені подяки

        // Формування URL з параметрами
        String url = String.format("%s?action=%s&conveyorHash=%s&params[limit]=%d&params[test]=%d",
                baseUrl, action, conveyorHash, limit, test);

        // Створення HttpClient з підтримкою перенаправлень
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)  // Дозволяє слідувати перенаправленням
                .build();

        // Створення запиту
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Minecraft-DonationEvents") // Додавання User-Agent
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body) // Отримання тіла відповіді
                .handle((responseBody, throwable) -> {
                    if (throwable != null) {
                        // Обробка помилки
                        plugin.getLogger().severe("Помилка при отриманні донатів: " + throwable.getMessage());
                        return null; // Або можна повертати повідомлення про помилку
                    }
                    return responseBody; // Повертаємо отримане тіло відповіді
                });
    }
}


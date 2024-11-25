package org.dominator.donationEvents.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.dominator.donationEvents.DonationEvents;
import org.dominator.donationEvents.events.EventsArrays;

public class CustomMenu implements Listener {
    private Inventory inventory;
    private DonationEvents plugin;
    private EventsArrays editingEvent;

    public CustomMenu(DonationEvents plugin) {
        this.plugin = plugin;
        initializeMenu();
    }

    private void initializeMenu() {
        inventory = null;
        this.inventory = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "Редагування івентів");
        int count = 1;
        for (EventsArrays event : plugin.eventsArraysList){
            ItemStack item = createItem(event, count);
            inventory.setItem(count - 1, item);
            count++;
        }
    }


    private ItemStack createItem(EventsArrays event, int count) {
        String name = event.events.eventName;
        String materialName = event.events.icon;

        Material material = Material.matchMaterial(materialName);

        if (material == null) {
            material = Material.BARRIER;
        }

        ItemStack item = new ItemStack(material, count);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {

            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }

        return item;
    }



    public Inventory getInventory() {
        return inventory;
    }

    public static Material getMaterial(String name) {
        Material material = Material.matchMaterial(name);
        if (material != null) return material;
        return Material.BARRIER;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.DARK_PURPLE + "Редагування івентів")) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.hasItemMeta()) {
                String itemName = clickedItem.getItemMeta().getDisplayName();

                for (EventsArrays eventArray : plugin.eventsArraysList) {
                    if (eventArray.events.eventName.equals(itemName)) {
                        editingEvent = eventArray;
                        break;
                    }
                }

                if (editingEvent != null) {
                    openEditMenu((Player) event.getWhoClicked());
                }
            }
        }
    }

    private void openEditMenu(Player player) {
        Inventory editMenu = Bukkit.createInventory(null, 9, ChatColor.DARK_PURPLE + "Редагування: " + editingEvent.events.eventName);

        //ТУТ ТІПА ЗМІНА МАКСИМАЛЬНОЇ ЦІНИ
        ItemStack editButtonMax = new ItemStack(Material.LIME_TERRACOTTA);
        ItemMeta metaMax = editButtonMax.getItemMeta();
        metaMax.setDisplayName(ChatColor.LIGHT_PURPLE + "Максимальна ціна: " + editingEvent.events.price.getEndSum());
        editButtonMax.setItemMeta(metaMax);
        editMenu.setItem(6, editButtonMax);

        //ТУТ ТІПА ЗМІНА МІНІМАЛЬНОЇ ЦІНИ
        ItemStack editButtonMin = new ItemStack(Material.RED_TERRACOTTA);
        ItemMeta metaMin = editButtonMin.getItemMeta();
        metaMin.setDisplayName(ChatColor.LIGHT_PURPLE + "Мінімальна ціна: " + editingEvent.events.price.getStartSum());
        editButtonMin.setItemMeta(metaMin);
        editMenu.setItem(2, editButtonMin);

        //ТУТ ТІПА ВИМКНЕННЯ ІВЕНТУ
        ItemStack iventState;
        if (editingEvent.events.price.getStartSum() == editingEvent.events.price.getEndSum() && editingEvent.events.price.getStartSum() == 0f){
            iventState = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta metaIventState = iventState.getItemMeta();
            metaIventState.setDisplayName(ChatColor.LIGHT_PURPLE + "Вимкнути / Івент вже вимкнено");
            iventState.setItemMeta(metaIventState);
        } else {
            iventState = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta metaIventState = iventState.getItemMeta();
            metaIventState.setDisplayName(ChatColor.LIGHT_PURPLE + "Вимкнути / Івент ввімкнено");
            iventState.setItemMeta(metaIventState);
        }
        editMenu.setItem(4, iventState);


        //ТУТ ТІПА ПОВЕРНУТИСЯ В ГОЛОВНЕ МЕНЮ
        ItemStack back = new ItemStack(Material.CHEST);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Повернутися до івентів");
        back.setItemMeta(backMeta);
        editMenu.setItem(0, back);

        initializeMenu();
        player.openInventory(editMenu);
    }

    @EventHandler
    public void onEditMenuClick(InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith(ChatColor.DARK_PURPLE + "Редагування: ")) {
            event.setCancelled(true);

            //ТУТ ТІПА ПОВЕРНЕННЯ В ГОЛОВНЕ МЕНЮ
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.CHEST){
                ((Player) event.getWhoClicked()).openInventory(inventory);
            }

            //ТУТ ТІПА ВИМКНЕННЯ ІВЕНТУ
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.LIME_STAINED_GLASS_PANE){
                editingEvent.events.price.changeMinSum(0);
                editingEvent.events.price.changeMaxSum(0);
                plugin.saveJsonFile();
                plugin.loadJsonFile();
                openEditMenu((Player) event.getWhoClicked());
            }

            //ТУТ ТІПА ЗМІНА МАКСИМАЛЬНОЇ ЦІНИ
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.LIME_TERRACOTTA) {
                Player player = (Player) event.getWhoClicked();

                player.sendMessage(ChatColor.YELLOW + "Введіть максимальну ціну:");
                player.closeInventory();

                plugin.getServer().getPluginManager().registerEvents(new Listener() {
                    @EventHandler
                    public void onChatMessage(AsyncPlayerChatEvent chatEvent) {
                        if (chatEvent.getPlayer().equals(player)) {
                            chatEvent.setCancelled(true);

                            try {
                                double enteredValue = Double.parseDouble(chatEvent.getMessage());
                                boolean success = editingEvent.events.price.changeMaxSum(enteredValue);

                                if (success) {
                                    player.sendMessage(ChatColor.GREEN + "Максимальну ціну змінено на: " + enteredValue);
                                } else {
                                    player.sendMessage(ChatColor.RED + "Введене значення не є дійсним.");
                                }

                            } catch (NumberFormatException e) {
                                player.sendMessage(ChatColor.RED + "Будь ласка, введіть дійсне число.");
                            }

                            plugin.saveJsonFile();
                            plugin.loadJsonFile();

                            Bukkit.getScheduler().runTask(plugin, () -> openEditMenu(player));

                            HandlerList.unregisterAll(this);
                        }
                    }
                }, plugin);
            }

            //ТУТ ТІПА ЗМІНА МІНІМАЛЬНОЇ ЦІНИ
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.RED_TERRACOTTA) {
                Player player = (Player) event.getWhoClicked();

                player.sendMessage(ChatColor.YELLOW + "Введіть мінімальну ціну:");
                player.closeInventory();

                plugin.getServer().getPluginManager().registerEvents(new Listener() {
                    @EventHandler
                    public void onChatMessage(AsyncPlayerChatEvent chatEvent) {
                        if (chatEvent.getPlayer().equals(player)) {
                            chatEvent.setCancelled(true);

                            try {
                                double enteredValue = Double.parseDouble(chatEvent.getMessage());

                                boolean success = editingEvent.events.price.changeMinSum(enteredValue);

                                if (success) {
                                    player.sendMessage(ChatColor.GREEN + "Мінімальну ціну змінено на: " + enteredValue);
                                } else {
                                    player.sendMessage(ChatColor.RED + "Введене значення не є дійсним.");
                                }

                            } catch (NumberFormatException e) {
                                player.sendMessage(ChatColor.RED + "Будь ласка, введіть дійсне число.");
                            }

                            plugin.saveJsonFile();
                            plugin.loadJsonFile();

                            Bukkit.getScheduler().runTask(plugin, () -> openEditMenu(player));

                            HandlerList.unregisterAll(this);
                        }
                    }
                }, plugin);
            }
        }
    }

    //TODO: move to other class
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.api.donatelloAPIkey.equals("change me") || plugin.api.donatelloAPIkey.equals("")){
            event.getPlayer().sendMessage("§l§8Встанови АПІ ключ §3Donatello §4/setAPIKey §ствійБлятьAPI§4 2§r");
            event.getPlayer().sendMessage("§7Або якщо ти мамкін §l§8хацкер§7 заміни його в конфігу: §9../plugins/DonationEvents/config.yml§7 заміни §4change me§7 на свій АПІ ключ");
        }
    }
}

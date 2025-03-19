package org.dominator.donationEvents.menu;

import org.bukkit.Bukkit;
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
import org.dominator.donationEvents.useAPI.APIManager;

import static org.bukkit.ChatColor.*;

public class CustomMenu implements Listener {
    private static Inventory inventory;
    private static EventsArrays editingEvent;

    public CustomMenu() {
        initializeMenu();
    }

    private static void initializeMenu() {
        inventory = null;
        inventory = Bukkit.createInventory(null, 54, DARK_PURPLE + "Редагування івентів");
        int count = 1;
        for (EventsArrays event : DonationEvents.eventsArraysList){
            ItemStack item = createItem(event, count);
            inventory.setItem(count - 1, item);
            count++;
        }

        ItemStack iventsState;
        if (DonationEvents.acceptEvents){
            iventsState = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta metaIventsState = iventsState.getItemMeta();
            metaIventsState.setDisplayName(GREEN + "Вимкнути / Івенти ввімкнено");
            iventsState.setItemMeta(metaIventsState);
        } else {
            iventsState = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta metaIventsState = iventsState.getItemMeta();
            metaIventsState.setDisplayName(DARK_RED + "Ввімкнути / Івенти вимкнено");
            iventsState.setItemMeta(metaIventsState);
        }
        inventory.setItem(49, new ItemStack(iventsState));
    }


    private static ItemStack createItem(EventsArrays event, int count) {
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



    public static Inventory getInventory() {
        return inventory;
    }

    public static Material getMaterial(String name) {
        Material material = Material.matchMaterial(name);
        if (material != null) return material;
        return Material.BARRIER;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(DARK_PURPLE + "Редагування івентів")) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.hasItemMeta()) {
                String itemName = clickedItem.getItemMeta().getDisplayName();
                if (itemName.equals(DARK_RED + "Ввімкнути / Івенти вимкнено") || itemName.equals(GREEN + "Вимкнути / Івенти ввімкнено")){
                    DonationEvents.acceptEvents = !DonationEvents.acceptEvents;
                    initializeMenu();
                    event.getWhoClicked().openInventory(inventory);
                    DonationEvents.instance.updateStartTime();
                    return;
                }

                for (EventsArrays eventArray : DonationEvents.eventsArraysList) {
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

    private static void openEditMenu(Player player) {
        Inventory editMenu = Bukkit.createInventory(null, 9, DARK_PURPLE + "Редагування: " + editingEvent.events.eventName);

        //ТУТ ТІПА ЗМІНА МАКСИМАЛЬНОЇ ЦІНИ
        ItemStack editButtonMax = new ItemStack(Material.LIME_TERRACOTTA);
        ItemMeta metaMax = editButtonMax.getItemMeta();
        metaMax.setDisplayName(LIGHT_PURPLE + "Максимальна ціна: " + editingEvent.events.price.getEndSum());
        editButtonMax.setItemMeta(metaMax);
        editMenu.setItem(6, editButtonMax);

        //ТУТ ТІПА ЗМІНА МІНІМАЛЬНОЇ ЦІНИ
        ItemStack editButtonMin = new ItemStack(Material.RED_TERRACOTTA);
        ItemMeta metaMin = editButtonMin.getItemMeta();
        metaMin.setDisplayName(LIGHT_PURPLE + "Мінімальна ціна: " + editingEvent.events.price.getStartSum());
        editButtonMin.setItemMeta(metaMin);
        editMenu.setItem(2, editButtonMin);

        //ТУТ ТІПА ВИМКНЕННЯ ІВЕНТУ
        ItemStack iventState;
        if (editingEvent.events.price.getStartSum() == editingEvent.events.price.getEndSum() && editingEvent.events.price.getStartSum() == 0f){
            iventState = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta metaIventState = iventState.getItemMeta();
            metaIventState.setDisplayName(LIGHT_PURPLE + "Вимкнути / Івент вже вимкнено");
            iventState.setItemMeta(metaIventState);
        } else {
            iventState = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta metaIventState = iventState.getItemMeta();
            metaIventState.setDisplayName(LIGHT_PURPLE + "Вимкнути / Івент ввімкнено");
            iventState.setItemMeta(metaIventState);
        }
        editMenu.setItem(4, iventState);


        //ТУТ ТІПА ПОВЕРНУТИСЯ В ГОЛОВНЕ МЕНЮ
        ItemStack back = new ItemStack(Material.CHEST);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(LIGHT_PURPLE + "Повернутися до івентів");
        back.setItemMeta(backMeta);
        editMenu.setItem(0, back);

        initializeMenu();
        player.openInventory(editMenu);
    }

    @EventHandler
    public void onEditMenuClick(InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith(DARK_PURPLE + "Редагування: ")) {
            event.setCancelled(true);

            //ТУТ ТІПА ПОВЕРНЕННЯ В ГОЛОВНЕ МЕНЮ
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.CHEST){
                ((Player) event.getWhoClicked()).openInventory(inventory);
            }

            //ТУТ ТІПА ВИМКНЕННЯ ІВЕНТУ
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.LIME_STAINED_GLASS_PANE){
                editingEvent.events.price.changeMinSum(0);
                editingEvent.events.price.changeMaxSum(0);
                DonationEvents.instance.saveJsonFile();
                DonationEvents.instance.loadJsonFile();
                openEditMenu((Player) event.getWhoClicked());
            }

            //ТУТ ТІПА ЗМІНА МАКСИМАЛЬНОЇ ЦІНИ
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.LIME_TERRACOTTA) {
                Player player = (Player) event.getWhoClicked();

                player.sendMessage(YELLOW + "Введіть максимальну ціну:");
                player.closeInventory();

                DonationEvents.instance.getServer().getPluginManager().registerEvents(new Listener() {
                    @EventHandler
                    public void onChatMessage(AsyncPlayerChatEvent chatEvent) {
                        if (chatEvent.getPlayer().equals(player)) {
                            chatEvent.setCancelled(true);

                            try {
                                double enteredValue = Double.parseDouble(chatEvent.getMessage());
                                boolean success = editingEvent.events.price.changeMaxSum(enteredValue);

                                if (success) {
                                    player.sendMessage(GREEN + "Максимальну ціну змінено на: " + enteredValue);
                                } else {
                                    player.sendMessage(RED + "Введене значення не є дійсним.");
                                }

                            } catch (NumberFormatException e) {
                                player.sendMessage(RED + "Будь ласка, введіть дійсне число.");
                            }

                            DonationEvents.instance.saveJsonFile();
                            DonationEvents.instance.loadJsonFile();

                            Bukkit.getScheduler().runTask(DonationEvents.instance, () -> openEditMenu(player));

                            HandlerList.unregisterAll(this);
                        }
                    }
                }, DonationEvents.instance);
            }

            //ТУТ ТІПА ЗМІНА МІНІМАЛЬНОЇ ЦІНИ
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.RED_TERRACOTTA) {
                Player player = (Player) event.getWhoClicked();

                player.sendMessage(YELLOW + "Введіть мінімальну ціну:");
                player.closeInventory();

                DonationEvents.instance.getServer().getPluginManager().registerEvents(new Listener() {
                    @EventHandler
                    public void onChatMessage(AsyncPlayerChatEvent chatEvent) {
                        if (chatEvent.getPlayer().equals(player)) {
                            chatEvent.setCancelled(true);

                            try {
                                double enteredValue = Double.parseDouble(chatEvent.getMessage());

                                boolean success = editingEvent.events.price.changeMinSum(enteredValue);

                                if (success) {
                                    player.sendMessage(GREEN + "Мінімальну ціну змінено на: " + enteredValue);
                                } else {
                                    player.sendMessage(RED + "Введене значення не є дійсним.");
                                }

                            } catch (NumberFormatException e) {
                                player.sendMessage(RED + "Будь ласка, введіть дійсне число.");
                            }

                            DonationEvents.instance.saveJsonFile();
                            DonationEvents.instance.loadJsonFile();

                            Bukkit.getScheduler().runTask(DonationEvents.instance, () -> openEditMenu(player));

                            HandlerList.unregisterAll(this);
                        }
                    }
                }, DonationEvents.instance);
            }
        }
    }

    //TODO: move to other class
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (APIManager.getAPIkey(1).equals("change me") || APIManager.getAPIkey(1).isEmpty()){
            event.getPlayer().sendMessage("§l§8Встанови АПІ ключ §3Donatello §4/setAPIKey §ствійAPI§4 2§r");
            event.getPlayer().sendMessage("§7Або якщо ти мамкін §l§8хацкер§7 заміни його в конфігу: §9../plugins/DonationEvents/config.yml§7 заміни §4change me§7 на свій АПІ ключ");
        }
    }
}

package me.dannusnl.dungeonmanager.guihandlers;

import me.dannusnl.dungeonmanager.DataStore;
import me.dannusnl.dungeonmanager.MainClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class SetMusique implements Listener {

    @EventHandler
    public void dungeonPlayers(InventoryClickEvent e) {

        DataStore dataStore = MainClass.getMainClass().getDataStore();

        if (e.getView().getTitle().startsWith(ChatColor.DARK_AQUA + "Pour quel donjon?")) {
            if (e.getClickedInventory() == null) return;
            if (e.getClickedInventory().equals(e.getView().getBottomInventory())) return;
            if (e.getCurrentItem() == null) return;

            e.setCancelled(true);

            if (e.getCurrentItem().getType().equals(Material.BARRIER)) {

                e.getWhoClicked().closeInventory();

            } else if (e.getCurrentItem().getType().equals(Material.ARROW)) {

                ItemStack filling = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
                ItemMeta fillingMeta = filling.getItemMeta();
                fillingMeta.setDisplayName(" ");
                filling.setItemMeta(fillingMeta);

                ItemStack close = new ItemStack(Material.BARRIER);
                ItemMeta closeMeta = filling.getItemMeta();
                closeMeta.setDisplayName(ChatColor.RED + "Fermer");
                close.setItemMeta(closeMeta);

                ItemStack next = new ItemStack(Material.ARROW);
                ItemMeta nextMeta = next.getItemMeta();
                nextMeta.setDisplayName(ChatColor.AQUA + "Page suivante");
                next.setItemMeta(nextMeta);

                ItemStack previous = new ItemStack(Material.ARROW);
                ItemMeta previousMeta = previous.getItemMeta();
                previousMeta.setDisplayName(ChatColor.AQUA + "Page précédent");
                previous.setItemMeta(previousMeta);

                int currentPage = Integer.parseInt(ChatColor.stripColor(e.getView().getTitle().replace("Pour quel donjon? - Page ", "")));
                int newPage = currentPage + 1;
                if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Page précédent")) newPage = currentPage - 1;

                int itemCount = 0;
                List<ItemStack> items = new ArrayList<>();
                List<String> dungeons = dataStore.getDungeons();

                for (int i = (newPage - 1) * 36; i < dungeons.size(); i++) {
                    ItemStack item = new ItemStack(Material.CHISELED_STONE_BRICKS);
                    ItemMeta itemMeta = item.getItemMeta();
                    itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', dungeons.get(i)));
                    List<String> itemLore = new ArrayList<>();
                    itemLore.add(" ");
                    if (dataStore.getMusic(dungeons.get(i)) == null) itemLore.add(ChatColor.RED + "Not set.");
                    else itemLore.add(ChatColor.DARK_AQUA + dataStore.getMusic(dungeons.get(i)).getType().name().toLowerCase().replace('_', ' '));
                    itemMeta.setLore(itemLore);
                    item.setItemMeta(itemMeta);
                    items.add(item);
                    itemCount++;
                }

                Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_AQUA + "Pour quel donjon? - Page " + newPage);
                if (itemCount <= 9) inv = Bukkit.createInventory(null, 27, ChatColor.DARK_AQUA + "Pour quel donjon? - Page " + newPage);
                else if (itemCount <= 18) inv = Bukkit.createInventory(null, 36, ChatColor.DARK_AQUA + "Pour quel donjon? - Page " + newPage);
                else if (itemCount <= 27) inv = Bukkit.createInventory(null, 45, ChatColor.DARK_AQUA + "Pour quel donjon? - Page " + newPage);

                for (ItemStack item : items) {
                    inv.addItem(item);
                }

                for (int i = (inv.getSize() - 18); i < inv.getSize(); i++) {
                    inv.setItem(i, filling);
                }

                inv.setItem(inv.getSize() - 5, close);
                if (itemCount > 36) inv.setItem(inv.getSize() - 4, next);
                if (newPage > 1) inv.setItem(inv.getSize() - 6, previous);

                Inventory inv0 = inv;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        e.getWhoClicked().openInventory(inv0);
                    }
                }.runTaskLater(MainClass.getPlugin(MainClass.class), 1);

            } else if (e.getCurrentItem().getType().equals(Material.CHISELED_STONE_BRICKS)) {

                dataStore.setMusic(e.getWhoClicked().getEquipment().getItemInMainHand(), e.getCurrentItem().getItemMeta().getDisplayName().replace('§', '&'));
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().sendMessage(ChatColor.GREEN + "La musique a été ajoutée avec succès!");

            }
        }
    }

}

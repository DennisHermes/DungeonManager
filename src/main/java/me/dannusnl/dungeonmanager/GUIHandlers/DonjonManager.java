package me.dannusnl.dungeonmanager.GUIHandlers;

import me.dannusnl.dungeonmanager.DataManager;
import me.dannusnl.dungeonmanager.DungeonManager;
import me.dannusnl.dungeonmanager.MainClass;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class DonjonManager implements Listener {

    @EventHandler
    public void dungeonPlayers(InventoryClickEvent e) {
        if (e.getSlot() > e.getView().getTopInventory().getSize()) return;
        if (e.getCurrentItem() == null) return;

        if (e.getView().getTitle().startsWith(ChatColor.DARK_AQUA + "DonjonManager: Donjons - Page ")) {
            e.setCancelled(true);

            if (e.getCurrentItem().getType().equals(Material.BARRIER)) {

                e.getWhoClicked().closeInventory();

            } else if (e.getCurrentItem().getType().equals(Material.GREEN_TERRACOTTA) || e.getCurrentItem().getType().equals(Material.RED_TERRACOTTA)) {

                int players = 1;
                if (e.getCurrentItem().getItemMeta().getDisplayName().contains("2")) players = 2;
                else if (e.getCurrentItem().getItemMeta().getDisplayName().contains("3")) players = 3;
                else if (e.getCurrentItem().getItemMeta().getDisplayName().contains("4")) players = 4;

                String mapName = e.getInventory().getItem(e.getSlot() - (players + 1)).getItemMeta().getDisplayName().replace('§', '&');
                int currentPage = Integer.parseInt(ChatColor.stripColor(e.getView().getTitle().replace("DonjonManager: Donjons - Page ", "")));

                if (e.getCurrentItem().getType().equals(Material.GREEN_TERRACOTTA))
                    DataManager.removePlayableWith(mapName, players + "");
                else
                    DataManager.addPlayableWith(mapName, players + "");

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        DungeonManager.setplayeramount((Player) e.getWhoClicked(), currentPage);
                    }
                }.runTaskLater(MainClass.getPlugin(MainClass.class), 1);

            } else if (e.getCurrentItem().getType().equals(Material.ARROW)) {

                int currentPage = Integer.parseInt(ChatColor.stripColor(e.getView().getTitle().replace("DonjonManager: Donjons - Page ", "")));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Page suivante"))
                            DungeonManager.setplayeramount((Player) e.getWhoClicked(), currentPage + 1);
                        else if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Page précédent"))
                            DungeonManager.setplayeramount((Player) e.getWhoClicked(), currentPage - 1);
                    }
                }.runTaskLater(MainClass.getPlugin(MainClass.class), 1);

            }
        }
    }

}

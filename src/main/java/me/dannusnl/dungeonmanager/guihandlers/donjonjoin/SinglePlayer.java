package me.dannusnl.dungeonmanager.guihandlers.donjonjoin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.dannusnl.dungeonmanager.DataStore;
import me.dannusnl.dungeonmanager.DungeonTeleporter;
import me.dannusnl.dungeonmanager.MainClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class SinglePlayer implements Listener {

	@EventHandler
	public void dungeonPlayers(InventoryClickEvent e) {

		DataStore dataStore = MainClass.getMainClass().getDataStore();

		if (e.getView().getTitle().equals(ChatColor.DARK_AQUA + "Jeu solo ou multijoueur?")) {
			if (e.getClickedInventory() == null) return;
			if (e.getClickedInventory().equals(e.getView().getBottomInventory())) return;
			if (e.getCurrentItem() == null) return;

			e.setCancelled(true);
			
			if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
				
				e.getWhoClicked().closeInventory();
				
			} else if (e.getCurrentItem().getType().equals(Material.POPPY)) {
				
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
				
				int itemCount = 0;
				List<ItemStack> items = new ArrayList<>();
				List<String> dungeons = dataStore.getDungeons();

				for (String dungeon : dungeons) {
					if (dataStore.isPlayableWith(dungeon, 1 + "")) {
						ItemStack item = new ItemStack(Material.CHISELED_STONE_BRICKS);
						ItemMeta itemMeta = item.getItemMeta();
						itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', dungeon));
						item.setItemMeta(itemMeta);
						items.add(item);
						itemCount++;
					}
				}
				
				Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_AQUA + "Donjons - Page 1");
				if (itemCount <= 9) inv = Bukkit.createInventory(null, 27, ChatColor.DARK_AQUA + "Donjons - Page 1");
				else if (itemCount <= 18) inv = Bukkit.createInventory(null, 36, ChatColor.DARK_AQUA + "Donjons - Page 1");
				else if (itemCount <= 27) inv = Bukkit.createInventory(null, 45, ChatColor.DARK_AQUA + "Donjons - Page 1");

				for (ItemStack item : items) {
					inv.addItem(item);
				}
				
				for (int i = (inv.getSize() - 18); i < inv.getSize(); i++) {
					inv.setItem(i, filling);
				}
				
				inv.setItem(inv.getSize() - 5, close);
				if (itemCount > 36) inv.setItem(inv.getSize() - 4, next);

				openDelayedInv(e.getWhoClicked(), inv);

			} else if (e.getCurrentItem().getType().equals(Material.ROSE_BUSH)) {
				Inventory inv = Bukkit.createInventory(null, 27, ChatColor.AQUA + "Rejoindre ou créer un jeu");

				ItemStack filling = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
				ItemMeta fillingMeta = filling.getItemMeta();
				fillingMeta.setDisplayName(" ");
				filling.setItemMeta(fillingMeta);

				ItemStack newGame = new ItemStack(Material.ARROW);
				ItemMeta newGameMeta = newGame.getItemMeta();
				newGameMeta.setDisplayName(ChatColor.AQUA + "Créer un nouveau jeu");
				newGame.setItemMeta(newGameMeta);

				ItemStack joinGame = new ItemStack(Material.TIPPED_ARROW);
				ItemMeta joinGameMeta = joinGame.getItemMeta();
				joinGameMeta.setDisplayName(ChatColor.AQUA + "Rejoindre une jeu existant");
				joinGame.setItemMeta(joinGameMeta);

				ItemStack close = new ItemStack(Material.BARRIER);
				ItemMeta closeMeta = close.getItemMeta();
				closeMeta.setDisplayName(ChatColor.RED + "Fermer");
				close.setItemMeta(closeMeta);

				for (int i = 0; i < inv.getSize(); i++) {
					inv.setItem(i, filling);
				}

				inv.setItem(11, newGame);
				inv.setItem(15, joinGame);
				inv.setItem(22, close);

				openDelayedInv(e.getWhoClicked(), inv);
			}
			
		}



		else if (e.getView().getTitle().startsWith(ChatColor.DARK_AQUA + "Donjons - Page ")) {
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
				
				int currentPage = Integer.parseInt(ChatColor.stripColor(e.getView().getTitle().replace("Donjons - Page ", "")));
				int newPage = currentPage + 1;
				if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Page précédent")) newPage = currentPage - 1;
				
				int itemCount = 0;
				List<ItemStack> items = new ArrayList<>();
				List<String> dungeons = dataStore.getDungeons();
				
				for (int i = (newPage - 1) * 36; i < dungeons.size(); i++) {
					if (dataStore.isPlayableWith(dungeons.get(i), e.getCurrentItem().getAmount() + "")) {
						ItemStack item = new ItemStack(Material.CHISELED_STONE_BRICKS);
						ItemMeta itemMeta = item.getItemMeta();
						itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', dungeons.get(i)));
						item.setItemMeta(itemMeta);
						items.add(item);
						itemCount++;
					}
				}
				
				Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_AQUA + "Donjons - Page " + newPage);
				if (itemCount <= 9) inv = Bukkit.createInventory(null, 27, ChatColor.DARK_AQUA + "Donjons - Page " + newPage);
				else if (itemCount <= 18) inv = Bukkit.createInventory(null, 36, ChatColor.DARK_AQUA + "Donjons - Page " + newPage);
				else if (itemCount <= 27) inv = Bukkit.createInventory(null, 45, ChatColor.DARK_AQUA + "Donjons - Page " + newPage);

				for (ItemStack item : items) {
					inv.addItem(item);
				}
				
				for (int i = (inv.getSize() - 18); i < inv.getSize(); i++) {
					inv.setItem(i, filling);
				}
				
				inv.setItem(inv.getSize() - 5, close);
				if (itemCount > 36) inv.setItem(inv.getSize() - 4, next);
				if (newPage > 1) inv.setItem(inv.getSize() - 6, previous);

				openDelayedInv(e.getWhoClicked(), inv);
				
			} else if (e.getCurrentItem().getType().equals(Material.CHISELED_STONE_BRICKS)) {

				e.getWhoClicked().closeInventory();
				e.getWhoClicked().teleport(dataStore.getWaitRoomLoc());
				e.getWhoClicked().sendMessage(ChatColor.AQUA + "Le monde est en train de se charger, veuillez patienter...");

				String game = dataStore.getGameOfPlayer((Player) e.getWhoClicked());
				if (game != null) dataStore.leaveGame((Player) e.getWhoClicked(), game);
				DungeonTeleporter.requestedWorlds.remove(e.getWhoClicked());

				String worldName = e.getCurrentItem().getItemMeta().getDisplayName().replace('§', '&');
				File worldFile = new File(MainClass.getPlugin(MainClass.class).getDataFolder() + "/" + worldName);

				DungeonTeleporter.requestedWorlds.put((Player) e.getWhoClicked(), dataStore.loadMap(worldFile));
				DungeonTeleporter.requestedDonjon.put((Player) e.getWhoClicked(), worldName);

				e.getWhoClicked().sendMessage(ChatColor.GREEN + "Le donjon a été chargé avec succès! Placez-vous dans la case indiquée pour commencer.");
			}
		}
	}

	private void openDelayedInv(HumanEntity p, Inventory inv) {
		new BukkitRunnable() {
			@Override
			public void run() {
				p.openInventory(inv);
			}
		}.runTaskLater(MainClass.getPlugin(MainClass.class), 1);
	}

}

	package me.dannusnl.dungeonmanager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DungeonManager {

	public DungeonManager() {

	}

	public void setplayeramount(Player p, int page) {

		DataStore dataStore = MainClass.getMainClass().getDataStore();

		Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_AQUA + "DonjonManager: Donjons - Page " + page);
		
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
		
		ItemStack info = new ItemStack(Material.PAPER);
		ItemMeta infoMeta = info.getItemMeta();
		infoMeta.setDisplayName(ChatColor.AQUA + "Sélectionnez le nombre de joueurs avec lesquels vous souhaitez jouer à ce donjon.");
		info.setItemMeta(infoMeta);
		
		ItemStack players = new ItemStack(Material.TOTEM_OF_UNDYING);
		ItemMeta playersMeta = players.getItemMeta();
		playersMeta.setDisplayName(ChatColor.AQUA + "1 joueur");
		players.setItemMeta(playersMeta);
		
		ItemStack trueItem = new ItemStack(Material.GREEN_TERRACOTTA);
		ItemMeta trueItemMeta = trueItem.getItemMeta();
		trueItemMeta.setDisplayName(ChatColor.AQUA + "Le donjon est jouable à 1 joueur: " + ChatColor.GREEN + "vrai");
		ArrayList<String> trueItemLore = new ArrayList<>();
		trueItemLore.add("");
		trueItemLore.add(ChatColor.BLUE + "Cliquez pour basculer.");
		trueItemMeta.setLore(trueItemLore);
		trueItem.setItemMeta(trueItemMeta);
		
		ItemStack falseItem = new ItemStack(Material.RED_TERRACOTTA);
		ItemMeta falseItemMeta = falseItem.getItemMeta();
		falseItemMeta.setDisplayName(ChatColor.AQUA + "Le donjon est jouable à 1 joueur: " + ChatColor.RED + "Faux");
		ArrayList<String> falseItemLore = new ArrayList<>();
		falseItemLore.add("");
		falseItemLore.add(ChatColor.BLUE + "Cliquez pour basculer.");
		falseItemMeta.setLore(falseItemLore);
		falseItem.setItemMeta(falseItemMeta);
		
		inv.setItem(1, info);
		for (int i = 2; i < 6; i++) {
			inv.setItem(i + 1, players);
			playersMeta.setDisplayName(ChatColor.AQUA + "" + i + " Joueurs");
			players.setItemMeta(playersMeta);
			players.setAmount(i);
		}
		for (int i = 0; i < 46; i = i + 9) {
			inv.setItem(i, filling);
		}
		for (int i = 2; i < 48; i = i + 9) {
			inv.setItem(i, filling);
		}
		for (int i = 7; i < 53; i = i + 9) {
			inv.setItem(i, filling);
		}
		for (int i = 8; i < 54; i = i + 9) {
			inv.setItem(i, filling);
		}
		
		List<String> dungeons = dataStore.getDungeons();
		
		if (dungeons.size() >=  (4 * (page - 1)) + 1) {
			for (int i = 12; i < 16; i++) {
				int playerCount = (i - 11);
				
				trueItemMeta.setDisplayName(ChatColor.AQUA + "Le donjon est jouable à " + playerCount + " joueurs: " + ChatColor.GREEN + "vrai");
				falseItemMeta.setDisplayName(ChatColor.AQUA + "Le donjon est jouable à " + playerCount + " joueurs: " + ChatColor.RED + "Faux");
				trueItem.setItemMeta(trueItemMeta);
				falseItem.setItemMeta(falseItemMeta);
				
				if (dataStore.isPlayableWith(dungeons.get((4 * (page - 1))), playerCount + "")) inv.setItem(i, trueItem);
				else inv.setItem(i, falseItem);
			}
		}
		
		if (dungeons.size() >= (4 * (page - 1)) + 2) {
			for (int i = 21; i < 25; i++) {
				int playerCount = (i - 20);
				
				trueItemMeta.setDisplayName(ChatColor.AQUA + "Le donjon est jouable à " + playerCount + " joueurs: " + ChatColor.GREEN + "vrai");
				falseItemMeta.setDisplayName(ChatColor.AQUA + "Le donjon est jouable à " + playerCount + " joueurs: " + ChatColor.RED + "Faux");
				trueItem.setItemMeta(trueItemMeta);
				falseItem.setItemMeta(falseItemMeta);
				
				if (dataStore.isPlayableWith(dungeons.get((4 * (page - 1)) + 1), playerCount + "")) inv.setItem(i, trueItem);
				else inv.setItem(i, falseItem);
			}
		}
		
		if (dungeons.size() >= (4 * (page - 1)) + 3) {
			for (int i = 30; i < 34; i++) {
				int playerCount = (i - 29);
				
				trueItemMeta.setDisplayName(ChatColor.AQUA + "Le donjon est jouable à " + playerCount + " joueurs: " + ChatColor.GREEN + "vrai");
				falseItemMeta.setDisplayName(ChatColor.AQUA + "Le donjon est jouable à " + playerCount + " joueurs: " + ChatColor.RED + "Faux");
				trueItem.setItemMeta(trueItemMeta);
				falseItem.setItemMeta(falseItemMeta);
				
				if (dataStore.isPlayableWith(dungeons.get((4 * (page - 1)) + 2), playerCount + "")) inv.setItem(i, trueItem);
				else inv.setItem(i, falseItem);
			}
		}
		
		if (dungeons.size() >= (4 * (page - 1)) + 4) {
			for (int i = 39; i < 43; i++) {
				int playerCount = (i - 38);
				
				trueItemMeta.setDisplayName(ChatColor.AQUA + "Le donjon est jouable à " + playerCount + " joueurs: " + ChatColor.GREEN + "vrai");
				falseItemMeta.setDisplayName(ChatColor.AQUA + "Le donjon est jouable à " + playerCount + " joueurs: " + ChatColor.RED + "Faux");
				trueItem.setItemMeta(trueItemMeta);
				falseItem.setItemMeta(falseItemMeta);
				
				if (dataStore.isPlayableWith(dungeons.get((4 * (page - 1)) + 3), playerCount + "")) inv.setItem(i, trueItem);
				else inv.setItem(i, falseItem);
			}
		}
		
		inv.setItem(46, filling);
		if (page > 1) inv.setItem(48, previous);
		else inv.setItem(48, filling);
		inv.setItem(49, close);
		if (dungeons.size() > (4 * page)) inv.setItem(50, next);
		else inv.setItem(50, filling);
		inv.setItem(51, filling);
		
		for (int i = (4 * (page - 1)); i < dungeons.size(); i++) {
			ItemStack item = new ItemStack(Material.CHISELED_STONE_BRICKS);
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', dungeons.get(i)));
			item.setItemMeta(itemMeta);
			inv.addItem(item);
		}
		
		p.openInventory(inv);
		
	}
	
}

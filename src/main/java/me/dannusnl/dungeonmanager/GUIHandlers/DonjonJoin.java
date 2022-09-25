package me.dannusnl.dungeonmanager.GUIHandlers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.dannusnl.dungeonmanager.DataManager;
import me.dannusnl.dungeonmanager.DungeonTeleporter;
import me.dannusnl.dungeonmanager.MainClass;
import net.md_5.bungee.api.chat.hover.content.Text;
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

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class DonjonJoin implements Listener {

	Map<Player, Integer> selectedPlayers = new HashMap<>();

	@EventHandler
	public void dungeonPlayers(InventoryClickEvent e) {
		if (e.getView().getTitle().equals(ChatColor.DARK_AQUA + "Jeu solo ou multijoueur?")) {
			if (e.getClickedInventory().equals(e.getView().getBottomInventory())) return;
			if (e.getCurrentItem() == null) return;

			e.setCancelled(true);
			
			if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
				
				e.getWhoClicked().closeInventory();
				
			} else if (e.getCurrentItem().getType().equals(Material.POPPY)) {
				
				selectedPlayers.put((Player) e.getWhoClicked(), e.getCurrentItem().getAmount());
				
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
				List<String> dungeons = DataManager.getDungeons();

				for (String dungeon : dungeons) {
					if (DataManager.isPlayableWith(dungeon, 1 + "")) {
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
				Inventory inv = Bukkit.createInventory(null, 36, ChatColor.DARK_AQUA + "Combien de joueurs?");

				ItemStack filling = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
				ItemMeta fillingMeta = filling.getItemMeta();
				fillingMeta.setDisplayName(" ");
				filling.setItemMeta(fillingMeta);

				for (int i = 0; i < inv.getSize(); i++) {
					inv.setItem(i, filling);
				}

				ItemStack lines = new ItemStack(Material.TOTEM_OF_UNDYING);
				lines.setAmount(2);
				ItemMeta linesMeta = lines.getItemMeta();
				linesMeta.setDisplayName(ChatColor.AQUA + "Nombre de joueurs");
				lines.setItemMeta(linesMeta);

				ItemStack down = new ItemStack(Material.REDSTONE);
				ItemMeta downMeta = down.getItemMeta();
				downMeta.setDisplayName(ChatColor.RED + "-1");
				down.setItemMeta(downMeta);

				ItemStack up = new ItemStack(Material.EMERALD);
				ItemMeta upMeta = up.getItemMeta();
				upMeta.setDisplayName(ChatColor.GREEN + "+1");
				up.setItemMeta(upMeta);

				ItemStack close = new ItemStack(Material.BARRIER);
				ItemMeta closeMeta = close.getItemMeta();
				closeMeta.setDisplayName(ChatColor.RED + "Fermer");
				close.setItemMeta(closeMeta);

				ItemStack create = new ItemStack(Material.PISTON);
				ItemMeta createMeta = create.getItemMeta();
				createMeta.setDisplayName(ChatColor.RED + "Suivant");
				create.setItemMeta(createMeta);

				up.setItemMeta(upMeta);

				inv.setItem(11, down);
				inv.setItem(13, lines);
				inv.setItem(15, up);
				inv.setItem(30, close);
				inv.setItem(32, create);

				openDelayedInv(e.getWhoClicked(), inv);
			}
			
		}



		else if (e.getView().getTitle().startsWith(ChatColor.DARK_AQUA + "Combien de joueurs?")) {
			if (e.getClickedInventory().equals(e.getView().getBottomInventory())) return;
			if (e.getCurrentItem() == null) return;

			e.setCancelled(true);

			if (e.getCurrentItem().getType().equals(Material.BARRIER)) {

				e.getWhoClicked().closeInventory();

			} else if (e.getCurrentItem().getType().equals(Material.REDSTONE)) {
				if (e.getInventory().getItem(13).getAmount() == 2) return;

				ItemStack newItem = e.getInventory().getItem(13);
				newItem.setAmount(e.getInventory().getItem(13).getAmount() - 1);
				e.getInventory().setItem(13, newItem);
			} else if (e.getCurrentItem().getType().equals(Material.EMERALD)) {
				if (e.getInventory().getItem(13).getAmount() == 10) return;

				ItemStack newItem = e.getInventory().getItem(13);
				newItem.setAmount(e.getInventory().getItem(13).getAmount() + 1);
				e.getInventory().setItem(13, newItem);
			} else if (e.getCurrentItem().getType().equals(Material.PISTON)) {
				Inventory inv = Bukkit.createInventory(null, 36, ChatColor.DARK_AQUA + "Combien de joueurs?");

				ItemStack filling = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
				ItemMeta fillingMeta = filling.getItemMeta();
				fillingMeta.setDisplayName(" ");
				filling.setItemMeta(fillingMeta);

				for (int i = 0; i < inv.getSize(); i++) {
					inv.setItem(i, filling);
				}

				ItemStack lines = new ItemStack(Material.TOTEM_OF_UNDYING);
				lines.setAmount(2);
				ItemMeta linesMeta = lines.getItemMeta();
				linesMeta.setDisplayName(ChatColor.AQUA + "Nombre de joueurs");
				lines.setItemMeta(linesMeta);

				ItemStack down = new ItemStack(Material.REDSTONE);
				ItemMeta downMeta = down.getItemMeta();
				downMeta.setDisplayName(ChatColor.RED + "-1");
				down.setItemMeta(downMeta);

				ItemStack up = new ItemStack(Material.EMERALD);
				ItemMeta upMeta = up.getItemMeta();
				upMeta.setDisplayName(ChatColor.GREEN + "+1");
				up.setItemMeta(upMeta);

				ItemStack close = new ItemStack(Material.BARRIER);
				ItemMeta closeMeta = close.getItemMeta();
				closeMeta.setDisplayName(ChatColor.RED + "Fermer");
				close.setItemMeta(closeMeta);

				ItemStack create = new ItemStack(Material.PISTON);
				ItemMeta createMeta = create.getItemMeta();
				createMeta.setDisplayName(ChatColor.RED + "Suivant");
				create.setItemMeta(createMeta);

				up.setItemMeta(upMeta);

				inv.setItem(11, down);
				inv.setItem(13, lines);
				inv.setItem(15, up);
				inv.setItem(30, close);
				inv.setItem(32, create);

				openDelayedInv(e.getWhoClicked(), inv);
			}
		}
		
		
		
		else if (e.getView().getTitle().startsWith(ChatColor.DARK_AQUA + "Donjons - Page ")) {
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
				List<String> dungeons = DataManager.getDungeons();
				
				for (int i = (newPage - 1) * 36; i < dungeons.size(); i++) {
					if (DataManager.isPlayableWith(dungeons.get(i), e.getCurrentItem().getAmount() + "")) {
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
				
				if (selectedPlayers.get(e.getWhoClicked()) == 1) {
					e.getWhoClicked().closeInventory();
					e.getWhoClicked().teleport(DataManager.getWaitRoomLoc());
					e.getWhoClicked().sendMessage(ChatColor.AQUA + "Le monde est en train de se charger, veuillez patienter...");
					
					String game = DataManager.getGameOfPlayer((Player) e.getWhoClicked());
					if (game != null) DataManager.leaveGame((Player) e.getWhoClicked(), game);
					DungeonTeleporter.requestedWorlds.remove(e.getWhoClicked());
					
					String worldName = e.getCurrentItem().getItemMeta().getDisplayName().replace('§', '&');
					File worldFile = new File(MainClass.getPlugin(MainClass.class).getDataFolder() + "/" + worldName);
					
					DungeonTeleporter.requestedWorlds.put((Player) e.getWhoClicked(), DataManager.loadMap(worldFile));
					DungeonTeleporter.requestedDonjon.put((Player) e.getWhoClicked(), worldName);
				} else {
					Inventory inv = Bukkit.createInventory(null, 27, ChatColor.AQUA + "Donjon: " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', e.getCurrentItem().getItemMeta().getDisplayName()));
					
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
		}
		
		
		
		else if (e.getView().getTitle().startsWith(ChatColor.AQUA + "Donjon: ")) {
			e.setCancelled(true);
			
			if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
				
				e.getWhoClicked().closeInventory();
				
			} else if (e.getCurrentItem().getType().equals(Material.ARROW)) {
				
				e.getWhoClicked().closeInventory();
				e.getWhoClicked().teleport(DataManager.getWaitRoomLoc());
				e.getWhoClicked().sendMessage(ChatColor.AQUA + "Le monde est en train de se charger, veuillez patienter...");
				
				String game = DataManager.getGameOfPlayer((Player) e.getWhoClicked());
				if (game != null) DataManager.leaveGame((Player) e.getWhoClicked(), game);
				DungeonTeleporter.requestedWorlds.remove(e.getWhoClicked());
				
				DataManager.createGame((Player) e.getWhoClicked(), e.getView().getTitle().replace(ChatColor.AQUA + "Donjon: " + ChatColor.RESET, "").replace('§', '&'), selectedPlayers.get(e.getWhoClicked()));
				
			} else if (e.getCurrentItem().getType().equals(Material.TIPPED_ARROW)) {
				
				ItemStack close = new ItemStack(Material.BARRIER);
				ItemMeta closeMeta = close.getItemMeta();
				closeMeta.setDisplayName(ChatColor.RED + "Fermer");
				close.setItemMeta(closeMeta);
				
				ItemStack filling = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
				ItemMeta fillingMeta = filling.getItemMeta();
				fillingMeta.setDisplayName(" ");
				filling.setItemMeta(fillingMeta);
				
				int itemCount = 0;
				List<ItemStack> items = new ArrayList<>();
				List<String> dungeons = DataManager.getGames(selectedPlayers.get(e.getWhoClicked()), e.getView().getTitle().replace(ChatColor.AQUA + "Donjon: " + ChatColor.RESET, ""));

				for (String dungeon : dungeons) {
					ItemStack item = new ItemStack(Material.CHISELED_STONE_BRICKS);
					ItemMeta itemMeta = item.getItemMeta();
					itemMeta.setDisplayName(ChatColor.AQUA + "Game: " + dungeon);
					List<String> itemLore = new ArrayList<>();
					String[] players = DataManager.getPlayers("AAA-" + dungeon);
					itemLore.add(" ");
					if (DataManager.startedGames.contains("AAA-" + dungeon)) {
						itemLore.add(ChatColor.GOLD + "Attention, le jeu a déjà commencé!");
						itemLore.add(" ");
					}
					itemLore.add(ChatColor.DARK_AQUA + "Joueurs en attente (" + players.length + "/" + selectedPlayers.get(e.getWhoClicked()) + "):");
					for (String player : players) {
						itemLore.add(ChatColor.AQUA + " - " + player);
					}
					itemMeta.setLore(itemLore);
					item.setItemMeta(itemMeta);
					items.add(item);
					itemCount++;
				}
				
				Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_AQUA + "Donjon join: " + e.getView().getTitle().replace(ChatColor.AQUA + "Donjon: ", ""));
				if (itemCount <= 9) inv = Bukkit.createInventory(null, 27, ChatColor.DARK_AQUA + "Donjon join: " + e.getView().getTitle().replace(ChatColor.AQUA + "Donjon: ", ""));
				else if (itemCount <= 18) inv = Bukkit.createInventory(null, 36, ChatColor.DARK_AQUA + "Donjon join: " + e.getView().getTitle().replace(ChatColor.AQUA + "Donjon: ", ""));
				else if (itemCount <= 27) inv = Bukkit.createInventory(null, 45, ChatColor.DARK_AQUA + "Donjon join: " + e.getView().getTitle().replace(ChatColor.AQUA + "Donjon: ", ""));

				for (ItemStack item : items) {
					inv.addItem(item);
				}
				
				for (int i = (inv.getSize() - 18); i < inv.getSize(); i++) {
					inv.setItem(i, filling);
				}
				inv.setItem(inv.getSize() - 5, close);
				
				openDelayedInv(e.getWhoClicked(), inv);
			}
		} 
		
		
		
		else if (e.getView().getTitle().startsWith(ChatColor.DARK_AQUA + "Donjon join: ")) {
			e.setCancelled(true);
			
			if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
				
				e.getWhoClicked().closeInventory();
				
			} else if (e.getCurrentItem().getType().equals(Material.CHISELED_STONE_BRICKS)) {
				
				if (DataManager.getPlayers("AAA-" + e.getCurrentItem().getItemMeta().getDisplayName().replace(ChatColor.AQUA + "Game: ", "")).length < selectedPlayers.get(e.getWhoClicked())) {
					
					String game = DataManager.getGameOfPlayer((Player) e.getWhoClicked());
					if (game != null) {
						if (game.equals("AAA-" + e.getCurrentItem().getItemMeta().getDisplayName().replace(ChatColor.AQUA + "Game: ", ""))) {
							e.getWhoClicked().sendMessage(ChatColor.RED + "Vous êtes déjà dans ce jeu!");
							return;
						} else {
							DataManager.leaveGame((Player) e.getWhoClicked(), game);
						}
					}
					DungeonTeleporter.requestedWorlds.remove(e.getWhoClicked());
					
					e.getWhoClicked().closeInventory();
					String game1 = "AAA-" + e.getCurrentItem().getItemMeta().getDisplayName().replace(ChatColor.AQUA + "Game: ", "");
					if (e.getCurrentItem().getItemMeta().getLore().contains(ChatColor.GOLD + "Attention, le jeu a déjà commencé!")) {
						e.getWhoClicked().sendMessage(ChatColor.GREEN + "Demande de rejoindre le jeu envoyée. Vous serez automatiquement admis lorsque vous serez accepté.");
						for (String player : DataManager.getPlayers(game1)) {
							TextComponent text = new TextComponent(ChatColor.AQUA + e.getWhoClicked().getName() + " aimerait participer. Cliquez sur ce message pour accepter!");
							text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.AQUA + "Cliquez pour accepter")));
							text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/accepterrequete"));
							Bukkit.getPlayer(player).spigot().sendMessage(text);
							DataManager.request.put(game1, (Player) e.getWhoClicked());
						}
					} else {
						e.getWhoClicked().teleport(DataManager.getWaitRoomLoc());
						DataManager.joinGame((Player) e.getWhoClicked(), game1);
					}
				} else {
					e.getWhoClicked().sendMessage(ChatColor.RED + "Le jeu est complet!");
				}
				
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

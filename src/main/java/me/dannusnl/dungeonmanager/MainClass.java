package me.dannusnl.dungeonmanager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.dannusnl.dungeonmanager.guihandlers.donjonjoin.MultiPlayer;
import me.dannusnl.dungeonmanager.guihandlers.donjonjoin.SinglePlayer;
import me.dannusnl.dungeonmanager.guihandlers.DonjonManager;
import me.dannusnl.dungeonmanager.guihandlers.SetMusique;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class MainClass extends JavaPlugin {

	private static MainClass instance;

	DataStore dataStore = new DataStore();
	public DataStore getDataStore() {
		return dataStore;
	}

	public static MainClass getMainClass() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;

		getCommand("donjonjoint").setExecutor(this);
		getCommand("donjonmanager").setExecutor(this);
		getCommand("accepterrequete").setExecutor(this);
		
		getServer().getPluginManager().registerEvents(new DungeonTeleporter(), this);
		getServer().getPluginManager().registerEvents(new WaitingroomSelector(), this);
		getServer().getPluginManager().registerEvents(new WorldManager(), this);

		getServer().getPluginManager().registerEvents(new DonjonManager(), this);
		getServer().getPluginManager().registerEvents(new SetMusique(), this);
		getServer().getPluginManager().registerEvents(new SinglePlayer(), this);
		getServer().getPluginManager().registerEvents(new MultiPlayer(), this);

		if (!getDataFolder().exists()) dataStore.setup();
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			List<Player> checked = new ArrayList<>();
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (!DungeonTeleporter.starting.contains(p) && DungeonTeleporter.requestedWorlds.containsKey(p) && !checked.contains(p)) {
					String game = dataStore.getGameOfPlayer(p);
					if (game != null) {
						String[] players = dataStore.getPlayers(game);
						int requiered = dataStore.getGamesRequiredPlayers(game);
						if (players.length == requiered) {
							boolean ableToStart = true;
							for (String player : players) {
								Player foundPlayer = Bukkit.getPlayer(player);
								checked.add(foundPlayer);
								if (!dataStore.isInStartRegion(foundPlayer.getLocation())) ableToStart = false;
							}
							if (ableToStart) {
								for (String player : players) {
									DungeonTeleporter.StartMultiplayerCountdown(Bukkit.getPlayer(player));
								}
							}
						} else {
							if (dataStore.isInStartRegion(p.getLocation())) p.sendTitle(ChatColor.GOLD + "⚠" + ChatColor.RED + " Pas assez de joueurs " + ChatColor.GOLD + "⚠", ChatColor.DARK_RED + "(" + players.length + "/" + requiered + ")", 1, 40, 1);
						}
					} else {
						if (dataStore.isInStartRegion(p.getLocation())) DungeonTeleporter.StartSingleplayerCountdown(p);
					}
				}
			}
		}, 20L, 20L);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			List<String> blockedWorlds = new ArrayList<>();
			for (World world : getServer().getWorlds()) {
				if (world.getPlayers().isEmpty() && !DungeonTeleporter.requestedWorlds.containsValue(world) && world.getName().startsWith("AAA-")) {
					dataStore.deleteWorld(world);
				} else {
					blockedWorlds.add(world.getName());
				}
			}
			for (File file : getServer().getWorldContainer().listFiles()) {
				if (file.getName().startsWith("AAA-")) {
					if (!blockedWorlds.contains(file.getName())) dataStore.deleteDir(file);
				}
			}
		}, 200L, 12000L);
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You need to be a player to do this!");
			return false;
		}
		
		Player p = (Player) sender;
	    
		if (cmd.getName().equalsIgnoreCase("donjonjoint")) {

			if (dataStore.getWaitRoomLoc() == null) {
				p.sendMessage(ChatColor.RED + "Veuillez d'abord configurer l'emplacement de la salle d'attente avec: '/donjonmanager setwaitroom'.");
				return false;
			}
			
			if (!dataStore.StartRegionIsSet()) {
				p.sendMessage(ChatColor.RED + "Veuillez d'abord configurer l'emplacement de la salle d'attente avec: '/donjonmanager setstartregion'.");
				return false;
			}
			
			Inventory inv = Bukkit.createInventory(null, 36, ChatColor.DARK_AQUA + "Jeu solo ou multijoueur?");
			
			ItemStack filling = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
			ItemMeta fillingMeta = filling.getItemMeta();
			fillingMeta.setDisplayName(" ");
			filling.setItemMeta(fillingMeta);
			
			ItemStack single = new ItemStack(Material.POPPY);
			ItemMeta singleMeta = single.getItemMeta();
			singleMeta.setDisplayName(ChatColor.AQUA + "Solo");
			single.setItemMeta(singleMeta);
			
			ItemStack multi = new ItemStack(Material.ROSE_BUSH);
			ItemMeta multiMeta = multi.getItemMeta();
			multiMeta.setDisplayName(ChatColor.AQUA + "Multijoueur");
			multi.setItemMeta(multiMeta);
			
			ItemStack close = new ItemStack(Material.BARRIER);
			ItemMeta closeMeta = filling.getItemMeta();
			closeMeta.setDisplayName(ChatColor.RED + "Fermer");
			close.setItemMeta(closeMeta);
			
			for (int i = 0; i < inv.getSize(); i++) {
				inv.setItem(i, filling);
			}
			
			inv.setItem(11, single);
			inv.setItem(15, multi);
			inv.setItem(31, close);
			
			p.openInventory(inv);
			
		} else if (cmd.getName().equalsIgnoreCase("donjonmanager")) {
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("setwaitroom")) {

					dataStore.setWaitRoomLoc(p.getLocation());
					p.sendMessage(ChatColor.GREEN + "L'emplacement de la salle d'attente de téléportation est mis à jour avec votre emplacement actuel !");
					
				} else if (args[0].equalsIgnoreCase("setstartregion")) {
					
					ItemStack stick = new ItemStack(Material.STICK);
					ItemMeta stickMeta = stick.getItemMeta();
					stickMeta.setDisplayName(ChatColor.AQUA + "Bâton de sélection");
					stick.setItemMeta(stickMeta);
					
					p.getEquipment().setItemInMainHand(stick);
					p.sendMessage(ChatColor.BLUE + "Sélectionnez une zone en cliquant avec le bouton droit et le bouton gauche sur un bloc avec ce bâton.");
					
				} else if (args[0].equalsIgnoreCase("setplayeramount")) {
					DungeonManager dungeonManager = new DungeonManager();
					dungeonManager.setplayeramount(p, 1);
					
				} else if (args[0].equalsIgnoreCase("setmusic")) {
					if (p.getEquipment().getItemInMainHand() == null) {
						p.sendMessage(ChatColor.RED + "Veuillez tenir un disque de musique dans votre main.");
						return false;
					}
					
					if (!p.getEquipment().getItemInMainHand().getType().isRecord()) {
						p.sendMessage(ChatColor.RED + "Veuillez tenir un disque de musique dans votre main.");
						return false;
					}

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
						ItemStack item = new ItemStack(Material.CHISELED_STONE_BRICKS);
						ItemMeta itemMeta = item.getItemMeta();
						itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', dungeon));
						List<String> itemLore = new ArrayList<>();
						itemLore.add(" ");
						if (dataStore.getMusic(dungeon) == null) itemLore.add(ChatColor.RED + "Not set.");
						else
							itemLore.add(ChatColor.DARK_AQUA + dataStore.getMusic(dungeon).getType().name().toLowerCase().replace('_', ' '));
						itemMeta.setLore(itemLore);
						item.setItemMeta(itemMeta);
						items.add(item);
						itemCount++;
					}
					
					Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_AQUA + "Pour quel donjon? - Page 1");
					if (itemCount <= 9) inv = Bukkit.createInventory(null, 27, ChatColor.DARK_AQUA + "Pour quel donjon? - Page 1");
					else if (itemCount <= 18) inv = Bukkit.createInventory(null, 36, ChatColor.DARK_AQUA + "Pour quel donjon? - Page 1");
					else if (itemCount <= 27) inv = Bukkit.createInventory(null, 45, ChatColor.DARK_AQUA + "Pour quel donjon? - Page 1");

					for (ItemStack item : items) {
						inv.addItem(item);
					}
					
					for (int i = (inv.getSize() - 18); i < inv.getSize(); i++) {
						inv.setItem(i, filling);
					}
					
					inv.setItem(inv.getSize() - 5, close);
					if (itemCount > 36) inv.setItem(inv.getSize() - 4, next);
					
					p.openInventory(inv);
					
				} else {
					p.sendMessage(ChatColor.RED + "Mauvais argument! Utilisez '/donjonmanager [setwaitroom | setstartregion | setplayeramount | setmusic]'.");
				}
				
			} else {
				p.sendMessage(ChatColor.RED + "Mauvais argument! Utilisez '/donjonmanager [setwaitroom | setstartregion | setplayeramount | setmusic]'.");
			}
			
		} else if (cmd.getName().equalsIgnoreCase("accepterrequete")) {
			
			String game = dataStore.getGameOfPlayer(p);
			if (game != null) {
				if (dataStore.request.containsKey(game)) {
					for (String player : dataStore.getPlayers(game)) {
						Bukkit.getPlayer(player).sendMessage(ChatColor.GREEN + "Demande acceptée ! Le joueur peut arriver à tout moment.");
						
						dataStore.request.get(game).teleport(p.getWorld().getSpawnLocation());
						List<String> dataList = dataStore.games.get(game);
						String players = dataList.get(2) + "," + dataStore.request.get(game);
						dataList.set(2, players);
						dataStore.games.put(game, dataList);
						
						dataStore.request.remove(game);
					}
				} else {
					p.sendMessage(ChatColor.RED + "La demande n'est plus valable!");
				}
			} else {
				p.sendMessage(ChatColor.RED + "Vous n'êtes plus dans ce jeu !");
			}
			
		}
		
		return false;
	}

}



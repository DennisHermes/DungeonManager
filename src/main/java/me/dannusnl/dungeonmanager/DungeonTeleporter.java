package me.dannusnl.dungeonmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class DungeonTeleporter implements Listener {

	public static HashMap<Player, World> requestedWorlds = new HashMap<>();
	public static HashMap<Player, String> requestedDonjon = new HashMap<>();
	static List<Player> starting = new ArrayList<>();

	public static void StartSingleplayerCountdown(Player p) {

		DataStore dataStore = MainClass.getMainClass().getDataStore();

		starting.add(p);
		for (int i = 0; i < 11; i++) {
			int i0 = i;
			new BukkitRunnable() { 
				@Override
				public void run() {
					if (dataStore.isInStartRegion(p.getLocation())) {
						if (i0 == 10) {
							p.sendTitle(ChatColor.DARK_AQUA + "Téléportation au donjon...", "", 1, 40, 1);
							p.teleport(DungeonTeleporter.requestedWorlds.get(p).getSpawnLocation());

							Material music = dataStore.getMusic(requestedDonjon.get(p)).getType();
							if (music != null) p.playEffect(p.getLocation(), Effect.RECORD_PLAY, dataStore.getMusic(requestedDonjon.get(p)).getType());

							requestedWorlds.remove(p);
							starting.remove(p);
						} else {
							p.sendTitle(ChatColor.DARK_AQUA + "Départs en: ", ChatColor.AQUA + "" + (10 - i0) + " secondes", 1, 40, 1);
						}
					} else {
						starting.remove(p);
						p.sendTitle("", "", 1, 20, 1);
					}
				}
			}.runTaskLater(MainClass.getPlugin(MainClass.class), i * 20);
		}
	}
	
	public static void StartMultiplayerCountdown(Player p) {

		DataStore dataStore = MainClass.getMainClass().getDataStore();

		starting.add(p);
		for (int i = 0; i < 11; i++) {
			int i0 = i;
			new BukkitRunnable() {
				@Override
				public void run() {
					String game = dataStore.getGameOfPlayer(p);
					if (game != null) {
						String[] players = dataStore.getPlayers(game);
						int requiered = dataStore.getGamesRequiredPlayers(game);
						if (players.length == requiered) {
							boolean ableToStart = true;
							for (String player : players) {
								Player foundPlayer = Bukkit.getPlayer(player);
								if (!dataStore.isInStartRegion(foundPlayer.getLocation())) ableToStart = false;
							}
							if (ableToStart) {
								if (i0 == 10) {
									for (String player : players) {
										Player foundPlayer = Bukkit.getPlayer(player);
										foundPlayer.sendTitle(ChatColor.DARK_AQUA + "Téléportation au donjon...", "", 1, 40, 1);
										foundPlayer.teleport(DungeonTeleporter.requestedWorlds.get(foundPlayer).getSpawnLocation());
										if (dataStore.getMusic(requestedDonjon.get(p)) != null) {
											p.playEffect(p.getLocation(), Effect.RECORD_PLAY, dataStore.getMusic(requestedDonjon.get(p)).getType());
										}
										for (Map.Entry<String, List<String>> entry : dataStore.games.entrySet()) {
										    List<String> list = entry.getValue();
										    if (entry.getKey().equals(game)) list.get(0);
										}
										requestedWorlds.remove(foundPlayer);
										starting.remove(foundPlayer);
									}
									dataStore.startedGames.add(game);
								} else {
									p.sendTitle(ChatColor.DARK_AQUA + "Départs en: ", ChatColor.AQUA + "" + (10 - i0) + " secondes", 1, 40, 1);
								}
							} else {
								starting.remove(p);
							}
						} else {
							p.sendTitle(ChatColor.GOLD + "⚠" + ChatColor.RED + " Pas assez de joueurs " + ChatColor.GOLD + "⚠", ChatColor.DARK_RED + "(" + players.length + "/" + requiered + ")", 1, 40, 1);
							starting.remove(p);
						}
					} else {
						starting.remove(p);
						p.sendTitle("", "", 1, 20, 1);
					}
				}
			}.runTaskLater(MainClass.getPlugin(MainClass.class), i * 20);
		}
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {

		DataStore dataStore = MainClass.getMainClass().getDataStore();

		String game = dataStore.getGameOfPlayer(e.getPlayer());
		if (game != null) dataStore.leaveGame(e.getPlayer(), game);
		requestedWorlds.remove(e.getPlayer());
	}
	
}

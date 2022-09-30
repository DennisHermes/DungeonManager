package me.dannusnl.dungeonmanager;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldManager implements Listener {

	@EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
		DataStore dataStore = MainClass.getMainClass().getDataStore();
		World world = e.getFrom();
		if (world.getPlayers().isEmpty() && !DungeonTeleporter.requestedWorlds.containsValue(world) && world.getName().startsWith("AAA-")) {
			dataStore.deleteWorld(world);
			dataStore.games.remove(dataStore.getGameOfPlayer(e.getPlayer()));
		}
		String game = dataStore.getGameOfPlayer(e.getPlayer());
		if (game != null) {
			if (!e.getPlayer().getWorld().getName().contains(game)) dataStore.leaveGame(e.getPlayer(), game);
		}
		DungeonTeleporter.requestedWorlds.remove(e.getPlayer());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void worldInit(org.bukkit.event.world.WorldInitEvent e) {
		e.getWorld().setKeepSpawnInMemory(false);
	}
	
}

package me.dannusnl.dungeonmanager;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldManager implements Listener {

	@EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
		World world = e.getFrom();
		if (world.getPlayers().isEmpty() && !DungeonTeleporter.requestedWorlds.containsValue(world) && world.getName().startsWith("AAA-")) {
			DataManager.deleteWorld(world);
			DataManager.games.remove(DataManager.getGameOfPlayer(e.getPlayer()));
		}
		String game = DataManager.getGameOfPlayer(e.getPlayer());
		if (game != null) {
			if (!e.getPlayer().getWorld().getName().contains(game)) DataManager.leaveGame(e.getPlayer(), game);
		}
		DungeonTeleporter.requestedWorlds.remove(e.getPlayer());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void worldInit(org.bukkit.event.world.WorldInitEvent e) {
		e.getWorld().setKeepSpawnInMemory(false);
	}
	
}

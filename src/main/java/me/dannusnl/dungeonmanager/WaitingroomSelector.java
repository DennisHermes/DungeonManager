package me.dannusnl.dungeonmanager;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class WaitingroomSelector implements Listener {

	HashMap<Player, Location> pos1 = new HashMap<>();
	HashMap<Player, Location> pos2 = new HashMap<>();

	@EventHandler
	public void posSelection(PlayerInteractEvent e) {
		if (e.getItem() == null) return;
		
		if (e.getItem().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Bâton de sélection")) {
			
			e.setCancelled(true);
			
			if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
				pos1.put(e.getPlayer(), e.getClickedBlock().getLocation());
				e.getPlayer().sendMessage(ChatColor.GREEN + "Position 1 placée");
			} else if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				pos2.put(e.getPlayer(), e.getClickedBlock().getLocation());
				e.getPlayer().sendMessage(ChatColor.GREEN + "Position 2 placée");
			}
			
			if (pos1.get(e.getPlayer()) != null && pos2.get(e.getPlayer()) != null) {
				DataStore dataStore = MainClass.getMainClass().getDataStore();
				dataStore.setStartRegion(pos1.get(e.getPlayer()), pos2.get(e.getPlayer()));
				pos1.put(e.getPlayer(), null);
				pos2.put(e.getPlayer(), null);
				e.getPlayer().getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
				e.getPlayer().sendMessage(ChatColor.GREEN + "Réussir à définir la zone de départ!");
			}
			
		}
	}
	
}

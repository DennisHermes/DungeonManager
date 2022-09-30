package me.dannusnl.dungeonmanager.guihandlers.donjonjoin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.dannusnl.dungeonmanager.DataStore;
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

public class MultiPlayer implements Listener {

    Map<Player, Integer> selectedPlayers = new HashMap<>();

    @EventHandler
    public void dungeonPlayers(InventoryClickEvent e) {

        DataStore dataStore = MainClass.getMainClass().getDataStore();

        if (e.getView().getTitle().startsWith(ChatColor.AQUA + "Rejoindre ou créer un jeu")) {
            if (e.getClickedInventory() == null) return;
            if (e.getClickedInventory().equals(e.getView().getBottomInventory())) return;
            if (e.getCurrentItem() == null) return;

            e.setCancelled(true);

            if (e.getCurrentItem().getType().equals(Material.BARRIER)) {

                e.getWhoClicked().closeInventory();

            } else if (e.getCurrentItem().getType().equals(Material.ARROW)) {
                Inventory inv = Bukkit.createInventory(null, 36, ChatColor.AQUA + "Nombre de joueurs");

                ItemStack filling = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
                ItemMeta fillingMeta = filling.getItemMeta();
                fillingMeta.setDisplayName(" ");
                filling.setItemMeta(fillingMeta);

                for (int i = 0; i < inv.getSize(); i++) {
                    inv.setItem(i, filling);
                }


                ItemStack players = new ItemStack(Material.TOTEM_OF_UNDYING);
                players.setAmount(2);
                ItemMeta playersMeta = players.getItemMeta();
                playersMeta.setDisplayName(ChatColor.AQUA + "Nombre de joueurs");
                players.setItemMeta(playersMeta);

                ItemStack down = new ItemStack(Material.REDSTONE);
                ItemMeta downMeta = down.getItemMeta();
                downMeta.setDisplayName(ChatColor.RED + "-1");
                down.setItemMeta(downMeta);

                ItemStack up = new ItemStack(Material.EMERALD);
                ItemMeta upMeta = up.getItemMeta();
                upMeta.setDisplayName(ChatColor.GREEN + "+1");
                up.setItemMeta(upMeta);

                ItemStack close = new ItemStack(Material.BARRIER);
                ItemMeta closeMeta = filling.getItemMeta();
                closeMeta.setDisplayName(ChatColor.RED + "Fermer");
                close.setItemMeta(closeMeta);

                ItemStack create = new ItemStack(Material.PISTON);
                ItemMeta createMeta = create.getItemMeta();
                createMeta.setDisplayName(ChatColor.AQUA + "créer");
                create.setItemMeta(createMeta);

                inv.setItem(11, down);
                inv.setItem(13, players);
                inv.setItem(15, up);
                inv.setItem(30, close);
                inv.setItem(32, create);

                openDelayedInv(e.getWhoClicked(), inv);

            }  else if (e.getCurrentItem().getType().equals(Material.TIPPED_ARROW)) {

                ItemStack filling = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
                ItemMeta fillingMeta = filling.getItemMeta();
                fillingMeta.setDisplayName(" ");
                filling.setItemMeta(fillingMeta);

                ItemStack close = new ItemStack(Material.BARRIER);
                ItemMeta closeMeta = filling.getItemMeta();
                closeMeta.setDisplayName(ChatColor.RED + "Fermer");
                close.setItemMeta(closeMeta);

                List<ItemStack> items = new ArrayList<>();

                for (String dungeon : dataStore.getGames()) {
                    ItemStack item = new ItemStack(Material.CHISELED_STONE_BRICKS);
                    ItemMeta itemMeta = item.getItemMeta();
                    itemMeta.setDisplayName(ChatColor.AQUA + "Game: " + dungeon);
                    List<String> itemLore = new ArrayList<>();
                    String[] players = dataStore.getPlayers("AAA-" + dungeon);
                    itemLore.add(" ");
                    itemLore.add(ChatColor.DARK_AQUA + "Donjon: " + ChatColor.AQUA + dataStore.getDungeonOfGame("AAA-" + dungeon));
                    itemLore.add(" ");
                    if (dataStore.startedGames.contains("AAA-" + dungeon)) {
                        itemLore.add(ChatColor.GOLD + "Attention, le jeu a déjà commencé!");
                        itemLore.add(" ");
                    }
                    itemLore.add(ChatColor.DARK_AQUA + "Joueurs en attente (" + ChatColor.AQUA + players.length + ChatColor.DARK_AQUA + "/" + ChatColor.AQUA + dataStore.getPlayersOfGame("AAA-" + dungeon) + ChatColor.DARK_AQUA + "):");
                    for (String player : players) {
                        itemLore.add(ChatColor.AQUA + " - " + player);
                    }
                    itemMeta.setLore(itemLore);
                    item.setItemMeta(itemMeta);
                    items.add(item);
                }

                Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_AQUA + "Jeux disponibles - page 1");
                if (items.size() <= 9) inv = Bukkit.createInventory(null, 27, ChatColor.DARK_AQUA + "Jeux disponibles - page 1");
                else if (items.size() <= 18) inv = Bukkit.createInventory(null, 36, ChatColor.DARK_AQUA + "Jeux disponibles - page 1");
                else if (items.size() <= 27) inv = Bukkit.createInventory(null, 45, ChatColor.DARK_AQUA + "Jeux disponibles - page 1");

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



        else if (e.getView().getTitle().equals(ChatColor.AQUA + "Nombre de joueurs")) {
            if (e.getClickedInventory() == null) return;
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
                selectedPlayers.put((Player) e.getWhoClicked(), e.getInventory().getItem(13).getAmount());

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

                Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_AQUA + "Choisir un donjon - page 1");
                if (itemCount <= 9) inv = Bukkit.createInventory(null, 27, ChatColor.DARK_AQUA + "Choisir un donjon - page 1");
                else if (itemCount <= 18) inv = Bukkit.createInventory(null, 36, ChatColor.DARK_AQUA + "Choisir un donjon - page 1");
                else if (itemCount <= 27) inv = Bukkit.createInventory(null, 45, ChatColor.DARK_AQUA + "Choisir un donjon - page 1");

                for (ItemStack item : items) {
                    inv.addItem(item);
                }

                for (int i = (inv.getSize() - 18); i < inv.getSize(); i++) {
                    inv.setItem(i, filling);
                }

                inv.setItem(inv.getSize() - 5, close);
                if (itemCount > 36) inv.setItem(inv.getSize() - 4, next);

                openDelayedInv(e.getWhoClicked(), inv);
            }
        }



        else if (e.getView().getTitle().startsWith(ChatColor.DARK_AQUA + "Choisir un donjon - page")) {
            if (e.getClickedInventory() == null) return;
            if (e.getClickedInventory().equals(e.getView().getBottomInventory())) return;
            if (e.getCurrentItem() == null) return;

            e.setCancelled(true);

            if (e.getCurrentItem().getType().equals(Material.BARRIER)) {

                e.getWhoClicked().closeInventory();

            } else if (e.getCurrentItem().getType().equals(Material.CHISELED_STONE_BRICKS)) {

                e.getWhoClicked().closeInventory();
                e.getWhoClicked().teleport(dataStore.getWaitRoomLoc());
                e.getWhoClicked().sendMessage(ChatColor.AQUA + "Le monde est en train de se charger, veuillez patienter...");

                String game = dataStore.getGameOfPlayer((Player) e.getWhoClicked());
                if (game != null) dataStore.leaveGame((Player) e.getWhoClicked(), game);
                DungeonTeleporter.requestedWorlds.remove(e.getWhoClicked());

                dataStore.createGame((Player) e.getWhoClicked(), e.getCurrentItem().getItemMeta().getDisplayName(), selectedPlayers.get(e.getWhoClicked()));
                e.getWhoClicked().sendMessage(ChatColor.GREEN + "Le donjon a été chargé avec succès! Placez-vous dans la case indiquée pour commencer.");

            }
        }



        else if (e.getView().getTitle().startsWith(ChatColor.DARK_AQUA + "Jeux disponibles - page")) {
            if (e.getClickedInventory() == null) return;
            if (e.getClickedInventory().equals(e.getView().getBottomInventory())) return;
            if (e.getCurrentItem() == null) return;

            e.setCancelled(true);

            if (e.getCurrentItem().getType().equals(Material.BARRIER)) {

                e.getWhoClicked().closeInventory();

            } else if (e.getCurrentItem().getType().equals(Material.CHISELED_STONE_BRICKS)) {

                String gameId = "AAA-" + e.getCurrentItem().getItemMeta().getDisplayName().replace(ChatColor.AQUA + "Game: ", "");

                if (dataStore.getPlayers(gameId).length < dataStore.getPlayersOfGame(gameId)) {
                    String game = dataStore.getGameOfPlayer((Player) e.getWhoClicked());
                    if (game != null) {
                        if (game.equals(gameId)) {
                            e.getWhoClicked().sendMessage(ChatColor.RED + "Vous êtes déjà dans ce jeu!");
                            return;
                        } else {
                            dataStore.leaveGame((Player) e.getWhoClicked(), game);
                        }
                    }
                    DungeonTeleporter.requestedWorlds.remove(e.getWhoClicked());

                    e.getWhoClicked().closeInventory();
                    if (e.getCurrentItem().getItemMeta().getLore().contains(ChatColor.GOLD + "Attention, le jeu a déjà commencé!")) {
                        e.getWhoClicked().sendMessage(ChatColor.GREEN + "Demande de rejoindre le jeu envoyée. Vous serez automatiquement admis lorsque vous serez accepté.");
                        for (String player : dataStore.getPlayers(gameId)) {
                            TextComponent text = new TextComponent(ChatColor.AQUA + e.getWhoClicked().getName() + " aimerait participer. Cliquez sur ce message pour accepter!");
                            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.AQUA + "Cliquez pour accepter")));
                            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/accepterrequete"));
                            Bukkit.getPlayer(player).spigot().sendMessage(text);
                            dataStore.request.put(gameId, (Player) e.getWhoClicked());
                        }
                    } else {
                        e.getWhoClicked().teleport(dataStore.getWaitRoomLoc());
                        dataStore.joinGame((Player) e.getWhoClicked(), gameId);
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
        }.runTaskLater(MainClass.getMainClass(), 1);
    }

}
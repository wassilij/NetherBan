package com.aim.wjcrouse913.NetherBan;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class nBlockListener extends BlockListener {
	public static NetherBan plugin;

	public nBlockListener(NetherBan instance) {
		plugin = instance;

	}
	public void onBlockPlace(BlockPlaceEvent event){
		Block block = event.getBlock();
		Player player = event.getPlayer();
		if(NetherBan.build == true){	
			if(plugin.playerBanish.containsKey(player)){
				player.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "]" + ChatColor.GRAY + " You cannot place blocks while you are banished!");
				event.setCancelled(true);

			}
		}
		if(NetherBan.bedrock == true){
			if(block.getType() == Material.BEDROCK){
				World nether = plugin.getServer().getWorld(NetherBan.nethername);
				player.teleport(nether.getSpawnLocation());
				player.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "]" + ChatColor.GRAY + " You have been banned from the nether for using an illegal material!");
				plugin.playerBanish.put(player, false);
				event.setCancelled(true);

			}
		}
		if(NetherBan.lava == true){
			if(block.getType() == Material.LAVA){
				World nether = plugin.getServer().getWorld(NetherBan.nethername);
				player.teleport(nether.getSpawnLocation());
				player.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "]" + ChatColor.GRAY + " You have been banned from the nether for using an illegal material!");
				plugin.playerBanish.put(player, false);
				event.setCancelled(true);

			}
		}
		if(NetherBan.tnt == true){
			if(block.getType() == Material.TNT){
					World nether = plugin.getServer().getWorld(NetherBan.nethername);
					player.teleport(nether.getSpawnLocation());
					player.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "]" + ChatColor.GRAY + " You have been banned from the nether for using an illegal material!");
					plugin.playerBanish.put(player, false);
					event.setCancelled(true);

			}
		}
		if(NetherBan.fire == true){
			if(block.getType() == Material.FIRE){
				World nether = plugin.getServer().getWorld(NetherBan.nethername);
				player.teleport(nether.getSpawnLocation());
				player.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "]" + ChatColor.GRAY + " You have been banned from the nether for using an illegal material!");
				plugin.playerBanish.put(player, false);
				event.setCancelled(true);

			}
		}
	}
	public void onBlockBreak(BlockBreakEvent event){
		Player player = event.getPlayer();
		if(NetherBan.destroy == true){
			if(plugin.playerBanish.containsKey(player)){
				player.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "]" + ChatColor.GRAY + " You cannot break blocks while you are banished!");
				event.setCancelled(true);

			}
		}
	}
}
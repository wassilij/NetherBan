package com.aim.wjcrouse913.NetherBan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;



public class nPlayerListener extends PlayerListener {
	public static NetherBan plugin;

	public nPlayerListener(NetherBan instance) {
		plugin = instance;

	}
	public void onPlayerMove(PlayerMoveEvent event){
		Player player = event.getPlayer();
		if(!player.getWorld().equals(plugin.getServer().getWorld(NetherBan.nethername))){
			if(plugin.playerBanish.containsKey(player)){
				player.teleport(plugin.getServer().getWorld(NetherBan.nethername).getSpawnLocation());
			}
		}
	}
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event){
		if(NetherBan.commands == true){
			Player player = event.getPlayer();
			if(plugin.playerBanish.containsKey(player)){
				player.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "]" + ChatColor.GRAY + " You cannot use commands while banned!");
				event.setCancelled(true);

			}
		}
	}
	public void onPlayerJoin(PlayerJoinEvent event){
		try {
		    BufferedReader in = new BufferedReader(new FileReader("plugins/NetherBan/banished.txt"));
		    String str;
		    while ((str = in.readLine()) != null) {
		    	final Player player = event.getPlayer();
		    	String name = player.getName();
		    	if(str.equals(name)){
		    		plugin.playerBanish.put(player, false);
		    		player.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "]" + ChatColor.GRAY + " You are still banished to the Nether!");
		    	}
		    }
		    in.close();
		}catch (IOException e){
			e.printStackTrace();
		}
		try {
		    BufferedReader in = new BufferedReader(new FileReader("plugins/NetherBan/whitelist.txt"));
		    String str;
		    while ((str = in.readLine()) != null) {
		    	final Player player = event.getPlayer();
		    	String name = player.getName();
		    	if(str.equals(name)){
		    		plugin.playerSafe.put(player, false);
		    	}
		    }
		    in.close();
		}catch (IOException e){
			e.printStackTrace();

		}
	}
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event){
    	if(NetherBan.emptybucket == true){
    		Player player = event.getPlayer();
    		if(plugin.playerBanish.containsKey(player)){
    			player.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "]" + ChatColor.GRAY + " You cannot empty your bucket while banished!");
    			event.setCancelled(true);
    		}
    	}
    }
	public void onPlayerChat(PlayerChatEvent event){
		if(NetherBan.mute == true){
			Player player = event.getPlayer();
			if(plugin.playerBanish.containsKey(player)){
				player.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "]" + ChatColor.GRAY + " Your cry falls on deaf ears.");
				event.setCancelled(true);

			}
		}
	}
	public void onPlayerPortal(PlayerPortalEvent event){
		Player player = event.getPlayer();
		if(plugin.playerBanish.containsKey(player)){
			player.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "]" + ChatColor.GRAY + " There is no escape from the Nether! Mwahahahaha!");
			event.setCancelled(true);
		}
	}
	public void onPlayerRespawn(PlayerRespawnEvent event){
		Player player = event.getPlayer();
		if(plugin.playerBanish.containsKey(player)){
			Location loc = plugin.getServer().getWorld(NetherBan.nethername).getSpawnLocation();
			event.setRespawnLocation(loc);
		}
	}
	public static void main(String[] args)throws IOException{

	}
}
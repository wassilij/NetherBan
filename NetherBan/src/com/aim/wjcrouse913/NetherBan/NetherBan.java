package com.aim.wjcrouse913.NetherBan;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.plugin.Plugin;

/* This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details. */

public class NetherBan extends JavaPlugin {
	public static PermissionHandler permissionHandler;
	public static String nethername;
	public static String normalname;
	public static boolean commands;
	public static boolean pvp;
	public static boolean emptybucket;
	public static boolean build;
	public static boolean destroy;
	public static boolean mute;
	public static boolean bedrock;
	public static boolean lava;
	public static boolean tnt;
	public static boolean fire;
	static String mainDirectory = "plugins/NetherBan";
	static File ConfigCreate = new File(mainDirectory + File.separator + "NetherBan.prop");
	static File BanList = new File(mainDirectory + File.separator + "banished.txt");
	static Properties prop = new Properties();
    public Map<Player, Boolean> playerBanish = new HashMap<Player, Boolean>();
	private final nEntityListener entityListener = new nEntityListener(this);
	private final nPlayerListener playerListener = new nPlayerListener(this);
	private final nBlockListener blockListener  = new nBlockListener(this);
	public static final Logger log = Logger.getLogger("Minecraft");
	public void loadProcedure(){
		FileInputStream InFile = null;
		try{
			InFile = new FileInputStream(ConfigCreate);
            prop.load(InFile);
            InFile.close();
            
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		commands = Boolean.parseBoolean(prop.getProperty("Banished-Cant-Use-Commands"));
		nethername = prop.getProperty("Nether-World-Name");
		normalname = prop.getProperty("Normal-World-Name");
		pvp = Boolean.parseBoolean(prop.getProperty("PvP-Disabled-For-Banished"));
		emptybucket = Boolean.parseBoolean(prop.getProperty("Banished-Cant-Empty-Bucket"));
		build = Boolean.parseBoolean(prop.getProperty("Banished-Cant-Build"));
		destroy = Boolean.parseBoolean(prop.getProperty("Banished-Cant-Destroy"));
		mute = Boolean.parseBoolean(prop.getProperty("Mute-on-Ban"));
		bedrock = Boolean.parseBoolean(prop.getProperty("Blacklist-Bedrock"));
		lava = Boolean.parseBoolean(prop.getProperty("Blacklist-Lava"));
		tnt = Boolean.parseBoolean(prop.getProperty("Blacklist-TnT"));
		fire = Boolean.parseBoolean(prop.getProperty("Blacklist-Fire"));
		
	}
	public void onEnable(){
		setupPermissions();
		new File(mainDirectory).mkdir();
		if(!BanList.exists()){
			try{
				BanList.createNewFile();
			}catch(IOException ex){
				ex.printStackTrace();
				
			}
		}
		if(!ConfigCreate.exists()){
			try{
				ConfigCreate.createNewFile();
	            FileOutputStream out = new FileOutputStream(ConfigCreate);
	            prop.put("Banished-Cant-Use-Commands", "true");
	            prop.put("Nether-World-Name", "world_nether");
	            prop.put("Normal-World-Name", "world");
	            prop.put("PvP-Disabled-For-Banished", "true");
	            prop.put("Banished-Cant-Empty-Bucket", "true");
	            prop.put("Banished-Cant-Build", "true");
	            prop.put("Banished-Cant-Destroy", "true");
	            prop.put("Mute-on-Ban", "false");
	            prop.put("Blacklist-Lava", "false");
	            prop.put("Blacklist-TnT", "false");
	            prop.put("Blacklist-Fire", "false");
	            prop.store(out, "NetherBan Config");
	            out.flush();
	            out.close();
			}catch (IOException ex) {
				ex.printStackTrace();
			}
		}else{
			loadProcedure();
		}
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_PORTAL, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_BUCKET_FILL, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Event.Priority.Normal, this);
		log.info("[NetherBan] NetherBan Enabled. Torturing souls...");
	}
	public void onDisable(){
		log.info("[NetherBan] NetherBan Disabled.");
        
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(!label.equalsIgnoreCase("nbkick")){
			toggleBanish(getServer().getPlayer(args[0]));
		}
		if(label.equalsIgnoreCase("nbban")){
			if (NetherBan.permissionHandler.has((Player) sender, "netherban.nbban") || sender.isOp()) {
				return onPlayerBan(sender, args);
			}else{
				sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
				return true;
			}
			
		}
		if(label.equalsIgnoreCase("nbunban")){
			if(NetherBan.permissionHandler.has((Player)sender, "netherban.nbunban") || sender.isOp()){
				return onPlayerUnbanned(sender, args);
			}else{
				sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
				return true;
				
			}
		}
		if(label.equalsIgnoreCase("nbkick")){
			if(NetherBan.permissionHandler.has((Player)sender, "netherban.nbkick") || sender.isOp()){
				return onPlayerKick(sender, args);
			}else{
				sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
				return true;
			}
		}
		return false;
		
	}
	private void toggleBanish(Player player) {
		if(playerBanish.containsKey(player)){
	        if(playerBanish.get(player)){
	            playerBanish.put(player, false);
	        } else {
	            playerBanish.put(player, true);
	            player.teleport(this.getServer().getWorld(normalname).getSpawnLocation());
	            player.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "]" + ChatColor.GRAY + " You have been freed from the Nether!");
	        }
	    } else {
	        playerBanish.put(player, true);
            player.teleport(this.getServer().getWorld(nethername).getSpawnLocation());
            player.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "]" + ChatColor.GRAY + " You have been banished to the Nether!");
            
	    }
	}
	public boolean onPlayerBan(CommandSender sender, String[] args){
		if(args.length < 1){
			sender.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "] " + ChatColor.RED + " Not enough arguments!");
			return false;
		}
		if(args.length > 1){
			sender.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "] " + ChatColor.RED + " Too many arguments!");
			return false;
		}
		if(args.length == 1){
			String p = args[0];
			Player banned = this.getServer().getPlayer(p);
			if(banned instanceof Player){
				try {
					main(null);
					Writer output = null;
					String text = banned.getName();
					File file = new File("plugins/NetherBan/banished.txt");
					output = new BufferedWriter(new FileWriter(file));
					output.write(text);
					output.close();
					System.out.println("[NetherBan] " + banned.getDisplayName() + " was banned to the Nether!");  
				} catch (IOException e) {
					e.printStackTrace();
					
				}
				sender.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "] " + ChatColor.GREEN + banned.getDisplayName() + ChatColor.GRAY + " was banished to the Nether!");
				playerBanish.put(banned, false);
				return true;
				
			}
		}
		return false;
	}
	public boolean onPlayerUnbanned(CommandSender sender, String[] args){
			if(args.length < 1){
				sender.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "] " + ChatColor.RED + "Not enough arguments!");
				return false;
			}
			if(args.length > 1){
				sender.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "] " + ChatColor.RED + "Too many arguments!");
				return false;
			}
			if(args.length == 1){
				String p = args[0];
				Player unbanned = this.getServer().getPlayer(p);
				if(unbanned instanceof Player){
					try {
						BufferedReader in = new BufferedReader(new FileReader("plugins/NetherBan/banished.txt"));
						String str;
						while ((str = in.readLine()) != null) {
							String name = unbanned.getName();
							if(str.equals(name)){
								main(null);
								Writer output = null;
								String text = " ";
								File file = new File("plugins/NetherBan/banished.txt");
								output = new BufferedWriter(new FileWriter(file));
								output.write(text);
								output.close();
								
							}
							
						}
					} catch (IOException e) {
						e.printStackTrace();
						
					}
					playerBanish.remove(unbanned);
					sender.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "] " + ChatColor.GREEN + unbanned.getDisplayName() + ChatColor.GRAY + " unbanished from the Nether!");
					return true;
					
				}
			}
			return false;
	}
	public boolean onPlayerKick(CommandSender sender, String[] args){
		if(args.length < 1){
			sender.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "] " + ChatColor.RED + "Not enough arguments!");
			return false;
		}
		if(args.length > 1){
			sender.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "] " + ChatColor.RED + "Too many arguments!");
			return false;
		}
		if(args.length == 1){
			String p = args[0];
			Player kicked = this.getServer().getPlayer(p);
			if(kicked instanceof Player){
				sender.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "] " + ChatColor.GREEN + kicked.getDisplayName() + ChatColor.GRAY + " kicked to the Nether!");
				kicked.teleport(this.getServer().getWorld(String.valueOf(nethername)).getSpawnLocation());
	        	kicked.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "] " + ChatColor.GRAY + "You have been banished to the Nether!");
	        	playerBanish.remove(kicked);
	        	return true;
			}
			
		}
		return false;
	}
	public static void main(String[] args)throws IOException{
		
	}
	private void setupPermissions() {
	      Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");

	      if (NetherBan.permissionHandler == null) {
	          if (permissionsPlugin != null) {
	              NetherBan.permissionHandler = ((Permissions) permissionsPlugin).getHandler();
	          } else {
	              log.info("[NetherBan] Permission system not detected, reverting to OP only mode!");
	          }
	      }
	  }
}
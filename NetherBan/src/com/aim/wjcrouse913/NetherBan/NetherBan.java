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
	public static String prefix = "[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "]";
	public static String version = "v0.5";
	public static String whitelist;
	public static String nethername;
	public static String normalname;
	public static boolean kickDeath;
	public static boolean death;
	public static boolean target;
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
	public static boolean lightning;
	static String mainDirectory = "plugins/NetherBan";
	static File ConfigCreate = new File(mainDirectory + File.separator + "NetherBan.prop");
	static File Whitelist = new File(mainDirectory + File.separator + "whitelist.txt");
	static File BanList = new File(mainDirectory + File.separator + "banished.txt");
	static Properties prop = new Properties();
    public Map<Player, Boolean> playerSafe = new HashMap<Player, Boolean>();
    public Map<Player, Boolean> playerBanish = new HashMap<Player, Boolean>();
    public PermissionHandler permissionHandler;
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
		kickDeath = Boolean.parseBoolean(prop.getProperty("Kick-on-Death"));
		death = Boolean.parseBoolean(prop.getProperty("Banish-on-Death"));
		target = Boolean.parseBoolean(prop.getProperty("Entities-Target-Banished"));
		lightning = Boolean.parseBoolean(prop.getProperty("Display-Lightning-On-Banish"));
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
		if(!Whitelist.exists()){
			try{
				Whitelist.createNewFile();
			}catch (IOException ex){
				ex.printStackTrace();

			}
		}
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
	            prop.put("Kick-on-Death", "false");
	            prop.put("Banish-on-Death", "false");
	            prop.put("Entities-Target-Banished", "false");
	            prop.put("Display-Lightning-On-Banish", "true");
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
                        loadProcedure();
		}else{
			loadProcedure();
		}
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.ENTITY_TARGET, entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_PORTAL, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_BUCKET_FILL, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Event.Priority.Highest, this);
		log.info("[NetherBan] NetherBan Enabled. Torturing souls...");
	}
	public void onDisable(){
		log.info("[NetherBan] NetherBan Disabled.");
        
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(this.getServer().getWorlds().contains(this.getServer().getWorld(nethername)) && this.getServer().getWorlds().contains(this.getServer().getWorld(normalname))){
			if(label.equalsIgnoreCase("nbhelp")){
				if(sender instanceof Player){
					sender.sendMessage(ChatColor.GRAY + "---------------" + ChatColor.WHITE + "[ " + ChatColor.DARK_RED + "NetherBan " + ChatColor.GREEN + "Help" + ChatColor.WHITE + " ]" + ChatColor.GRAY + "---------------");
					sender.sendMessage(ChatColor.GRAY + "-" + ChatColor.RED + "/nbban <player>" + ChatColor.GRAY + " -- " + ChatColor.GREEN + "Ban a player to the Nether" + ChatColor.GRAY + "------");
					sender.sendMessage(ChatColor.GRAY + "-" + ChatColor.RED + "/nbkick <player>" + ChatColor.GRAY + " - " + ChatColor.GREEN + "Kick a player to the Nether" + ChatColor.GRAY + "-------");
					sender.sendMessage(ChatColor.GRAY + "-" + ChatColor.RED + "/nbunban <player>" + ChatColor.GRAY + "-" + ChatColor.GREEN + "Unban player from the Nether" + ChatColor.GRAY + "----");
					sender.sendMessage(ChatColor.GRAY + "-" + ChatColor.RED + "/nbwl <player>" + ChatColor.GRAY + " -- " + ChatColor.GREEN + "Makes a player unbanishable" + ChatColor.GRAY + "------");
					sender.sendMessage(ChatColor.GRAY + "-" + ChatColor.RED + "/nbversion" + ChatColor.GRAY + " -- " + ChatColor.GREEN + "Shows the NetherBan Version!" + ChatColor.GRAY + "---------");
					sender.sendMessage(ChatColor.GRAY + "-" + ChatColor.RED + "/nbhelp" + ChatColor.GRAY + " -- " + ChatColor.GREEN + "Displays this message!" + ChatColor.GRAY + "------------------");
					sender.sendMessage(ChatColor.GRAY + "------------------------------------------------");
					return true;
				}else{
					sender.sendMessage("---------------[ NetherBan Help ]---------------");
					sender.sendMessage("-/nbban <player> -- Ban a Player to the Nether--");
					sender.sendMessage("-/nbkick <player> - Kick a Player to the Nether-");
					sender.sendMessage("-/nbunban <player>-Unban Player from ze Nether--");
					sender.sendMessage("-/nbwl <player> -- Make a player unbanishable---");
					sender.sendMessage("-/nbversion -- Shows NetherBan Version!---------");
					sender.sendMessage("-/nbhelp -- Displays this message!--------------");
					sender.sendMessage("------------------------------------------------");
					return true;
				}
			}
			if(label.equalsIgnoreCase("nbversion")){
				if(sender instanceof Player){
					sender.sendMessage(prefix + ChatColor.GRAY + " This server is running NetherBan " + ChatColor.GREEN + version);
					return true;
				}else{
					sender.sendMessage("[NetherBan] " + this.getServer().getIp() + ChatColor.DARK_RED + "NetherBan" + ChatColor.GRAY + " is running NetherBan " + version);
					return true;
				}
			}
			Plugin permissions = this.getServer().getPluginManager().getPlugin("Permissions");
			if(label.equalsIgnoreCase("nbban") || label.equalsIgnoreCase("netherban")){
				if(permissions != null){
					if(sender instanceof Player){
						if(!(this.permissionHandler.has((Player) sender, "netherban.nbban"))){
							sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
							return true;
						}else{
							return onPlayerBan(sender, args);
						}
					}else{
						return onPlayerBan(sender, args);
					}
				}else{
					if(!sender.isOp()){
						sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
						return true;
					}else{
						return onPlayerBan(sender, args);
					}
				}
			}
			if(label.equalsIgnoreCase("nbunban")){
				if(permissions != null){
					if(sender instanceof Player){
						if(!(this.permissionHandler.has((Player) sender, "netherban.nbunban"))){
							sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
							return true;
						}else{
							return onPlayerUnbanned(sender, args);
						}
					}else{
						return onPlayerUnbanned(sender, args);
					}
				}else{
					if(!sender.isOp()){
						sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
						return true;
					}else{
						return onPlayerUnbanned(sender, args);
					}
				}
			}
			if(label.equalsIgnoreCase("nbkick")){
				if(permissions != null){
					if(sender instanceof Player){
						if(!(this.permissionHandler.has((Player)sender, "netherban.nbkick"))){
							sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
							return true;
						}else{
							return onPlayerKick(sender, args);
						}
					}else{
						return onPlayerKick(sender, args);
					}
				}else{
					if(!sender.isOp()){
						sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
						return true;
					}else{
						return onPlayerKick(sender, args);
					}
				}
			}
			if(label.equalsIgnoreCase("nbwl") || label.equalsIgnoreCase("nbwhitelist")){
				if(permissions != null){
					if(sender instanceof Player){
						if(!(this.permissionHandler.has((Player)sender, "netherban.whitelist"))){
							sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
							return true;
						}else{
							return onWhitelist(sender, args);
						}
					}else{
						return onWhitelist(sender, args);
					}
				}else{
					if(!sender.isOp()){
						sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
						return true;
					}else{
						return onWhitelist(sender, args);
					}
				}
			}
		}else{
			sender.sendMessage(prefix + ChatColor.RED + " Error: Your NetherBan.prop is not configured properly!");
			return true;
		}
		return false;
	}
	public boolean onWhitelist(CommandSender sender, String[] args){
		if(args.length < 1){
			sender.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "] " + ChatColor.RED + " Not enough arguments!");
			return false;
		}
		if(args.length > 1){
			sender.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "] " + ChatColor.RED + " Not enough arguments!");
			return false;
		}
		if(args.length == 1){
			String p = args[0];
			Player safe = this.getServer().getPlayer(p);
			if(safe instanceof Player){
				try{
					main(null);
					Writer output = null;
					String text = safe.getName();
					File file = new File("plugins/NetherBan/whitelist.txt");
					output = new BufferedWriter(new FileWriter(file));
					output.write(text);
					output.close();
				}catch (IOException e){
					e.printStackTrace();
					return true;

				}
				playerSafe.put(safe, false);
				safe.sendMessage(prefix + ChatColor.GRAY + " You have been added to the Whitelist!");
				sender.sendMessage(prefix + " " + ChatColor.GREEN + safe.getDisplayName() + ChatColor.GRAY + " added to the Whitelist!");
				return true;
			}
		}
		return false;
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
			if(!playerSafe.containsKey(banned)){
				if(banned instanceof Player){
					try {
						main(null);
						Writer output = null;
						String text = banned.getName();
						File file = new File("plugins/NetherBan/banished.txt");
						output = new BufferedWriter(new FileWriter(file));
						output.write(text);
						output.close();
						System.out.println("[NetherBan] " + banned.getDisplayName() + " was banished to the Nether!");
						if(lightning == true){
							banned.getWorld().strikeLightning(banned.getLocation());
						}
						banned.teleport(this.getServer().getWorld(nethername).getSpawnLocation());
						banned.sendMessage(prefix + ChatColor.GRAY + " You have been banished to the Nether!");
						sender.sendMessage(prefix + " " + ChatColor.GREEN + banned.getDisplayName() + ChatColor.GRAY + " has been banished to the Nether!");
						playerBanish.put(banned, false);
						return true;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					try {
						main(null);
						Writer output = null;
						String text = p;
						File file = new File("plugins/NetherBan/banished.txt");
						output = new BufferedWriter(new FileWriter(file));
						output.write(text);
						output.close();
						System.out.println("[NetherBan] " + p + " was banished to the Nether!");
						sender.sendMessage(prefix + " " + ChatColor.GREEN + p + ChatColor.GRAY + " has been banished to the Nether!");
						return true;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}else{
				sender.sendMessage(prefix + " " + ChatColor.GREEN + banned.getDisplayName() + ChatColor.GRAY + " cannot be banished to the Nether!" );
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
								playerBanish.remove(unbanned);
								unbanned.teleport(this.getServer().getWorld(normalname).getSpawnLocation());
								sender.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "] " + ChatColor.GREEN + unbanned.getDisplayName() + ChatColor.GRAY + " unbanished from the Nether!");
								unbanned.sendMessage(prefix + ChatColor.GRAY + " You have been freed from the Nether!");
								return true;
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					try {
						BufferedReader in = new BufferedReader(new FileReader("plugins/NetherBan/banished.txt"));
						String str;
						while ((str = in.readLine()) != null) {
							String name = p;
							if(str.equals(name)){
								main(null);
								Writer output = null;
								String text = " ";
								File file = new File("plugins/NetherBan/banished.txt");
								output = new BufferedWriter(new FileWriter(file));
								output.write(text);
								output.close();
								sender.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "] " + ChatColor.GREEN + p + ChatColor.GRAY + " is offline but was unbanished from the Nether! Reload server for it to take effect!");
								return true;
							}else{
								sender.sendMessage(prefix + " " + ChatColor.GREEN + p + ChatColor.GRAY + " is not banished!");
								return true;
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
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
	        	kicked.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "] " + ChatColor.GRAY + "You have been kicked to the Nether!");
	        	kicked.teleport(this.getServer().getWorld(nethername).getSpawnLocation());
	        	return true;
			}

		}
		return false;
	}
	public static void main(String[] args)throws IOException{

	}
	private void setupPermissions() {
	      Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");

	      if (this.permissionHandler == null) {
	          if (permissionsPlugin != null) {
	              this.permissionHandler = ((Permissions) permissionsPlugin).getHandler();
	          } else {
	              log.info("[NetherBan] Permission system not detected, defaulting to OP");

	          }
	      }
	}
}
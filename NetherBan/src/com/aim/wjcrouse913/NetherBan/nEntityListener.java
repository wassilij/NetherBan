package com.aim.wjcrouse913.NetherBan;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

public class nEntityListener extends EntityListener{
	public static NetherBan plugin;

	public nEntityListener(NetherBan instance){
		plugin = instance;

	}
	public void onEntityDamage(EntityDamageEvent event){
		if(NetherBan.pvp == true){
			if(event.getEntity() instanceof Player){
				if(event instanceof EntityDamageByEntityEvent){
					EntityDamageByEntityEvent pvp = (EntityDamageByEntityEvent) event;
					if(pvp.getDamager() instanceof Player){
						Player attacker = (Player) pvp.getDamager();
						if(plugin.playerBanish.containsKey(attacker)){
							attacker.sendMessage("[" + ChatColor.DARK_RED + "NetherBan" + ChatColor.WHITE + "]" + ChatColor.GRAY + " You can't attack people while banished to the Nether!");
							event.setCancelled(true);

						}
					}
				}
			}
		}
	}
}
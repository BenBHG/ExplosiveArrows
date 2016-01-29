package com.darkbytelabs.explosivearrows;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class ExplosiveArrows extends JavaPlugin implements Listener {
	
	float power;
	boolean setFire;
	boolean breakBlocks;
	
	@Override
	public void onEnable() {
		FileConfiguration config;
		ItemStack explosiveArrow;
		ItemMeta itemMeta;
		ShapedRecipe recipe;
		
		config = getConfig();
		power = (float)config.getDouble("power");
		setFire = config.getBoolean("set-fire");
		breakBlocks = config.getBoolean("break-blocks");
		
		saveDefaultConfig();
		
		explosiveArrow = new ItemStack(Material.ARROW);
		itemMeta = explosiveArrow.getItemMeta();
		itemMeta.setLore(Arrays.asList("Explosive"));
		explosiveArrow.setItemMeta(itemMeta);
		
		recipe = new ShapedRecipe(explosiveArrow);
		recipe.shape("0G0", "0S0", "0F0");
		recipe.setIngredient('G', Material.SULPHUR);
		recipe.setIngredient('S', Material.STICK);
		recipe.setIngredient('F', Material.FEATHER);
		getServer().addRecipe(recipe);
		
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityShootBow(EntityShootBowEvent event) {
		Entity projectile;
		Entity entity;
		PlayerInventory inventory;
		int index;
		ItemStack itemStack;
		ItemMeta itemMeta;
		
		entity = event.getEntity();
		if (entity instanceof Player) {
			inventory = ((Player)entity).getInventory();
			index = inventory.first(Material.ARROW);
			
			if (index > 0) {
				itemStack = inventory.getItem(index);
				itemMeta = itemStack.getItemMeta();
				if (itemMeta.hasLore() && itemMeta.getLore().contains("Explosive")) {
					projectile = event.getProjectile();
					projectile.setMetadata("explosive", new FixedMetadataValue(this, 1));
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onProjectileHit(ProjectileHitEvent event) {
		Entity projectile;
		Location location;

		projectile = event.getEntity();
		if (projectile instanceof Arrow) {
			if (projectile.hasMetadata("explosive")) {
				location = projectile.getLocation();
				projectile.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), power, setFire, breakBlocks);
				projectile.remove();
				
				
			}
		}
	}

}

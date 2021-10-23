package ru.universalstudio.universaljump;

import java.util.*;
import org.bukkit.*;
import java.util.stream.*;
import org.bukkit.event.*;
import org.bukkit.util.Vector;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.event.inventory.*;
import org.bukkit.configuration.file.*;

/**
 * @Author source code: NaulbiMIX
 * @Author plugin code: UniversalStudio
 */

public class UniversalJump extends JavaPlugin implements Listener {

  private FileConfiguration config;
  private ItemStack rod;
  private Sound sound;
  private int slot;
  
  public void onEnable() {
    this.config = Config.getFile("config.yml");
    this.slot = this.config.getInt("fish.slot");
    if (this.config.getBoolean("fish.enable")) {
      this.rod = new ItemStack(Material.FISHING_ROD);
      ItemMeta itemMeta = this.rod.getItemMeta();
      itemMeta.setDisplayName(color(this.config.getString("fish.title")));
      itemMeta.setLore(color(this.config.getStringList("fish.lore")));
      itemMeta.setUnbreakable(true);
      this.rod.setItemMeta(itemMeta);
      try {
        this.sound = Sound.valueOf(this.config.getString("fish.sound").toUpperCase());
      } catch (Exception e) {
        getLogger().info("Звук для удочки: " + this.config.getString("fish.sound").toUpperCase());
      } 
    } else {
      try {
        this.sound = Sound.valueOf(this.config.getString("jump.sound").toUpperCase());
      } catch (Exception e) {
        getLogger().info("Звук для прыжка: " + this.config.getString("jump.sound").toUpperCase());
      } 
    }
    getServer().getPluginManager().registerEvents(this, this);
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[UniversalJump] The plugin started successfully! Website: u-studio.su");
  }

  // Events
  
  @EventHandler
  public void onClick(InventoryClickEvent e) {
    if (this.rod == null) {
      return;
    }
    ItemStack itemStack = e.getCurrentItem();
    if (isRod(itemStack)) {
      e.setCancelled(true);
    }
  }
  
  @EventHandler
  public void onDrop(PlayerDropItemEvent e) {
    if (isRod(e.getItemDrop().getItemStack())) {
      e.setCancelled(true);
    }
  }
  
  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    if (this.rod != null) {
      e.getPlayer().getInventory().setItem(this.slot, this.rod);
    } else {
      if (e.getPlayer().getInventory().getItem(this.slot) != null) {
        e.getPlayer().getInventory().setItem(this.slot, null);
      }
      e.getPlayer().setAllowFlight(true);
    } 
  }
  
  @EventHandler
  public void onFlight(PlayerToggleFlightEvent e) {
    if (this.rod != null)
      return; 
    e.setCancelled(true);
    
    e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().add(new Vector(0.0D, 0.5D, 0.0D)).multiply(1.5D));
    e.getPlayer().setAllowFlight(false);
    
    Bukkit.getScheduler().runTaskLater(this, new Runnable() {
      @Override
      public void run() {
        e.getPlayer().setAllowFlight(true);
      }
    }, 30L);
    
    if (this.sound != null) {
      e.getPlayer().playSound(e.getPlayer().getLocation(), this.sound, 10.0F, 1.0F);
    }
  }

  @EventHandler
  public void onFish(PlayerFishEvent e) {
    if (this.rod == null || e.getState() != PlayerFishEvent.State.IN_GROUND) {
      return;
    }

    Location loc = e.getPlayer().getLocation();
    e.getPlayer().setVelocity(loc.getDirection().multiply(3).add(new Vector(0, 1, 0)));

    if (this.sound != null) {
      e.getPlayer().playSound(e.getPlayer().getLocation(), this.sound, 10.0F, 1.0F);
    }
  }

  // Other

  private String color(String paramString) {
    return ChatColor.translateAlternateColorCodes('&', paramString);
  }

  private List<String> color(List<String> list) {
    return list.stream().map(this::s).collect(Collectors.toList());
  }

  private String s(String s) { return color(s); }

  private boolean isRod(ItemStack item) {
    if (this.rod != null && item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
      return item.getItemMeta().getDisplayName().equalsIgnoreCase(this.rod.getItemMeta().getDisplayName());
    }
    return false;
  }

}

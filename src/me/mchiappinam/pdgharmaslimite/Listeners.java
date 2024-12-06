package me.mchiappinam.pdgharmaslimite;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Listeners implements Listener {
    Random random = new Random();

	private Main plugin;
	public Listeners(Main main) {
		plugin=main;
	}
	
	@EventHandler
	public void onEntityCombust(EntityCombustEvent e) {
		if(e.getEntity() instanceof Zombie) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if(plugin.precisaTomarAntibiotic(e.getEntity().getPlayer()))
			plugin.hitadoPeloZombie.remove(e.getEntity().getPlayer().getName().toLowerCase());
	}
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		if (((e.getDamager() instanceof Player)) && ((e.getEntity() instanceof Monster))) {
			final Monster m = (Monster)e.getEntity();
			m.setVelocity(new Vector());
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					m.setVelocity(new Vector());
				}
			}, 1L);
		}else if (((e.getDamager() instanceof Zombie)) && ((e.getEntity() instanceof Player))) {
			Player p = (Player)e.getEntity();
			if(!plugin.temEfeitoAntibiotic(p))
				plugin.addHitZombie(p);
		}else if (((e.getDamager() instanceof Giant)) && ((e.getEntity() instanceof Player))) {
			Player p = (Player)e.getEntity();
			if(!plugin.temEfeitoAntibiotic(p))
				plugin.addHitGiant(p);
		}
	}
	
	@EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(e.getAction().equals(Action.RIGHT_CLICK_AIR)||e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
        	Player p = e.getPlayer();
    		if((p.getItemInHand() != null)) {
    			if(p.getItemInHand().hasItemMeta()) {
		            String name = p.getItemInHand().getItemMeta().getDisplayName();
		            if(name != null)
			        	if(name.contains(Main.itemAntibiotic.getItemMeta().getDisplayName())) {
				    		e.setCancelled(true);
				    		if(plugin.temEfeitoAntibiotic(p)) {
				    			p.sendMessage("§cVocê já está com efeito de antibiótico.");
				    			return;
				    		}
				    		if(!plugin.precisaTomarAntibiotic(p)) {
				    			p.sendMessage("§cVocê não está ferido!");
				    			return;
				    		}
				    		plugin.tomarAntibiotic(p);
				    		if(p.getItemInHand().getAmount()==1)
				    			p.setItemInHand(new ItemStack(Material.AIR));
				    		else
				    			p.getItemInHand().setAmount(p.getItemInHand().getAmount()-1);
				    		p.updateInventory();
			        	}
    			}
    		}
        }
	}
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player)e.getWhoClicked();
	    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	    	public void run() {
	    		if(p!=null)
	    		plugin.renameItens(p);
	    	}
	    }, 1L);
	}
	
	@EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
		Player p = (Player)e.getPlayer();
	    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	    	public void run() {
	    		if(p!=null)
	    		plugin.renameItens(p);
	    	}
	    }, 1L);
	}
	
	@EventHandler
    public void onItemPick(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
	    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	    	public void run() {
	    		if(p!=null)
	    		plugin.renameItens(p);
	    	}
	    }, 1L);
	}

	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
	  	if (isAllowedToSpawn(event.getEntity())) return;
	    Location loc = event.getLocation();
	    int count = random.nextInt(3) + 1;
	    for (int i = 0; i < count; i++) {
	    	loc.getWorld().spawn(getRandomLocation(loc, 5, 0), Zombie.class);
	    }
	    event.setCancelled(true);
	}

	  private Location getRandomLocation(Location loc, int radius, int testCount) {
	    for (int i = 0; i < 10; i++) {
	      Location newLoc = new Location(loc.getWorld(), loc.getX() + (this.random.nextInt(radius * 2) - radius), 
	        loc.getY() + (this.random.nextInt(radius * 2) - radius), 
	        loc.getZ() + (this.random.nextInt(radius * 2) - radius), loc.getPitch(), loc.getYaw());
	      if ((newLoc.getBlock().getType() == Material.AIR) && 
	        (newLoc.getBlock().getRelative(BlockFace.UP).getType() == Material.AIR) && 
	        (newLoc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR))
	        return newLoc;
	    }
	    return loc;
	  }

	private boolean isAllowedToSpawn(LivingEntity entity) {
		for (String mobStr : plugin.mobsPermitidos) {
			if (entity.getType().name().equalsIgnoreCase(mobStr))
				return true;
		}
		return false;
	}
}

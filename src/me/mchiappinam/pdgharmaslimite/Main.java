package me.mchiappinam.pdgharmaslimite;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

public class Main extends JavaPlugin {
	protected HashMap<String, Integer> efeitoAntibiotic = new HashMap<String, Integer>();
	protected List<String> hitadoPeloZombie=new ArrayList<String>();
	protected List<String> mobsPermitidos=new ArrayList<String>();
	public static ItemStack itemAntibiotic = null;

	public void onEnable() {
		getServer().getConsoleSender().sendMessage("§3[PDGHArmasLimite] §2iniciando...");
		File file = new File(getDataFolder(),"config.yml");
		getServer().getConsoleSender().sendMessage("§3[PDGHArmasLimite] §2verificando se a config existe...");
		if(!file.exists()) {
			try {
				getServer().getConsoleSender().sendMessage("§3[PDGHArmasLimite] §2config inexistente, criando config...");
				saveResource("config_template.yml",false);
				File file2 = new File(getDataFolder(),"config_template.yml");
				file2.renameTo(new File(getDataFolder(),"config.yml"));
				getServer().getConsoleSender().sendMessage("§3[PDGHArmasLimite] §2config criada");
			}catch(Exception e) {getServer().getConsoleSender().sendMessage("§c[PDGHArmasLimite] §cERRO AO CRIAR CONFIG");}
		}
		taskTime();
		timerAntibiotic();
		loadItens();PROJETO PARADO NA METADE MIGRADO PARA PDGHArmas
		mobsPermitidos.add("SKELETON");
		getServer().getConsoleSender().sendMessage("§3[PDGHArmasLimite] §2registrando eventos...");
		getServer().getPluginManager().registerEvents(new Listeners(this), this);
		getServer().getConsoleSender().sendMessage("§3[PDGHArmasLimite] §2eventos registrados");
		getServer().getConsoleSender().sendMessage("§3[PDGHArmasLimite] §2ativado - Developed by mchiappinam");
		getServer().getConsoleSender().sendMessage("§3[PDGHArmasLimite] §2Acesse: http://pdgh.com.br/");
		getServer().getConsoleSender().sendMessage("§3[PDGHArmasLimite] §2Acesse: https://hostload.com.br/");
	}
	    
	public void onDisable() {
		getServer().getConsoleSender().sendMessage("§3[PDGHArmasLimite] §2desativado - Developed by mchiappinam");
		getServer().getConsoleSender().sendMessage("§3[PDGHArmasLimite] §2Acesse: http://pdgh.com.br/");
		getServer().getConsoleSender().sendMessage("§3[PDGHArmasLimite] §2Acesse: https://hostload.com.br/");
	}
    
    public void renameItens(Player p) {
        Inventory inv = (Inventory)p.getInventory();
        ItemStack[] items = inv.getContents();
        for (int i = 0; i < items.length; ++i) {
        	if(items[i]!=null)
	            if (items[i].getTypeId() == itemAntibiotic.getTypeId()) {
	        		String name = null;
	            	if(!hasName(items[i]))
	            		name=itemAntibiotic.getItemMeta().getDisplayName();
	                if (name != null && name.length() > 0) {
	                    setName(items[i], name, itemAntibiotic.getItemMeta().getLore());
	                }
	            }
        }
    }
    
    public boolean hasName(ItemStack item) {
    	if(item.hasItemMeta())
    		if(item.getItemMeta().hasDisplayName())
    			return true;
    	return false;
    }
    
    public ItemStack setName(ItemStack item, String name, List<String> lore) {
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
	    im.setLore(lore);
        item.setItemMeta(im);
        return item;
    }
	
	private void loadItens() {
		ItemStack a0 = new ItemStack(Material.getMaterial(getConfig().getInt("itemAntibiotic.id")));
		
		List<String> ll0 = getConfig().getStringList("itemAntibiotic.lore");
		List<String> l0 = new ArrayList<String>();
		for (String string : ll0) {
		l0.add(string.replace("&", "§"));
		}
		
	    ItemMeta b0 = a0.getItemMeta();
	    b0.setDisplayName(getConfig().getString("itemAntibiotic.nome").replaceAll("&", "§"));
	    b0.setLore(l0);
	    a0.setItemMeta(b0);
	    itemAntibiotic=a0;
	}
	
	public void tomarAntibiotic(Player p) {
		hitadoPeloZombie.remove(p.getName().toLowerCase());
		efeitoAntibiotic.put(p.getName().toLowerCase(), 10);
		//sendActionText(p, "§e§lVocê tomou antibiótico e se curou!");
	}
	
	private void timerAntibiotic() {
	  	getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
	  		public void run() {
	  			if(!efeitoAntibiotic.isEmpty()) {
	  				final List<String> l = new ArrayList<String>();
		  			for(String p : efeitoAntibiotic.keySet()) {
		  				if(getServer().getPlayerExact(p)==null) {
		  					l.add(p);
		  				}else{
			  				int i=efeitoAntibiotic.get(p)-1;
			  				efeitoAntibiotic.put(p, i);
			  				sendActionText(getServer().getPlayerExact(p), "§e§lVocê tomou antibiótico e se curou! ("+i+"s)");
			  				if(efeitoAntibiotic.get(p)<0) {
			  					l.add(p);
				  				sendActionText(getServer().getPlayerExact(p), "§e§lO efeito de seu antibiótico acabou!");
			  				}
		  				}
		  			}
		  		  	for(String s : l)
		  		  		efeitoAntibiotic.remove(s);
	  			}
	  		}
	  	}, 20, 20);
	  	getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
	  		public void run() {
	  			if(!hitadoPeloZombie.isEmpty()) {
	  				for(String p : hitadoPeloZombie) {
	  					if(getServer().getPlayerExact(p)!=null) {
	  						Player pla = getServer().getPlayerExact(p);
	  						pla.damage(2.0);
	  						pla.getWorld().playEffect(pla.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
	  						sendActionText(pla, "§c§lUm Zombie te atacou! Procure um antibiótico urgente!");
	  					}
	  				}
	  			}
	  		}
	  	}, 60, 60);
	}
	
	public boolean temEfeitoAntibiotic(Player p) {
		if(efeitoAntibiotic.containsKey(p.getName().toLowerCase()))
			return true;
		return false;
	}
	
	public boolean precisaTomarAntibiotic(Player p) {
		if(hitadoPeloZombie.contains(p.getName().toLowerCase()))
			return true;
		return false;
	}

	public void sendActionText(Player p, String message){
    	PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte)2);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }
	
	public void addHitZombie(Player p) {
    	Random randomgen = new Random();
    	int i = randomgen.nextInt(7) + 1;
    	if(i == 1)
			if(!hitadoPeloZombie.contains(p.getName().toLowerCase())) {
				hitadoPeloZombie.add(p.getName().toLowerCase());
				sendActionText(p, "§c§lUm Zombie te atacou! Procure um antibiótico urgente!");
			}
	}
	
	public void addHitGiant(Player p) {
    	if(!hitadoPeloZombie.contains(p.getName().toLowerCase())) {
			hitadoPeloZombie.add(p.getName().toLowerCase());
			sendActionText(p, "§c§lUm Zombie gigante te atacou! Procure um antibiótico urgente!");
		}
	}
	
	public void taskTime() {
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				Calendar cal = Calendar.getInstance();
			    cal.getTime();
			    int hour = cal.get(11);
			    int minute = cal.get(12);
			    int minutes = hour * 60 + minute;
			    int seconds = minutes * 60 + cal.get(13);
			    int newtick = seconds * 20 / 72;
			    for (World w : getServer().getWorlds()) {
			    	int realtime = (newtick - 6000) % 24000;
			        if (realtime < 0) {
			        	realtime += 24000;
			        }
			        w.setFullTime(realtime);
			    }
			}
		}, 10, 10);
	}
}

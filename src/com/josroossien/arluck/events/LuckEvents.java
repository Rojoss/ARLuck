package com.josroossien.arluck.events;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.josroossien.arluck.ARLuck;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class LuckEvents implements Listener {

	private ARLuck arl;
	private Random random = new Random();
	private String prefix = "&8[&2Luck&8] &a";
	
	public LuckEvents(ARLuck arl) {
		this.arl = arl;
	}
	
	
	
	@EventHandler
	private void BlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		//Mine stone : Haste for 10 seconds.
		if (block.getType() == Material.STONE) {
			if (!player.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
				if (arl.luck.checkChance(player, 0.005f, 0.1f)) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 100, 5));
					player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.1f, 0.6f);
					player.sendMessage(CWUtil.integrateColor(prefix + "Haste!"));
                    ParticleEffect.SPELL_WITCH.display(0.5f, 1.0f, 0.5f, 0.01f, 50, player.getLocation());
				}
			}
			return;
		}
		
		//Mine spawner : Get spawner.
		if (block.getType() == Material.MOB_SPAWNER) {
			if (arl.luck.checkChance(player, 0.005f, 0.1f)) {
                if (!block.hasMetadata("CustomSpawner")) {
                    CreatureSpawner spawner = (CreatureSpawner) block.getState();
                    switch (spawner.getSpawnedType()) {
                        case ZOMBIE:
                        case SKELETON:
                        case CAVE_SPIDER:
                        case SPIDER:
                        case PIG:
                            player.getWorld().playSound(block.getLocation(), Sound.LEVEL_UP, 2.0f, 2.0f);
                            player.getWorld().dropItemNaturally(block.getLocation(), arl.getSpawnerItem(spawner.getSpawnedType().toString().toLowerCase().replace("_", "")));
                            player.sendMessage(CWUtil.integrateColor(prefix + "You got extremely lucky and got the spawner!!!!"));
                            ParticleEffect.FLAME.display(0.5f, 0.5f, 0.5f, 0.001f, 100, block.getLocation().add(0.5f, 0, 0.5f));
                            break;
                        default:
                            break;
                    }
                }
			}
			return;
		}
				
		//Break log : Break entire tree
		if (block.getType() == Material.LOG || block.getType() == Material.LOG_2) {
			if (arl.luck.checkChance(player, 0.02f, 0.5f)) {
				if (arl.luck.breakTree(player, block)) {
					player.getWorld().playSound(block.getLocation(), Sound.ZOMBIE_WOODBREAK, 0.2f, 0.0f);
					player.sendMessage(CWUtil.integrateColor(prefix + "Tree felled!"));
				}
			}
			return;
		}
		
		//Break Diamond ore : get extra diamond
		if (block.getType() == Material.DIAMOND_ORE) {
            if (player.getItemInHand() != null && !player.getItemInHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
                if (arl.luck.checkChance(player, 0.01f, 0.1f)) {
                    player.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.DIAMOND, 1));
                    player.sendMessage(CWUtil.integrateColor(prefix + "You found a extra diamond!"));
                    ParticleEffect.CRIT_MAGIC.display(1.0f, 1.0f, 1.0f, 0.1f, 50, block.getLocation().add(0.5f, 0, 0.5f));
                    ParticleEffect.ENCHANTMENT_TABLE.display(0.8f, 0.8f, 0.8f, 0.0001f, 100, block.getLocation().add(0.5f, 0.5f, 0.5f));
                }
            }
			return;
		}
		
		//Break Emerald ore : get extra emerald
		if (block.getType() == Material.EMERALD_ORE) {
            if (player.getItemInHand() != null && !player.getItemInHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
                if (arl.luck.checkChance(player, 0.01f, 0.2f)) {
                    player.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.EMERALD, 1));
                    player.sendMessage(CWUtil.integrateColor(prefix + "You found a extra emerald! More luck!"));
                    ParticleEffect.VILLAGER_HAPPY.display(0.6f, 0.6f, 0.6f, 0.001f, 50, block.getLocation().add(0.5f, 0, 0.5f));
                }
            }
			return;
		}
	}


	@EventHandler
	public void Interact(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			//Place crops : Insta growth
			if (player.getItemInHand().getType() == Material.SEEDS) {
				Block block = event.getClickedBlock();
				if (block.getType() == Material.SOIL) {
					if (arl.luck.checkChance(player, 0.01f, 0.5f)) {
						Block b = player.getWorld().getBlockAt(block.getLocation().add(0, 1, 0));
						b.setType(Material.CROPS);
						
						if (b.getType().equals(Material.CROPS)){
							Crops c = new Crops(CropState.RIPE);
							BlockState bs = b.getState();
							bs.setData(c);
							bs.update();
						}
						player.playSound(block.getLocation(), Sound.DIG_GRASS, 1.0f, 0.8f);
						player.sendMessage(CWUtil.integrateColor(prefix + "Insta growth!"));
                        ParticleEffect.DRIP_WATER.display(0.3f, 0.8f, 0.3f, 1.0f, 30, b.getLocation().add(0.5f, -0.5f, 0.5f));
					}
				}
			}
		}
	}
	
	
	@EventHandler
	public void Eat(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		//Eat food : Get full hunger
		if (arl.luck.checkChance(player, 0.01f, 0.2f)) {
			if (player.getFoodLevel() < 20) {
				player.setFoodLevel(20);
				player.getWorld().playSound(player.getLocation(), Sound.BURP, 1.0f, 1.2f);
				player.sendMessage(CWUtil.integrateColor(prefix + "Full hunger!"));
			}
		}
	}
	
	//Bukkit doesn't support this :/
	//https://bukkit.atlassian.net/browse/BUKKIT-5428
	/*
	@EventHandler
	public void Fish(PlayerFishEvent event) {
		//Throw hook : Insta bait
		Player player = event.getPlayer();
		if (event.getState() == State.FISHING) {
			if (cwu.luck.checkChance(0.02f)) {
				event.getHook().setBiteChance(0.9f);
				player.sendMessage(Utils.integrateColor(prefix + "Bait!"));
			}
			return;
		}
	}
	*/
	
	
	@EventHandler
	public void Sprint(PlayerToggleSprintEvent event) {
		//Toggle sprinting : Get speed
		Player player = event.getPlayer();
		if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
			if (arl.luck.checkChance(player, 0.002f, 0.05f)) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1));
				player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.1f, 0.6f);
				player.sendMessage(CWUtil.integrateColor(prefix + "Speed!"));
                ParticleEffect.VILLAGER_HAPPY.display(0.5f, 1.0f, 0.5f, 1.0f, 20, player.getLocation());
			}
		}
	}
	
	
	@EventHandler
	public void ItemBreak(PlayerItemBreakEvent event) {
		//Break item : Get item it's made of back.
		Player player = event.getPlayer();
		if (arl.luck.checkChance(player, 0.01f, 0.2f)) {
			Material item = event.getBrokenItem().getType();
			Material mat = Material.STONE;
			if (item == Material.DIAMOND_AXE || item == Material.DIAMOND_BOOTS || item == Material.DIAMOND_LEGGINGS || item == Material.DIAMOND_CHESTPLATE || 
					item == Material.DIAMOND_HELMET || item == Material.DIAMOND_HOE || item == Material.DIAMOND_PICKAXE || item == Material.DIAMOND_SPADE || item == Material.DIAMOND_SWORD) {
				mat = Material.DIAMOND;
			} else if (item == Material.IRON_AXE || item == Material.IRON_BOOTS || item == Material.IRON_LEGGINGS || item == Material.IRON_CHESTPLATE || 
					item == Material.IRON_HELMET || item == Material.IRON_HOE || item == Material.IRON_PICKAXE || item == Material.IRON_SPADE || item == Material.IRON_SWORD) {
				mat = Material.IRON_INGOT;
			} else if (item == Material.GOLD_AXE || item == Material.GOLD_BOOTS || item == Material.GOLD_LEGGINGS || item == Material.GOLD_CHESTPLATE || 
					item == Material.GOLD_HELMET || item == Material.GOLD_HOE || item == Material.GOLD_PICKAXE || item == Material.GOLD_SPADE || item == Material.GOLD_SWORD) {
				mat = Material.GOLD_INGOT;
			}
			player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.5f, 1.5f);
			player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(mat, 1));
			player.sendMessage(CWUtil.integrateColor(prefix + "Item recovery!"));
		}
	}
	
	
	@EventHandler
	public void AnimalBreed(CreatureSpawnEvent event) {
		//Breed animal : Spawn grown up animal instead of baby.
		if (event.getSpawnReason() == SpawnReason.BREEDING) {
			Entity entity = event.getEntity();
			if (entity instanceof Ageable) {
				float chance = random.nextFloat();
				if (chance <= 0.08f) {
					entity.getWorld().playSound(entity.getLocation(), Sound.ANVIL_LAND, 0.5f, 2f);
					((Ageable)entity).setAdult();
                    ParticleEffect.SPELL_WITCH.display(1.0f, 1.0f, 1.0f, 1.0f, 50, entity.getLocation());
				}
			}
		}
	}

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        float chance = random.nextFloat();
        if (arl.luck.checkChance(player, 0.02f, 0.4f)) {
            event.setDroppedExp(0);
            event.setKeepLevel(true);
            player.getWorld().playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 2f);
            player.sendMessage(CWUtil.integrateColor(prefix + "No XP lost!"));
            ParticleEffect.VILLAGER_HAPPY.display(1.0f, 1.0f, 1.0f, 1.0f, 50, player.getLocation());
        }
    }
	
	@EventHandler
	public void kill(EntityDeathEvent event) {
		//Get mob eggs from killing mobs used to craft spawners
		Entity e = event.getEntity();
		if (!(e instanceof LivingEntity)) {
			return;
		}
		LivingEntity entity = (LivingEntity)e;
		if (entity.getKiller() != null && entity.getKiller().isOnline()) {
			Player killer = entity.getKiller();
			short data = -1;
			EntityType type = null;
			switch (entity.getType()) {
				case PIG:
					if (arl.luck.checkChance(killer, 0.0005f, 0.01f)) {
						data = 90;
						type = EntityType.PIG;
					}
					break;
				case COW:
					if (arl.luck.checkChance(killer, 0.0003f, 0.008f)) {
						data = 92;
						type = EntityType.COW;
					}
					break;
				case CHICKEN:
					if (arl.luck.checkChance(killer, 0.0005f, 0.01f)) {
						data = 93;
						type = EntityType.CHICKEN;
					}
					break;
				case SHEEP:
					if (arl.luck.checkChance(killer, 0.0005f, 0.01f)) {
						data = 91;
						type = EntityType.SHEEP;
					}
					break;
				case ZOMBIE:
					if (arl.luck.checkChance(killer, 0.0002f, 0.006f)) {
						data = 54;
						type = EntityType.ZOMBIE;
					}
					break;
				case SKELETON:
					if (arl.luck.checkChance(killer, 0.0002f, 0.004f)) {
						data = 51;
						type = EntityType.SKELETON;
					}
					break;
				case CREEPER:
					if (arl.luck.checkChance(killer, 0.0001f, 0.001f)) {
						data = 50;
						type = EntityType.CREEPER;
					}
					break;
				case SPIDER:
					if (arl.luck.checkChance(killer, 0.0002f, 0.005f)) {
						data = 52;
						type = EntityType.SPIDER;
					}
					break;
				case CAVE_SPIDER:
					if (arl.luck.checkChance(killer, 0.0002f, 0.005f)) {
						data = 59;
						type = EntityType.CAVE_SPIDER;
					}
					break;
				case ENDERMAN:
					if (arl.luck.checkChance(killer, 0.0001f, 0.002f)) {
						data = 58;
						type = EntityType.ENDERMAN;
					}
					break;
				default:
					break;
			}
			if (type != null && data > 0) {
                event.getDrops().add(new CWItem(Material.MONSTER_EGG, 1, data, ("&5&l" + type.toString().toLowerCase().replace("_", " ") + " &6&legg"), new String[]{"&7Can be used to craft spawners!", "&5/recipe spawner &7to see the recipe."}));
				killer.getWorld().playSound(entity.getLocation(), Sound.LEVEL_UP, 2.0f, 2.0f);
				killer.sendMessage(CWUtil.integrateColor(prefix + "Mob spawn egg!"));
                ParticleEffect.FLAME.display(0.5f, 0.5f, 0.5f, 0.001f, 100, entity.getLocation());
			}
		}
	}
	
	
	
	
	
}

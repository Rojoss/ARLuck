package com.josroossien.arluck.events;

import com.clashwars.cwcore.utils.CWUtil;
import com.josroossien.arluck.ARLuck;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.Random;

public class OtherEvents implements Listener {
	
	private ARLuck arl;
	Random random = new Random();
	
	public OtherEvents(ARLuck arl) {
		this.arl = arl;
	}

    //Place custom spawner and set the mob type.
    @EventHandler
    public void Place(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        if (item.getType() != Material.MOB_SPAWNER) {
            return;
        }
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName() && CWUtil.stripAllColor(meta.getDisplayName()).contains("Spawner!")) {
                List<String> lore = meta.getLore();
                if (lore.size() > 0) {
                    BlockState state = event.getBlockPlaced().getState();
                    if (state instanceof CreatureSpawner) {
                        CreatureSpawner spawner = (CreatureSpawner) state;
                        spawner.setCreatureTypeByName(CWUtil.stripAllColor(lore.get(0)));
                        event.getBlockPlaced().setMetadata("CustomSpawner", new FixedMetadataValue(arl, CWUtil.stripAllColor(lore.get(0))));
                        player.sendMessage(CWUtil.integrateColor("&8[&4AR&8] &5" + CWUtil.stripAllColor(lore.get(0)) + " &6spawner placed!"));
                    }
                }
            }
        }
    }

    //Break a custom spawner and give item back.
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        World world = block.getWorld();

        if (block.getType().equals(Material.MOB_SPAWNER)) {
            if (block.hasMetadata("CustomSpawner")) {
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
                world.dropItemNaturally(event.getBlock().getLocation(), arl.getSpawnerItem(event.getBlock().getMetadata("CustomSpawner").get(0).asString()));
                event.getBlock().removeMetadata("CustomSpawner", arl);
            }
            return;
        }
        return;
    }
}

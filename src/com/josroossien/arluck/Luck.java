package com.josroossien.arluck;

import com.clashwars.cwcore.packet.ParticleEffect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class Luck {
	
	private Random random = new Random();
	
	// 0.05 | 0.1
	public boolean checkChance(Player player, float minChance, float maxChance) {
		//Get amount of emeralds in ivnentory.
		int emeralds = 0;
		for (ItemStack item : player.getInventory().getContents()) {
			if (item == null) {
				continue;
			}
			if (item.getType() == Material.EMERALD) {
				emeralds += item.getAmount();
			}
		}
		
		//Get percentage of total emeralds for example 64 is 0.1% and 640 is 1.0%.
		float percentage = ((float)emeralds / 640.0f) * 100.0f;
		
		//Get chance based on percentage of emeralds and min/max chance.
		float chance = minChance + (((maxChance - minChance) / 100) * percentage);
		
		float r = random.nextFloat();
		if (r <= chance) {
			return true;
		} else {
			return false;
		}
	}


	@SuppressWarnings("deprecation")
	public boolean breakTree(Player player, Block baseLog) {
		int blocksRemoved = 0;
		Block blockAbove = baseLog.getRelative(BlockFace.UP);
		while (blockAbove.getType() == Material.LOG || blockAbove.getType() == Material.LOG_2) {
			blocksRemoved++;
			blockAbove.breakNaturally();
            ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(blockAbove.getType(), blockAbove.getData()), 0.5f, 0.5f, 0.5f, 0.2f, 20, blockAbove.getLocation().add(0.5f, 0f, 0.5f));
			blockAbove = blockAbove.getRelative(BlockFace.UP);
		}
		if (blocksRemoved < 1) {
			return false;
		} else {
			return true;
		}
	}
	
	
	
	
}

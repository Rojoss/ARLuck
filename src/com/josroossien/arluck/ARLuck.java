package com.josroossien.arluck;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.utils.CWUtil;
import com.josroossien.arluck.events.LuckEvents;
import com.josroossien.arluck.events.OtherEvents;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class ARLuck extends JavaPlugin {

    private static ARLuck instance;
    public Luck luck;

    private final Logger log = Logger.getLogger("Minecraft");


    @Override
    public void onDisable() {
        log("disabled");
    }

    @Override
    public void onEnable() {
        instance = this;

        registerEvents();
        addRecipes();

        luck = new Luck();

        log("loaded successfully");
    }


    public static ARLuck inst() {
        return instance;
    }

    public void log(Object msg) {
        log.info("[ARLuck " + getDescription().getVersion() + "] " + msg.toString());
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("luck") || label.equalsIgnoreCase("arluck")) {
            sender.sendMessage(CWUtil.integrateColor("&8===== &4&lArchaicRealms luck plugin &8====="));
            sender.sendMessage(CWUtil.integrateColor("&6Author&8: &5worstboy32(jos)"));
            sender.sendMessage(CWUtil.integrateColor("&6For more information check out the wiki!"));
            sender.sendMessage(CWUtil.integrateColor("&9http://archaicrealms.com/towny#Luck"));
            return true;
        }
        return false;
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new LuckEvents(this), this);
        pm.registerEvents(new OtherEvents(this), this);
    }

    private void addRecipes() {
        //Custom Spawnwer recipes.
        addSpawnerRecipe(50, "Creeper");
        addSpawnerRecipe(51, "Skeleton");
        addSpawnerRecipe(52, "Spider");
        addSpawnerRecipe(59, "CaveSpider");
        addSpawnerRecipe(54, "Zombie");
        addSpawnerRecipe(58, "Enderman");
        addSpawnerRecipe(90, "Pig");
        addSpawnerRecipe(92, "Cow");
        addSpawnerRecipe(91, "Sheep");
        addSpawnerRecipe(93, "Chicken");
    }

    private void addSpawnerRecipe(int eggID, String entityName) {
        ShapedRecipe spawner = new ShapedRecipe(getSpawnerItem(entityName));
        spawner.shape("^*^", "^@^", "###");
        spawner.setIngredient('^', Material.DRAGON_EGG);
        spawner.setIngredient('*', Material.NETHER_STAR);
        spawner.setIngredient('@', Material.MONSTER_EGG, eggID);
        spawner.setIngredient('#', Material.OBSIDIAN);
        getServer().addRecipe(spawner);
    }

    public ItemStack getSpawnerItem(String entityName) {
        return new CWItem(Material.MOB_SPAWNER, 1, (short)0, "&5&l" + entityName + " &6&lSpawner!", new String[]{
                "&e&l" + entityName, "&7When placed it will spawn &8" + entityName, "&7You can break and move the spawner for a limited time.", "&cWhen the server restarts you &4can't move &cit anymore!",
                "&7You also can't ask the staff to move the spawner.", "&7So make sure you place it in the right place!", "&7Till a restart you can pick it back up with any pickaxe."});
    }

}

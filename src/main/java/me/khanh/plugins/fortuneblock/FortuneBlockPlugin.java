package me.khanh.plugins.fortuneblock;

import lombok.Getter;
import me.khanh.plugins.fortuneblock.listener.BlockBreakListener;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.SplittableRandom;

public final class FortuneBlockPlugin extends JavaPlugin {
    public Set<Material> BLOCKS = new HashSet<>();
    @Getter
    private boolean useVanilaFormula;
    @Getter
    private double increasePerLevel;
    public final SplittableRandom RANDOM = new SplittableRandom();


    @Override
    public void onEnable() {
        loadConfig();
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this),  this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void loadConfig(){
        saveDefaultConfig();
        for (String key: getConfig().getStringList("Blocks")){
            try {
                Material material = Material.valueOf(key);
                if (!material.isBlock()){
                    getLogger().warning(String.format("Material %s is not a block", key));
                    continue;
                }
                BLOCKS.add(material);
            } catch (IllegalArgumentException e){
                getLogger().warning("Couldn't parse material: " + key);
            }
        }
        useVanilaFormula = getConfig().getBoolean("CalculationFormula.UseVanilaMinecraftFortuneFormula");
        increasePerLevel = getConfig().getDouble("CalculationFormula.NonVanileMinecraftFortuneFormula.IncreasePercentPerLevel");
    }
}

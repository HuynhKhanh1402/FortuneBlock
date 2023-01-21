package me.khanh.plugins.fortuneblock.listener;

import lombok.Getter;
import me.khanh.plugins.fortuneblock.FortuneBlockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ItemSpawnEvent;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;


public class BlockBreakListener implements Listener {
    @Getter
    private final FortuneBlockPlugin plugin;
    private final HashMap<Location, Map.Entry<Material, Integer>> BLOCK_BREAK_LOCATIONS = new HashMap<>();

    public BlockBreakListener(FortuneBlockPlugin plugin){
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.LOW)
    public void onBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        if (event.isCancelled() ||
                !event.isDropItems() ||
                player.getGameMode().equals(GameMode.CREATIVE) ||
                !player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS) ||
                !plugin.BLOCKS.contains(event.getBlock().getType())){
            return;
        }
        Block block = event.getBlock();
        int level = player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
        double chance = level * plugin.getIncreasePerLevel();
        int amount = 0;
        while (chance >= 1){
            amount += 1;
            chance -= 1;
        }
        if (chance != 0){
            if (plugin.RANDOM.nextDouble() <= chance){
                amount += 1;
            }
        }
        if (amount != 0){
            put(block.getLocation(), event.getBlock().getType(), amount + 1);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemSpawn(ItemSpawnEvent event){
        if (event.isCancelled()){
            return;
        }
        Location location = getBlockLocation(event.getLocation());
        if (!BLOCK_BREAK_LOCATIONS.containsKey(getBlockLocation(event.getLocation()))){
            return;
        }
        if (!BLOCK_BREAK_LOCATIONS.get(location).getKey().equals(event.getEntity().getItemStack().getType())){
            return;
        }
        event.getEntity().getItemStack().setAmount(BLOCK_BREAK_LOCATIONS.get(location).getValue());
    }

    private void put(Location blockLocation, Material type, int amount){
        BLOCK_BREAK_LOCATIONS.put(getBlockLocation(blockLocation), new AbstractMap.SimpleEntry<>(type, amount));
        Bukkit.getScheduler().runTaskTimer(plugin, () -> BLOCK_BREAK_LOCATIONS.remove(getBlockLocation(blockLocation)), 0, 40L);
    }

    private Location getBlockLocation(Location location){
        return new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}

package net.plazmix.practice.sumo.listener;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ComboListener
        implements Listener {

    public static final Map<String, Integer> PLAYERS_MAX_COMBO_MAP = new HashMap<>();

    private final Cache<String, Integer> playersCombosMap = CacheBuilder.newBuilder()
            .expireAfterAccess(1500, TimeUnit.MILLISECONDS)
            .build();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() != EntityType.PLAYER) {
            return;
        }

        Player damager = (Player) event.getDamager();

        int currentCombo = playersCombosMap.asMap().getOrDefault(damager.getName(), 0);
        playersCombosMap.put(damager.getName(), currentCombo += 1);

        if (currentCombo >= 3) {

            if (PLAYERS_MAX_COMBO_MAP.getOrDefault(damager.getName(), 0) < currentCombo) {
                PLAYERS_MAX_COMBO_MAP.put(damager.getName(), currentCombo);
            }

            damager.sendTitle("", "§6§l§n" + currentCombo + "§6 COMBO                                                 ");
        }

        playersCombosMap.asMap().remove(event.getEntity().getName());
    }

}


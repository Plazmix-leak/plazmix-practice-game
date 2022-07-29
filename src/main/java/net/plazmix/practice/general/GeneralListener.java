package net.plazmix.practice.general;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.plazmix.game.utility.GameSchedulers;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.concurrent.TimeUnit;

public class GeneralListener implements Listener {
    private final Cache<ProjectileSource, EntityType> enderPearlCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build();

    @EventHandler
    public void onEnderPearThrow(ProjectileLaunchEvent event) {
        if(event.getEntity().getType() != EntityType.ENDER_PEARL) return;

        EntityType entityType = enderPearlCache.getIfPresent(event.getEntity().getShooter());

        if(entityType == null)
            enderPearlCache.put(event.getEntity().getShooter(), EntityType.ENDER_PEARL);
        else
            event.setCancelled(true);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        if (event.getItem().getTypeId() == 373) {
            GameSchedulers.runLater(1L, () -> {
                player.setItemInHand(new ItemStack(Material.AIR));
            });
        }
    }
}

package net.plazmix.practice.sumo;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.installer.GameInstallerTask;
import net.plazmix.game.utility.GameWorldCache;
import net.plazmix.practice.mlgrush.item.category.BedSoundGameCategory;
import net.plazmix.practice.sumo.database.SumoGameStatsMysqlDatabase;
import net.plazmix.practice.sumo.state.SumoEndingState;
import net.plazmix.practice.sumo.state.SumoIngameState;
import net.plazmix.practice.sumo.state.SumoWaitingState;
import org.bukkit.entity.EntityType;


public class SumoInstallerTask extends GameInstallerTask {

    public SumoInstallerTask(@NonNull GamePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void handleExecute(@NonNull Actions actions, @NonNull Settings settings) {
        settings.setRadius(500);
        settings.setCenter(plugin.getService().getMapWorld().getSpawnLocation());

        actions.addEntity(EntityType.ARMOR_STAND, (entity) -> {

            plugin.getCache().set("spawn" + (plugin.getCache().contains("spawn1") ? 2 : 1), entity.getLocation());

            System.out.println("spawn" + (plugin.getCache().contains("spawn1") ? 2 : 1) + " have been set");

            entity.remove();
        });

        plugin.getService().addGameDatabase(new SumoGameStatsMysqlDatabase());

        plugin.getService().registerState(new SumoWaitingState(plugin));
        plugin.getService().registerState(new SumoIngameState(plugin));
        plugin.getService().registerState(new SumoEndingState(plugin));
    }
}

package net.plazmix.practice.gapple;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.installer.GameInstallerTask;
import net.plazmix.practice.gapple.mysql.GAppleGameStatsMysqlDatabase;
import net.plazmix.practice.gapple.state.GAppleEndingState;
import net.plazmix.practice.gapple.state.GAppleIngameState;
import net.plazmix.practice.gapple.state.GAppleWaitingState;
import org.bukkit.entity.EntityType;

public class GAppleInstallerTask extends GameInstallerTask {
    public GAppleInstallerTask(@NonNull GamePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void handleExecute(@NonNull GameInstallerTask.Actions actions, @NonNull GameInstallerTask.Settings settings) {
        settings.setRadius(150);
        settings.setCenter(plugin.getService().getMapWorld().getSpawnLocation());

        actions.addEntity(EntityType.ARMOR_STAND, (entity) -> {

            System.out.println("spawn" + (plugin.getCache().contains("spawn1") ? 2 : 1) + " have been set");

            plugin.getCache().set("spawn" + (plugin.getCache().contains("spawn1") ? 2 : 1), entity.getLocation());

            entity.remove();
        });

        plugin.getService().addGameDatabase(new GAppleGameStatsMysqlDatabase());

        plugin.getService().registerState(new GAppleWaitingState(plugin));
        plugin.getService().registerState(new GAppleIngameState(plugin));
        plugin.getService().registerState(new GAppleEndingState(plugin));
    }
}

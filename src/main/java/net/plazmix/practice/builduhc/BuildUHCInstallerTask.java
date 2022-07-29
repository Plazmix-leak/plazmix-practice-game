package net.plazmix.practice.builduhc;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.installer.GameInstallerTask;
import net.plazmix.practice.builduhc.mysql.BuildUHCGameStatsMysqlDatabase;
import net.plazmix.practice.builduhc.state.BuildUHCEndingState;
import net.plazmix.practice.builduhc.state.BuildUHCIngameState;
import net.plazmix.practice.builduhc.state.BuildUHCWaitingState;
import org.bukkit.entity.EntityType;

public class BuildUHCInstallerTask extends GameInstallerTask {
    public BuildUHCInstallerTask(@NonNull GamePlugin plugin) {
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

        plugin.getService().addGameDatabase(new BuildUHCGameStatsMysqlDatabase());

        plugin.getService().registerState(new BuildUHCWaitingState(plugin));
        plugin.getService().registerState(new BuildUHCIngameState(plugin));
        plugin.getService().registerState(new BuildUHCEndingState(plugin));
    }
}

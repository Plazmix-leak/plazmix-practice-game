package net.plazmix.practice.spleef;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.installer.GameInstallerTask;
import net.plazmix.practice.spleef.database.SpleefGameStatsMysqlDatabase;
import net.plazmix.practice.spleef.state.SpleefEndingState;
import net.plazmix.practice.spleef.state.SpleefIngameState;
import net.plazmix.practice.spleef.state.SpleefWaitingState;
import org.bukkit.entity.EntityType;

public class SpleefInstallerTask extends GameInstallerTask {
    public SpleefInstallerTask(@NonNull GamePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void handleExecute(@NonNull Actions actions, @NonNull Settings settings) {
        settings.setRadius(150);
        settings.setCenter(plugin.getService().getMapWorld().getSpawnLocation());

        actions.addEntity(EntityType.ARMOR_STAND, (entity) -> {

            plugin.getCache().set("spawn" + (plugin.getCache().contains("spawn1") ? 2 : 1), entity.getLocation());

            System.out.println("spawn" + (plugin.getCache().contains("spawn1") ? 2 : 1) + " have been set");

            entity.remove();
        });

        plugin.getService().addGameDatabase(new SpleefGameStatsMysqlDatabase());

        plugin.getService().registerState(new SpleefWaitingState(plugin));
        plugin.getService().registerState(new SpleefIngameState(plugin));
        plugin.getService().registerState(new SpleefEndingState(plugin));
    }
}

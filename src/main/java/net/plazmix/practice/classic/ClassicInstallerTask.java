package net.plazmix.practice.classic;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.installer.GameInstallerTask;
import net.plazmix.practice.classic.mysql.ClassicGameStatsMysqlDatabase;
import net.plazmix.practice.classic.state.ClassicEndingState;
import net.plazmix.practice.classic.state.ClassicIngameState;
import net.plazmix.practice.classic.state.ClassicWaitingState;
import org.bukkit.entity.EntityType;

public class ClassicInstallerTask extends GameInstallerTask {
    public ClassicInstallerTask(@NonNull GamePlugin plugin) {
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

        plugin.getService().addGameDatabase(new ClassicGameStatsMysqlDatabase());

        plugin.getService().registerState(new ClassicWaitingState(plugin));
        plugin.getService().registerState(new ClassicIngameState(plugin));
        plugin.getService().registerState(new ClassicEndingState(plugin));
    }
}

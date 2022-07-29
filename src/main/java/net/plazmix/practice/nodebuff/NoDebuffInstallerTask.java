package net.plazmix.practice.nodebuff;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.installer.GameInstallerTask;
import net.plazmix.practice.nodebuff.mysql.NoDebuffGameStatsMysqlDatabase;
import net.plazmix.practice.nodebuff.state.NoDebuffEndingState;
import net.plazmix.practice.nodebuff.state.NoDebuffIngameState;
import net.plazmix.practice.nodebuff.state.NoDebuffWaitingState;
import org.bukkit.entity.EntityType;

public class NoDebuffInstallerTask extends GameInstallerTask {

    public NoDebuffInstallerTask(@NonNull GamePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void handleExecute(@NonNull Actions actions, @NonNull Settings settings) {
        System.out.println(plugin.getServer().getWorlds());
        settings.setRadius(150);
        settings.setCenter(plugin.getService().getMapWorld().getSpawnLocation());

        actions.addEntity(EntityType.ARMOR_STAND, (entity) -> {

            System.out.println("spawn" + (plugin.getCache().contains("spawn1") ? 2 : 1) + " have been set");

            plugin.getCache().set("spawn" + (plugin.getCache().contains("spawn1") ? 2 : 1), entity.getLocation());

            entity.remove();
        });

        plugin.getService().addGameDatabase(new NoDebuffGameStatsMysqlDatabase());

        plugin.getService().registerState(new NoDebuffWaitingState(plugin));
        plugin.getService().registerState(new NoDebuffIngameState(plugin));
        plugin.getService().registerState(new NoDebuffEndingState(plugin));
    }
}

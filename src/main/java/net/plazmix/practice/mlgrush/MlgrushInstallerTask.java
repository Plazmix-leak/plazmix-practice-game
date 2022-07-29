package net.plazmix.practice.mlgrush;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.installer.GameInstallerTask;
import net.plazmix.game.mysql.type.BasedGameItemsMysqlDatabase;
import net.plazmix.game.utility.GameWorldCache;
import net.plazmix.practice.mlgrush.item.category.BedSoundGameCategory;
import net.plazmix.practice.mlgrush.mysql.MlgrushGameStatsMysqlDatabase;
import net.plazmix.practice.mlgrush.state.MlgrushEndingState;
import net.plazmix.practice.mlgrush.state.MlgrushIngameState;
import net.plazmix.practice.mlgrush.state.MlgrushWaitingState;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class MlgrushInstallerTask extends GameInstallerTask {

    public MlgrushInstallerTask(@NonNull GamePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void handleExecute(@NonNull Actions actions, @NonNull Settings settings) {
        settings.setCenter(plugin.getService().getMapWorld().getSpawnLocation());
        settings.setRadius(500);

        actions.addEntity(EntityType.ARMOR_STAND, entity -> {

            GameWorldCache worldCache = GameWorldCache.fromEntity(entity);

            ArmorStand armorStand = (ArmorStand) entity;
            armorStand.setCanPickupItems(false);
            armorStand.setMarker(false);
            armorStand.setVisible(false);

            worldCache.set("spawn" + (worldCache.contains("spawn1") ? 2 : 1), entity.getLocation());
            System.out.println("setting spawn");

            entity.remove();
        });

        actions.addBlock(Material.BED_BLOCK, block -> {
            GameWorldCache worldCache = GameWorldCache.fromBlock(block);
            worldCache.set("bed" + (worldCache.contains("bed1") ? 2 : 1), block.getLocation());
        });

        // Add game databases.
        plugin.getService().addGameDatabase(new BasedGameItemsMysqlDatabase("MLGRDuels"));
        plugin.getService().addGameDatabase(new MlgrushGameStatsMysqlDatabase());

        // Register game items.
        plugin.getService().registerItemsCategory(new BedSoundGameCategory());

        // Register game states..
        plugin.getService().registerState(new MlgrushWaitingState(plugin));
        plugin.getService().registerState(new MlgrushIngameState(plugin));
        plugin.getService().registerState(new MlgrushEndingState(plugin));

    }

}

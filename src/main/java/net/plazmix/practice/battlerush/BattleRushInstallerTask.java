package net.plazmix.practice.battlerush;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.installer.GameInstallerTask;
import net.plazmix.practice.battlerush.mysql.BattleRushGameStatsMysqlDatabase;
import net.plazmix.practice.battlerush.state.BattleRushEndingState;
import net.plazmix.practice.battlerush.state.BattleRushIngameState;
import net.plazmix.practice.battlerush.state.BattleRushWaitingState;
import net.plazmix.practice.battlerush.util.BattleRushGameConstants;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;

public class BattleRushInstallerTask extends GameInstallerTask {
    public BattleRushInstallerTask(@NonNull GamePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void handleExecute(@NonNull Actions actions, @NonNull Settings settings) {
        settings.setRadius(200);
        settings.setCenter(plugin.getService().getMapWorld().getSpawnLocation());

        actions.addEntity(EntityType.ARMOR_STAND, (entity) -> {

            Block block = entity.getLocation().subtract(0, 1, 0).getBlock();

            if (block.getType() != Material.STAINED_CLAY) return;

            System.out.println(block.getData());

            if (block.getData() == 14) {
                plugin.getCache().set(BattleRushGameConstants.SPAWN_RED, entity.getLocation());
                System.out.println("Location of " + block.getData() + ": has been set");
            } else if(block.getData() == 11) {
                plugin.getCache().set(BattleRushGameConstants.SPAWN_BLUE, entity.getLocation());
                System.out.println("Location of " + block.getData() + ": has been set");
            }

            entity.remove();
        });

        actions.addBlock(Material.FURNACE, block -> {
            Block down = block.getLocation().subtract(0, 1, 0).getBlock();
            if(down.getType() != Material.WOOL) return;

            System.out.println(down.getData());

            if (down.getData() == 14) {
                plugin.getCache().set(BattleRushGameConstants.PORTAL_RED, down.getLocation());
                System.out.println("Location of " + down.getData() + ": has been set");
            } else if(down.getData() == 11) {
                plugin.getCache().set(BattleRushGameConstants.PORTAL_BLUE, down.getLocation());
                System.out.println("Location of " + down.getData() + ": has been set");
            }

            down.setType(Material.ENDER_PORTAL);
            block.setType(Material.AIR);
        });

        plugin.getService().addGameDatabase(new BattleRushGameStatsMysqlDatabase());

        plugin.getService().registerState(new BattleRushWaitingState(plugin));
        plugin.getService().registerState(new BattleRushIngameState(plugin));
        plugin.getService().registerState(new BattleRushEndingState(plugin));
    }
}

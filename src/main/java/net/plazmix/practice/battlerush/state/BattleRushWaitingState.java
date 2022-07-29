package net.plazmix.practice.battlerush.state;

import lombok.NonNull;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.setting.GameSetting;
import net.plazmix.game.state.type.StandardWaitingState;
import net.plazmix.game.user.GameUser;
import net.plazmix.game.utility.GameSchedulers;
import net.plazmix.game.utility.hotbar.GameHotbar;
import net.plazmix.game.utility.hotbar.GameHotbarBuilder;
import net.plazmix.practice.battlerush.scoreboard.BattleRushWaitingScoreboard;
import net.plazmix.practice.battlerush.util.BattleRushGameConstants;
import net.plazmix.practice.util.GeneralGameConstants;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BattleRushWaitingState extends StandardWaitingState {
    private final GameHotbar gameHotbar = GameHotbarBuilder.newBuilder()
            .setMoveItems(false)
            .addItem(9, ItemUtil.newBuilder(Material.MAGMA_CREAM)
                            .setName("§aПокинуть арену")
                            .build(),
                    PlazmixCoreApi::redirectToLobby)
            .build();

    private boolean teleportedToSpawn1 = false;

    public BattleRushWaitingState(GamePlugin plugin) {
        super(plugin, "Ожидание игроков");
    }

    @Override
    protected Location getTeleportLocation() {
        return GeneralGameConstants.LOBBY_LOCATION;
    }

    @Override
    protected void handleEvent(@NonNull PlayerJoinEvent event) {
        GameSetting.setAll(plugin.getService(), false);

        int online = Bukkit.getOnlinePlayers().size();
        int maxOnline = getPlugin().getService().getMaxPlayers();
        if(online > maxOnline)
            PlazmixCoreApi.redirectToLobby(event.getPlayer());

        if(!teleportedToSpawn1) {
            GameUser.from(event.getPlayer()).getCache().set(BattleRushGameConstants.PLAYER_SPAWN, plugin.getCache().getLocation(BattleRushGameConstants.SPAWN_BLUE));
            event.getPlayer().teleport(plugin.getCache().getLocation(BattleRushGameConstants.SPAWN_BLUE));
            System.out.println("Teleported to SpawnBlue");
            teleportedToSpawn1 = true;
        } else {
            GameUser.from(event.getPlayer()).getCache().set(BattleRushGameConstants.PLAYER_SPAWN, plugin.getCache().getLocation(BattleRushGameConstants.SPAWN_RED));
            event.getPlayer().teleport(plugin.getCache().getLocation(BattleRushGameConstants.SPAWN_RED));
            System.out.println("Teleported to SpawnRed");
        }

        GameSchedulers.runLater(10, () -> {

            new BattleRushWaitingScoreboard(getTimerStatus(), event.getPlayer());

            gameHotbar.setHotbarTo(event.getPlayer());
        });

        event.setJoinMessage(GeneralGameConstants.PREFIX + PlazmixUser.of(event.getPlayer()).getDisplayName() + " §fподключился к игре! §7(" + online + "/" + maxOnline + ")");

        // Если сервер полный, то запускаем таймер
        if (online >= maxOnline && !timerStatus.isLived()) {
            timerStatus.runTask();
        }
    }

    @Override
    protected void handleEvent(@NonNull PlayerQuitEvent event) {
        if(plugin.getCache().getLocation(BattleRushGameConstants.SPAWN_BLUE).equals(GameUser.from(event.getPlayer()).getCache().getLocation(BattleRushGameConstants.PLAYER_SPAWN)))
            teleportedToSpawn1 = false;

        int online = Bukkit.getOnlinePlayers().size() - 1;
        int maxOnline = getPlugin().getService().getMaxPlayers();

        event.setQuitMessage(GeneralGameConstants.PREFIX + PlazmixUser.of(event.getPlayer()).getDisplayName() + " §fпокинул игру! §7(" + online + "/" + maxOnline + ")");

        // Если кто-то вышел, то надо вырубать таймер
        if (online < maxOnline && timerStatus.isLived()) {
            timerStatus.cancelTask();
        }
    }

    @Override
    protected void handleTimerUpdate(@NonNull TimerStatus timerStatus) {

    }

    @EventHandler
    public void onPlayerFall(PlayerMoveEvent event) {
        if (GameUser.from(event.getPlayer()).getCache().getLocation(BattleRushGameConstants.PLAYER_SPAWN).getY() > event.getTo().getY()) {
            event.getPlayer().teleport(GameUser.from(event.getPlayer()).getCache().getLocation(BattleRushGameConstants.PLAYER_SPAWN));
        }

    }
}

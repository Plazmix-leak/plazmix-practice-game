package net.plazmix.practice.classic.state;

import lombok.NonNull;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.setting.GameSetting;
import net.plazmix.game.state.type.StandardWaitingState;
import net.plazmix.game.utility.GameSchedulers;
import net.plazmix.game.utility.hotbar.GameHotbar;
import net.plazmix.game.utility.hotbar.GameHotbarBuilder;
import net.plazmix.practice.classic.scoreboard.ClassicWaitingScoreboard;
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

public class ClassicWaitingState extends StandardWaitingState {
    private final GameHotbar gameHotbar = GameHotbarBuilder.newBuilder()
            .setMoveItems(false)
            .addItem(9, ItemUtil.newBuilder(Material.MAGMA_CREAM)
                            .setName("§aПокинуть арену")
                            .build(),
                    PlazmixCoreApi::redirectToLobby)
            .build();

    public ClassicWaitingState(@NonNull GamePlugin plugin) {
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

        GameSchedulers.runLater(10, () -> {

            new ClassicWaitingScoreboard(getTimerStatus(), event.getPlayer());

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
        if (event.getTo().getWorld().getSpawnLocation().getY() - event.getTo().getY() > 5) {

            Location teleportLocation = getTeleportLocation();

            if (teleportLocation != null) {
                event.getPlayer().teleport(teleportLocation);
            }
        }
    }
}

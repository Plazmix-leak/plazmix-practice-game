package net.plazmix.practice;

import com.google.common.collect.ImmutableMap;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.installer.GameInstaller;
import net.plazmix.game.installer.GameInstallerTask;
import net.plazmix.game.utility.GameSchedulers;
import net.plazmix.practice.general.ChatListener;
import net.plazmix.practice.general.CosmeticListener;
import net.plazmix.practice.general.GeneralListener;
import net.plazmix.practice.util.GeneralGameConstants;
import net.plazmix.practice.util.PracticeMode;
import org.bukkit.ChatColor;
import org.bukkit.World;

import java.util.*;

/*  Leaked by https://t.me/leak_mine
    - Все слитые материалы вы используете на свой страх и риск.

    - Мы настоятельно рекомендуем проверять код плагинов на хаки!
    - Список софта для декопиляции плагинов:
    1. Luyten (последнюю версию можно скачать можно тут https://github.com/deathmarine/Luyten/releases);
    2. Bytecode-Viewer (последнюю версию можно скачать можно тут https://github.com/Konloch/bytecode-viewer/releases);
    3. Онлайн декомпиляторы https://jdec.app или http://www.javadecompilers.com/

    - Предложить свой слив вы можете по ссылке @leakmine_send_bot или https://t.me/leakmine_send_bot
*/

public final class PlazmixPracticePlugin extends GamePlugin {

    private static PracticeMode practiceMode;

    public static PracticeMode getPracticeMode(){
        return practiceMode;
    }

    @Override
    public GameInstallerTask getInstallerTask() {
        return PlazmixPracticeLoader.loadPractice(this);
    }

    @Override
    protected void handleEnable() {
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new CosmeticListener(), this);
        getServer().getPluginManager().registerEvents(new GeneralListener(), this);

        saveDefaultConfig();
        GeneralGameConstants.initLocationValues();

        practiceMode = PracticeMode.valueOf(getConfig().getString("mode"));

        service.setGameName("Practice");

        String map = getConfig().getString("map");

        System.out.println(map);

        service.setMapName(map);
        service.setServerMode(practiceMode.getTitle());
        service.setMaxPlayers(2);

        GameSchedulers.runTimer(0, 10, () -> {

            for (World world : getServer().getWorlds()) {
                world.setStorm(false);
                world.setThundering(false);

                world.setWeatherDuration(0);
                world.setTime(1200);
            }
        });

        GameInstaller.create().executeInstall(getInstallerTask());
    }

    @Override
    protected void handleDisable() {
        broadcastMessage(ChatColor.RED + "Арена " + PlazmixCoreApi.getCurrentServerName() + " перезапускается!");
    }

    private String getRandom(List<String> array) {
        int rnd = new Random().nextInt(array.size());
        return array.get(rnd);
    }
}

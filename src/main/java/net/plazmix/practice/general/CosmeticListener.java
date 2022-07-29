package net.plazmix.practice.general;

import com.google.common.collect.ImmutableMap;
import net.plazmix.game.user.GameUser;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Effect;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class CosmeticListener implements Listener {
    public static final ImmutableMap<Integer, String> LOSE_MESSAGES = ImmutableMap.<Integer, String> builder()
            .put(0, "§c§lПОРАЖЕНИЕ")
            .put(1, "§c§lТЫ ПОТЕРПЕЛ КРУШЕНИЕ")
            .put(12, "§6§lТЕБЯ ОТПРАВИЛИ В КОСМОС")
            .build();

    public static final ImmutableMap<Integer, Effect> MOVE_PARTICLES = ImmutableMap.<Integer, Effect> builder()
            .put(1, Effect.VILLAGER_THUNDERCLOUD)
            .put(2, Effect.SNOWBALL_BREAK)
            .build();

    public static String getLoseMessage(GameUser user){
        int choosed = 0;

        return LOSE_MESSAGES.get(choosed);
    }

    public static Effect getMoveParticle(GameUser user){
        int choosed = 1;
        

        return MOVE_PARTICLES.get(choosed);
    }

}

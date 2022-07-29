package net.plazmix.practice.util;

import lombok.EqualsAndHashCode;
import lombok.Value;
import net.plazmix.game.user.GameUser;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Value
@EqualsAndHashCode(callSuper = true)
public class RoundVictoryEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    GameUser user;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}

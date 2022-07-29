package net.plazmix.practice.mlgrush.item.type;

import lombok.NonNull;
import net.plazmix.game.item.GameItem;
import net.plazmix.game.item.GameItemPrice;
import net.plazmix.game.user.GameUser;
import net.plazmix.practice.mlgrush.util.MlgrushGameConstants;
import net.plazmix.utility.ItemUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemFlag;

import java.util.Collections;

public class BedSoundGameItem extends GameItem {

    private final Sound sound;
    private final float pitch;

    public BedSoundGameItem(@NonNull String title, Sound sound, float pitch) {
        super(sound == null ? 0 : sound.ordinal(), GameItemPrice.create(sound == null ? 0 : 5_000, GameItemPrice.PriceCurrency.COINS), title, ItemUtil.newBuilder(sound == null ? Material.BARRIER : Material.GOLD_RECORD)
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .build());

        this.sound = sound;
        this.pitch = pitch;

        setDescription(Collections.singletonList("§7Оооо.. обосраться не встать"));
    }

    @Override
    protected void onApply(@NonNull GameUser gameUser) {
        if (sound == null) {
            return;
        }

        Location bedLocation = gameUser.getCurrentTeam().getCache().get(MlgrushGameConstants.BED_LOCATION_DATA);
        bedLocation.getWorld().playSound(bedLocation, sound, 10, pitch);
    }

    @Override
    protected void onCancel(@NonNull GameUser gameUser) {
        // nothing.
    }

}

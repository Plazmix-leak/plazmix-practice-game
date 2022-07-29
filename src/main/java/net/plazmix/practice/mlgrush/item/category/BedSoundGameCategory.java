package net.plazmix.practice.mlgrush.item.category;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.plazmix.game.item.GameItemsCategory;
import net.plazmix.practice.mlgrush.item.type.BedSoundGameItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class BedSoundGameCategory extends GameItemsCategory {

    public BedSoundGameCategory() {
        super(1, 24, "Звуки ломания кровати", new ItemStack(Material.JUKEBOX));

        for (SoundNames soundNames : SoundNames.values()) {
            addItem(new BedSoundGameItem(soundNames.name, soundNames.sound, soundNames.pitch));
        }

        setDescription(Collections.singletonList("§7Щас помру от переизбытка джавы"));
    }


    @RequiredArgsConstructor
    @AllArgsConstructor
    public enum SoundNames {

        DRAGON_DEATH("Драконья погибель", Sound.ENDERDRAGON_DEATH, 2),
        BLAZE_DEATH("Разочарование преисподни", Sound.BLAZE_DEATH),
        ANVIL_FALL("Металлический звон", Sound.ANVIL_LAND),
        ;

        private final String name;
        private final Sound sound;

        private float pitch = 1f;
    }

}

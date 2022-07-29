package net.plazmix.practice.mlgrush.util;

import lombok.NonNull;
import net.plazmix.inventory.impl.BaseSimpleInventory;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.NumberUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BedAmountSelectInventory extends BaseSimpleInventory {

    public static @NonNull Map<String, Integer> BEDS_VOTES_MAP
            = new HashMap<>();

    public static int getMiddleBedsCount() {
        List<String> playerList = new ArrayList<>(BEDS_VOTES_MAP.keySet());

        if (playerList.isEmpty()) {
            return 5;
        }

        if (playerList.size() == 1) {
            return BEDS_VOTES_MAP.get(playerList.get(0));
        }

        int beds1 = BEDS_VOTES_MAP.getOrDefault(playerList.get(0).toLowerCase(), 0);
        int beds2 = BEDS_VOTES_MAP.getOrDefault(playerList.get(1).toLowerCase(), 0);

        return (beds1 + beds2) / 2;
    }


    public BedAmountSelectInventory() {
        super("Выбор кроватей", 3);
    }

    @Override
    public void drawInventory(Player player) {
        drawBed(11, 3);
        drawBed(13, 5);
        drawBed(15, 7);
        drawBed(17, 9);
    }

    protected void drawBed(int inventorySlot, int amount) {
        setClickItem(inventorySlot, ItemUtil.newBuilder(Material.BED)

                .setAmount(amount)
                .setName(ChatColor.GREEN + NumberUtil.formatting(amount, "кровать", "кровати", "кроватей"))

                .addLore("§7Для победы необходимо будет сломать")
                .addLore("§7кровать противника §e" + NumberUtil.formatting(amount, "раз", "раза", "раз"))
                .addLore("")
                .addLore("§a▸ Нажмите, чтобы проголосовать!")
                .build(),

                (player, inventoryClickEvent) -> {

                    player.closeInventory();

                    if (BEDS_VOTES_MAP.containsKey(player.getName().toLowerCase())) {
                        player.sendMessage(MlgrushGameConstants.PREFIX + "§cВы уже голосовали в данной игре!");
                        return;
                    }

                    player.sendMessage(MlgrushGameConstants.PREFIX + "Ваш голос был засчитан!");
                    BEDS_VOTES_MAP.put(player.getName().toLowerCase(), amount);
                });
    }

}

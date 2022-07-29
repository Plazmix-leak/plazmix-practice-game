package net.plazmix.practice.mlgrush.state;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.NonNull;
import net.md_5.bungee.api.ChatMessageType;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.item.GameItem;
import net.plazmix.game.setting.GameSetting;
import net.plazmix.game.state.GameState;
import net.plazmix.game.team.GameTeam;
import net.plazmix.game.user.GameUser;
import net.plazmix.game.utility.GameSchedulers;
import net.plazmix.game.utility.GameWorldCache;
import net.plazmix.game.utility.hotbar.GameHotbar;
import net.plazmix.game.utility.hotbar.GameHotbarBuilder;
import net.plazmix.game.utility.worldreset.GameWorldReset;
import net.plazmix.practice.mlgrush.scoreboard.MlgrushIngameScoreboard;
import net.plazmix.practice.mlgrush.util.BedAmountSelectInventory;
import net.plazmix.practice.mlgrush.util.MlgrushGameConstants;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.PlayerUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MlgrushIngameState extends GameState {

    private final GameHotbar gameHotbar = GameHotbarBuilder.newBuilder()
            .setMoveItems(true)
            .setAllowInteraction(true)

            .addItem(1, ItemUtil.newBuilder(Material.STICK)
                    .addEnchantment(Enchantment.KNOCKBACK, 1)
                    .build())

            .addItem(2, ItemUtil.newBuilder(Material.WOOD_PICKAXE)
                    .addEnchantment(Enchantment.DIG_SPEED, 2)
                    .setUnbreakable(true)
                    .build())

            .addItem(3, ItemUtil.newBuilder(Material.SANDSTONE)
                    .setAmount(32)
                    .build())

            .build();

    private final Cache<Player, Player> damagerCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10L, TimeUnit.SECONDS)
            .build();

    public MlgrushIngameState(GamePlugin plugin) {
        super(plugin, "Идет игра", false);
    }

    private void resetGameProcess() {
        GameWorldReset.resetAllWorlds();

        for (GameTeam gameTeam : plugin.getService().getLoadedTeams()) {
            gameTeam.handleBroadcast(gameUser -> resetPlayer(gameUser.getBukkitHandle()));
        }
    }

    private Location getSpawnLocation(@NonNull Player player) {
        GameTeam gameTeam = GameUser.from(player).getCurrentTeam();

        if (gameTeam == null) {
            return plugin.getService().getMapWorld().getSpawnLocation();
        }

        Location spawnLocation = getMapWorldCache().get("spawn" + gameTeam.getTeamIndex(), Location.class);

        Vector direction = spawnLocation.getWorld().getSpawnLocation().clone().subtract(spawnLocation).toVector().normalize();
        spawnLocation.setDirection(direction);

        return spawnLocation;
    }

    private Location getBedLocation(@NonNull Player player) {
        GameTeam gameTeam = GameUser.from(player).getCurrentTeam();

        return getMapWorldCache().get("bed" + gameTeam.getTeamIndex(), Location.class);
    }

    private void resetPlayer(@NonNull Player player) {
        player.setNoDamageTicks(999);
        player.teleport(getSpawnLocation(player));

        // Play sounds.
        player.playSound(player.getLocation(), Sound.SILVERFISH_KILL, 1, 1);

        // Reset hotbar.
        gameHotbar.setHotbarTo(player);
        GameSchedulers.runLater(20L, () -> {
            player.setNoDamageTicks(0);
        });
    }

    private GameWorldCache getMapWorldCache() {
        return GameWorldCache.fromWorld(plugin.getService().getMapWorld());
    }

    @Override
    protected void onStart() {
        MlgrushGameConstants.BEDS_COUNT
                = BedAmountSelectInventory.getMiddleBedsCount();

        // change game settings.
        GameSetting.setAll(plugin.getService(), true);

        GameSetting.LEAVES_DECAY.set(plugin.getService(), false);
        GameSetting.FOOD_CHANGE.set(plugin.getService(), false);
        GameSetting.WEATHER_CHANGE.set(plugin.getService(), false);
        GameSetting.ENTITY_EXPLODE.set(plugin.getService(), false);

        // register teams.
        int index = Bukkit.getOnlinePlayers().size();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().setCollidesWithEntities(true);

            // Change this logic for doubles
            GameTeam gameTeam = new GameTeam(index, ChatColor.YELLOW, player.getName());
            plugin.getService().registerTeam(gameTeam);

            gameTeam.getCache().set(MlgrushGameConstants.BED_LOCATION_DATA, getMapWorldCache().get("bed" + gameTeam.getTeamIndex(), Location.class));
            gameTeam.addPlayer(player);

            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
            player.sendTitle("§a§lИГРА НАЧАЛАСЬ", "§fОткинь игрока как можно дальше, чтобы успеть сломать кровать!");

            player.setGameMode(GameMode.SURVIVAL);

            new MlgrushIngameScoreboard(player);
            index--;
        }

        // reset game data.
        resetGameProcess();
    }

    @Override
    protected void onShutdown() {
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (event.getBlockPlaced().getLocation().getBlockY() >= (getSpawnLocation(player).getBlockY() + 5)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Вы не можете ставить блоки за границами карты");
            return;
        }

        System.out.println(getSpawnLocation(player).getYaw());

        GameWorldReset.addBlockCache(event.getBlockPlaced(), event.getBlockReplacedState().getData());
        if(net.plazmix.practice.util.PlayerUtil.getBorder((int) Math.floor(getSpawnLocation(player).getYaw())).equals("x")) {
            if (event.getBlockPlaced().getLocation().getBlockX() >= (getSpawnLocation(player).getBlockX() + 10) || event.getBlockPlaced().getLocation().getBlockX() <= (getSpawnLocation(player).getBlockX() - 10)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Вы не можете ставить блоки за границами карты");
            }
        } else if(net.plazmix.practice.util.PlayerUtil.getBorder((int) Math.floor(getSpawnLocation(player).getYaw())).equals("y")) {
            if (event.getBlockPlaced().getLocation().getBlockY() >= (getSpawnLocation(player).getBlockY() + 10) || event.getBlockPlaced().getLocation().getBlockY() <= (getSpawnLocation(player).getBlockY() - 10)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Вы не можете ставить блоки за границами карты");
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        if (event.getBlock().getType() != Material.BED_BLOCK) {

            if (GameWorldReset.hasBlock(event.getBlock())) {
                GameWorldReset.removeBlockCache(event.getBlock());
                return;
            }

            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        Location ownBedLocation = GameUser.from(player).getCurrentTeam().getCache().get(MlgrushGameConstants.BED_LOCATION_DATA);

        if (event.getBlock().getLocation().distance(ownBedLocation) <= 3) {
            player.sendMessage(MlgrushGameConstants.PREFIX + "§cВы не можете ломать свою кровать!");
            return;
        }

        AtomicBoolean broadcasted = new AtomicBoolean(false);
        plugin.getService().getLoadedTeams().stream()
                .filter(team -> team.getCache().getLocation(MlgrushGameConstants.BED_LOCATION_DATA).distance(event.getBlock().getLocation()) <= 3)
                .findFirst()
                .ifPresent(brokenBedTeam -> {
                    brokenBedTeam.handleBroadcast(member -> {
                        // Play death sound.
                        GameItem deathSoundItem = member.getSelectedItem(plugin.getService().getItemsCategory(1));
                        if (deathSoundItem != null) {
                            deathSoundItem.applyItem(member);
                        }

                        player.sendTitle("", "§aВы сломали кровать оппонента");
                        member.getBukkitHandle().sendTitle("", "§cВаша кровать была сломана");

                        if (!broadcasted.get()) {
                            String placeholder = brokenBedTeam.getPlayers().size() < 2 ? member.getPlazmixHandle().getDisplayName() : brokenBedTeam.getTeamName().substring(0, brokenBedTeam.getTeamName().length() - 2) + "ой команды";
                            plugin.broadcastMessage(MlgrushGameConstants.PREFIX + "Кровать " + placeholder + " §fбыла сломана игроком " + PlazmixUser.of(player).getDisplayName());
                            broadcasted.set(true);
                        }
                    });

                    GameUser gameUser = GameUser.from(player);
                    int bedsBroken = gameUser.getCache().getInt(MlgrushGameConstants.BEDS_BROKEN_DATA);

                    if ((bedsBroken + 1) >= MlgrushGameConstants.BEDS_COUNT) {
                        nextStage();
                        return;
                    }

                    gameUser.getCache().increment(MlgrushGameConstants.BEDS_BROKEN_DATA);
                    resetGameProcess();
                });
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        event.setCancelled(false);
        event.setDamage(0.0);
        if(event.getCause().equals(EntityDamageEvent.DamageCause.FALL))
            event.setCancelled(true);

        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent) event;
            if (event.getEntity().getType() == EntityType.PLAYER && damageByEntityEvent.getDamager().getType() == EntityType.PLAYER) {
                Player damager = (Player) damageByEntityEvent.getDamager();
                damagerCache.put((Player) event.getEntity(), damager);
            }
        }
    }

    @EventHandler
    public void onPlayerFall(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location bedLocation = getBedLocation(player);

        if (bedLocation != null && event.getTo().getBlockY() < bedLocation.getBlockY() - 10) {
            resetPlayer(player);

            Player damager = damagerCache.getIfPresent(player);
            if (damager != null) {
                GameUser killerUser = GameUser.from(damager);
                killerUser.getCache().increment(MlgrushGameConstants.GAME_KILLS_DATA);

                plugin.broadcastMessage(ChatMessageType.ACTION_BAR, PlayerUtil.getDisplayName(player) + " §fбыл убит " + PlayerUtil.getDisplayName(damager));
                damagerCache.invalidate(player);
                return;
            }

            plugin.broadcastMessage(ChatMessageType.ACTION_BAR, PlayerUtil.getDisplayName(player) + " §fвыпал из мира");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        GameUser gameUser = GameUser.from(event.getPlayer());

        if (gameUser.isAlive()) {
            plugin.getService().getStateManager().nextStage();
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getClickedBlock().getType() == Material.BED_BLOCK) {
                event.setCancelled(true);
            }
        }
    }
}

package net.plazmix.practice.battlerush.mysql;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.mysql.GameMysqlDatabase;
import net.plazmix.game.mysql.RemoteDatabaseRowType;
import net.plazmix.game.user.GameUser;
import net.plazmix.practice.battlerush.util.BattleRushGameConstants;

public class BattleRushGameStatsMysqlDatabase extends GameMysqlDatabase {
    public BattleRushGameStatsMysqlDatabase() {
        super("BattleRush", true);
    }

    @Override
    public void initialize() {
        addColumn(BattleRushGameConstants.WINS_PLAYER_DATA, RemoteDatabaseRowType.INT,
                gameUser -> gameUser.getCache().getInt(BattleRushGameConstants.WINS_PLAYER_DATA));
        addColumn(BattleRushGameConstants.GAMES_PLAYED_PLAYER_DATA, RemoteDatabaseRowType.INT,
                gameUser -> gameUser.getCache().getInt(BattleRushGameConstants.GAMES_PLAYED_PLAYER_DATA));
    }

    @Override
    public void onJoinLoad(@NonNull GamePlugin gamePlugin, @NonNull GameUser gameUser) {
        loadPrimary(false, gameUser, gameUser.getCache()::set);
    }

}

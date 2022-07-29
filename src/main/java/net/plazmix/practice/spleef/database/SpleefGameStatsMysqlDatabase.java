package net.plazmix.practice.spleef.database;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.mysql.GameMysqlDatabase;
import net.plazmix.game.mysql.RemoteDatabaseRowType;
import net.plazmix.game.user.GameUser;
import net.plazmix.practice.spleef.util.SpleefGameConstants;
import net.plazmix.practice.sumo.util.SumoGameConstants;

public class SpleefGameStatsMysqlDatabase extends GameMysqlDatabase {
    public SpleefGameStatsMysqlDatabase() {
        super("Spleef", true);
    }

    @Override
    public void initialize() {
        addColumn(SpleefGameConstants.SPLEEF_WINS_PLAYER_DATA, RemoteDatabaseRowType.INT,
                gameUser -> gameUser.getCache().getInt(SpleefGameConstants.SPLEEF_WINS_PLAYER_DATA));
        addColumn(SpleefGameConstants.SPLEEF_GAMES_PLAYED_PLAYER_DATA, RemoteDatabaseRowType.INT,
                gameUser -> gameUser.getCache().getInt(SpleefGameConstants.SPLEEF_GAMES_PLAYED_PLAYER_DATA));
    }

    @Override
    public void onJoinLoad(@NonNull GamePlugin gamePlugin, @NonNull GameUser gameUser) {
        loadPrimary(false, gameUser, gameUser.getCache()::set);
    }
}

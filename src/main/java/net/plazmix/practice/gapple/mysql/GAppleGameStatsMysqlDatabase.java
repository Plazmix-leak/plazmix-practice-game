package net.plazmix.practice.gapple.mysql;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.mysql.GameMysqlDatabase;
import net.plazmix.game.mysql.RemoteDatabaseRowType;
import net.plazmix.game.user.GameUser;
import net.plazmix.practice.gapple.util.GAppleGameConstants;

public class GAppleGameStatsMysqlDatabase extends GameMysqlDatabase {
    public GAppleGameStatsMysqlDatabase() {
        super("GApple", true);
    }

    @Override
    public void initialize() {
        addColumn(GAppleGameConstants.GAPPLE_WINS_PLAYER_DATA, RemoteDatabaseRowType.INT,
                gameUser -> gameUser.getCache().getInt(GAppleGameConstants.GAPPLE_WINS_PLAYER_DATA));
        addColumn(GAppleGameConstants.GAPPLE_GAMES_PLAYED_PLAYER_DATA, RemoteDatabaseRowType.INT,
                gameUser -> gameUser.getCache().getInt(GAppleGameConstants.GAPPLE_GAMES_PLAYED_PLAYER_DATA));
    }

    @Override
    public void onJoinLoad(@NonNull GamePlugin gamePlugin, @NonNull GameUser gameUser) {
        loadPrimary(false, gameUser, gameUser.getCache()::set);
    }
}

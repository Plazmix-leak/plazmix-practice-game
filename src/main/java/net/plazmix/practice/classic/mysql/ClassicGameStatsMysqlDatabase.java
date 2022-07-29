package net.plazmix.practice.classic.mysql;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.mysql.GameMysqlDatabase;
import net.plazmix.game.mysql.RemoteDatabaseRowType;
import net.plazmix.game.user.GameUser;
import net.plazmix.practice.classic.util.ClassicGameConstants;

public class ClassicGameStatsMysqlDatabase extends GameMysqlDatabase {
    public ClassicGameStatsMysqlDatabase() {
        super("ClassicDuels", true);
    }

    @Override
    public void initialize() {
        addColumn(ClassicGameConstants.CLASSIC_WINS_PLAYER_DATA, RemoteDatabaseRowType.INT,
                gameUser -> gameUser.getCache().getInt(ClassicGameConstants.CLASSIC_WINS_PLAYER_DATA));
        addColumn(ClassicGameConstants.CLASSIC_GAMES_PLAYED_PLAYER_DATA, RemoteDatabaseRowType.INT,
                gameUser -> gameUser.getCache().getInt(ClassicGameConstants.CLASSIC_GAMES_PLAYED_PLAYER_DATA));
    }

    @Override
    public void onJoinLoad(@NonNull GamePlugin gamePlugin, @NonNull GameUser gameUser) {
        loadPrimary(false, gameUser, gameUser.getCache()::set);
    }
}

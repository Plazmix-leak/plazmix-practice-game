package net.plazmix.practice.builduhc.mysql;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.mysql.GameMysqlDatabase;
import net.plazmix.game.mysql.RemoteDatabaseRowType;
import net.plazmix.game.user.GameUser;
import net.plazmix.practice.builduhc.util.BuildUHCGameConstants;

public class BuildUHCGameStatsMysqlDatabase extends GameMysqlDatabase {
    public BuildUHCGameStatsMysqlDatabase() {
        super("BuildUHC", true);
    }

    @Override
    public void initialize() {
        addColumn(BuildUHCGameConstants.BUILDUHC_WINS_PLAYER_DATA, RemoteDatabaseRowType.INT,
                gameUser -> gameUser.getCache().getInt(BuildUHCGameConstants.BUILDUHC_WINS_PLAYER_DATA));
        addColumn(BuildUHCGameConstants.BUILDUHC_GAMES_PLAYED_PLAYER_DATA, RemoteDatabaseRowType.INT,
                gameUser -> gameUser.getCache().getInt(BuildUHCGameConstants.BUILDUHC_GAMES_PLAYED_PLAYER_DATA));
    }

    @Override
    public void onJoinLoad(@NonNull GamePlugin gamePlugin, @NonNull GameUser gameUser) {
        loadPrimary(false, gameUser, gameUser.getCache()::set);
    }
}

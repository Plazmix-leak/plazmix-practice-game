package net.plazmix.practice.mlgrush.mysql;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.mysql.GameMysqlDatabase;
import net.plazmix.game.mysql.RemoteDatabaseRowType;
import net.plazmix.game.user.GameUser;
import net.plazmix.practice.mlgrush.util.MlgrushGameConstants;

public final class MlgrushGameStatsMysqlDatabase extends GameMysqlDatabase {

    public MlgrushGameStatsMysqlDatabase() {
        super("MLGRDuels", true);
    }

    @Override
    public void initialize() {
        addColumn(MlgrushGameConstants.WINS_PLAYER_DATA, RemoteDatabaseRowType.INT, gameUser -> gameUser.getCache().getInt(MlgrushGameConstants.WINS_PLAYER_DATA));
        addColumn(MlgrushGameConstants.BEDS_PLAYER_DATA, RemoteDatabaseRowType.INT, gameUser -> gameUser.getCache().getInt(MlgrushGameConstants.BEDS_PLAYER_DATA));
        addColumn(MlgrushGameConstants.KILLS_PLAYER_DATA, RemoteDatabaseRowType.INT, gameUser -> gameUser.getCache().getInt(MlgrushGameConstants.KILLS_PLAYER_DATA));
        addColumn(MlgrushGameConstants.BLOCK_PLACED_PLAYER_DATA, RemoteDatabaseRowType.INT, gameUser -> gameUser.getCache().getInt(MlgrushGameConstants.BLOCK_PLACED_PLAYER_DATA));
        addColumn(MlgrushGameConstants.GAMES_PLAYED_PLAYER_DATA, RemoteDatabaseRowType.INT, gameUser -> gameUser.getCache().getInt(MlgrushGameConstants.GAMES_PLAYED_PLAYER_DATA));
    }

    @Override
    public void onJoinLoad(@NonNull GamePlugin gamePlugin, @NonNull GameUser gameUser) {
        loadPrimary(false, gameUser, gameUser.getCache()::set);
    }

}

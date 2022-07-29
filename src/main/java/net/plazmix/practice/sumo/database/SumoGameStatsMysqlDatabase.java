package net.plazmix.practice.sumo.database;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.mysql.GameMysqlDatabase;
import net.plazmix.game.mysql.RemoteDatabaseRowType;
import net.plazmix.game.user.GameUser;
import net.plazmix.practice.sumo.util.SumoGameConstants;

public class SumoGameStatsMysqlDatabase extends GameMysqlDatabase {


    public SumoGameStatsMysqlDatabase() {
        super("SumoDuels", true);
    }

    @Override
    public void initialize() {
        addColumn(SumoGameConstants.SUMO_WINS_PLAYER_DATA, RemoteDatabaseRowType.INT,
                gameUser -> gameUser.getCache().getInt(SumoGameConstants.SUMO_WINS_PLAYER_DATA));
        addColumn(SumoGameConstants.SUMO_GAMES_PLAYED_PLAYER_DATA, RemoteDatabaseRowType.INT,
                gameUser -> gameUser.getCache().getInt(SumoGameConstants.SUMO_GAMES_PLAYED_PLAYER_DATA));
    }

    @Override
    public void onJoinLoad(@NonNull GamePlugin gamePlugin, @NonNull GameUser gameUser) {
        loadPrimary(false, gameUser, gameUser.getCache()::set);
    }


}


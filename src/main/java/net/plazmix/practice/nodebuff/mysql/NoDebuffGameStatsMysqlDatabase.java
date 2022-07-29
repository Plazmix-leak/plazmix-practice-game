package net.plazmix.practice.nodebuff.mysql;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.mysql.GameMysqlDatabase;
import net.plazmix.game.mysql.RemoteDatabaseRowType;
import net.plazmix.game.user.GameUser;
import net.plazmix.practice.nodebuff.util.NoDebuffGameConstants;

public class NoDebuffGameStatsMysqlDatabase  extends GameMysqlDatabase {
    public NoDebuffGameStatsMysqlDatabase() {
        super("NoDebuff", true);
    }

    @Override
    public void initialize() {
        addColumn(NoDebuffGameConstants.NODEBUFF_WINS_PLAYER_DATA, RemoteDatabaseRowType.INT,
                gameUser -> gameUser.getCache().getInt(NoDebuffGameConstants.NODEBUFF_WINS_PLAYER_DATA));
        addColumn(NoDebuffGameConstants.NODEBUFF_GAMES_PLAYED_PLAYER_DATA, RemoteDatabaseRowType.INT,
                gameUser -> gameUser.getCache().getInt(NoDebuffGameConstants.NODEBUFF_GAMES_PLAYED_PLAYER_DATA));
    }

    @Override
    public void onJoinLoad(@NonNull GamePlugin gamePlugin, @NonNull GameUser gameUser) {
        loadPrimary(false, gameUser, gameUser.getCache()::set);
    }
}

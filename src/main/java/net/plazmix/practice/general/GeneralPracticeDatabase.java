package net.plazmix.practice.general;

import lombok.NonNull;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.mysql.GameMysqlDatabase;
import net.plazmix.game.mysql.RemoteDatabaseRowType;
import net.plazmix.game.user.GameUser;
import net.plazmix.practice.util.GeneralGameConstants;

public class GeneralPracticeDatabase extends GameMysqlDatabase {

    public GeneralPracticeDatabase() {
        super("Practice", true);
    }

    @Override
    public void initialize() {
        addColumn(GeneralGameConstants.PRACTICE_TOTAL_XP, RemoteDatabaseRowType.INT,
                gameUser -> gameUser.getCache().getInt(GeneralGameConstants.PRACTICE_TOTAL_XP));
        addColumn(GeneralGameConstants.PRACTICE_TOTAL_POINTS, RemoteDatabaseRowType.INT,
                gameUser -> gameUser.getCache().getInt(GeneralGameConstants.PRACTICE_TOTAL_POINTS));
    }

    @Override
    public void onJoinLoad(@NonNull GamePlugin gamePlugin, @NonNull GameUser gameUser) {
        loadPrimary(false, gameUser, gameUser.getCache()::set);
    }


}


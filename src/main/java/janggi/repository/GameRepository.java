package janggi.repository;

import janggi.db.DBConnector;
import janggi.db.GameDao;
import janggi.db.PieceDao;
import janggi.domain.GameContext;
import janggi.domain.board.Board;
import janggi.domain.team.TurnManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class GameRepository {
    private final DBConnector dbConnector;

    public GameRepository(DBConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    public int saveGame(GameContext gameContext) {
        try (Connection connection = dbConnector.getConnection()) {
            int gameId = GameDao.insertCurrentTurn(connection, gameContext);
            PieceDao.insertPiece(connection, gameContext, gameId);
            return gameId;
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 오류", e);
        }
    }

    public GameContext loadPreviousGame(final int gameId) {
        try (Connection connection = dbConnector.getConnection()) {
            TurnManager turnManager = new TurnManager(GameDao.selectCurrentTurn(connection, gameId));
            Board board = new Board(PieceDao.selectPieceMap(connection, gameId));
            return new GameContext(turnManager, board);
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 오류", e);
        }
    }

    public void deleteGame(final int gameId) {
        try (Connection connection = dbConnector.getConnection()) {
            PieceDao.deletePiecesTable(connection, gameId);
            GameDao.deleteGameTable(connection, gameId);
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 오류", e);
        }
    }

    public boolean hasGameData() {
        try (Connection connection = dbConnector.getConnection()) {
            return GameDao.hasGameData(connection);
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 오류", e);
        }
    }

    public List<String> printGameData() {
        try (Connection connection = dbConnector.getConnection()) {
            return GameDao.selectGameData(connection);
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 오류", e);
        }
    }
}

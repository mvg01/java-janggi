package janggi.repository;

import janggi.db.DBConnector;
import janggi.db.GameDao;
import janggi.db.GameRecord;
import janggi.db.GameTurnRecord;
import janggi.db.PieceDao;
import janggi.db.PieceRecord;
import janggi.domain.GameContext;
import janggi.domain.Position;
import janggi.domain.board.Board;
import janggi.domain.piece.Piece;
import janggi.domain.piece.PieceType;
import janggi.domain.team.TeamType;
import janggi.domain.team.TurnManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameRepository {
    private final DBConnector dbConnector;

    public GameRepository(DBConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    public int saveGame(GameContext gameContext) {
        try (Connection connection = dbConnector.getConnection()) {
            int gameId = GameDao.insertCurrentTurn(connection,
                    new GameTurnRecord(gameContext.currentTeamType().toString()));
            List<PieceRecord> pieceRecords = gameContext.getPositionPieceMap().entrySet().stream()
                    .map(entry -> new PieceRecord(
                            entry.getKey().getRow(), entry.getKey().getColumn(),
                            entry.getValue().pieceType().toString(), entry.getValue().teamType().toString()))
                    .toList();
            PieceDao.insertPiece(connection, pieceRecords, gameId);
            return gameId;
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 오류", e);
        }
    }

    public GameContext loadPreviousGame(final int gameId) {
        try (Connection connection = dbConnector.getConnection()) {
            GameTurnRecord gameTurnRecord = GameDao.selectCurrentTurn(connection, gameId);
            List<PieceRecord> pieceRecords = PieceDao.selectPieceMap(connection, gameId);

            Map<Position, Piece> positionPieceMap = pieceRecords.stream()
                    .collect(Collectors.toMap(
                            entry -> Position.valueOf(entry.row(), entry.column()),
                            entry -> {
                                PieceType pieceType = PieceType.valueOf(entry.pieceType());
                                TeamType teamType = TeamType.valueOf(entry.teamType());
                                return pieceType.toPiece(teamType);
                            }
                    ));
            Board board = new Board(positionPieceMap);
            return new GameContext(new TurnManager(gameTurnRecord.currentTurn()), board);
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
            List<GameRecord> gameRecords = GameDao.selectGameData(connection);
            return gameRecords.stream()
                    .map(record -> "id: " + record.id() + " 마지막으로 저장된 시간: " + record.timeStamp().
                            toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                    .toList();
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 오류", e);
        }
    }
}

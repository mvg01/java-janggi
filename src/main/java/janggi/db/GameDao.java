package janggi.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GameDao {
    private GameDao() {
    }

    public static int insertCurrentTurn(Connection connection, GameTurnRecord gameTurnRecord) {
        final String sql = "INSERT INTO game (current_turn) VALUES (?);";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, gameTurnRecord.currentTurn());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                throw new SQLException("생성된 키 없음");
            }
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 오류", e);
        }
    }

    public static GameTurnRecord selectCurrentTurn(Connection connection, final int gameId) {
        final String sql = "SELECT current_turn FROM game WHERE id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, gameId);
            return extractCurrentTurn(statement);
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 오류", e);
        }
    }

    private static GameTurnRecord extractCurrentTurn(PreparedStatement statement) {
        try (ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return new GameTurnRecord(resultSet.getString("current_turn"));
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 오류", e);
        }
    }

    public static boolean hasGameData(Connection connection) {
        final String sql = "SELECT COUNT(*) FROM game;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            return isGamePresent(statement);
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 오류", e);
        }
    }

    private static boolean isGamePresent(PreparedStatement statement) {
        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("COUNT(*)") > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 오류", e);
        }
    }

    public static List<GameRecord> selectGameData(Connection connection) {
        final String sql = "SELECT id, saved_at FROM game;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            return queryToList(statement);
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 오류", e);
        }
    }

    private static List<GameRecord> queryToList(PreparedStatement statement) {
        try (ResultSet resultSet = statement.executeQuery()) {
            List<GameRecord> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(new GameRecord(resultSet.getInt("id"), resultSet.getTimestamp("saved_at")));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 오류", e);
        }
    }
}

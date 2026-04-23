package janggi.db;

import janggi.domain.GameContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GameDao {
    private GameDao() {
    }

    public static void deleteGameTable(Connection connection, final int gameId) {
        final String sql = "DELETE FROM GAME WHERE id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, gameId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 오류", e);
        }
    }

    public static int insertCurrentTurn(Connection connection, GameContext gameContext) {
        final String sql = "INSERT INTO game (current_turn) VALUES (?);";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, gameContext.currentTeamType().toString());
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

    public static String selectCurrentTurn(Connection connection, final int gameId) {
        final String sql = "SELECT current_turn FROM game WHERE id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, gameId);
            return extractCurrentTurn(statement);
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 오류", e);
        }
    }

    private static String extractCurrentTurn(PreparedStatement statement) {
        try (ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getString("current_turn");
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

    public static List<String> selectGameData(Connection connection) {
        final String sql = "SELECT id, saved_at FROM game;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            return queryToList(statement);
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 오류", e);
        }
    }

    private static List<String> queryToList(PreparedStatement statement) {
        try (ResultSet resultSet = statement.executeQuery()) {
            List<String> list = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                Timestamp savedAt = resultSet.getTimestamp("saved_at");
                String formatted = savedAt.toLocalDateTime()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                list.add(id + "번 게임 | 마지막 저장: " + formatted);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 오류", e);
        }
    }
}

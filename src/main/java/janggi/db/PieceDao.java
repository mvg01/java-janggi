package janggi.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PieceDao {
    private PieceDao() {
    }

    public static void insertPiece(Connection connection, List<PieceRecord> pieceRecords, final int gameId) {
        final String sql = "INSERT INTO piece (game_id, position_row, position_column, piece_type, team_type) VALUES (?, ?, ?, ?, ?);";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (PieceRecord pieceRecord : pieceRecords) {
                statement.setInt(1, gameId);
                statement.setInt(2, pieceRecord.row());
                statement.setInt(3, pieceRecord.column());
                statement.setString(4, pieceRecord.pieceType());
                statement.setString(5, pieceRecord.teamType());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 오류", e);
        }
    }

    public static List<PieceRecord> selectPieceMap(Connection connection, final int gameId) {
        final String sql = "SELECT position_row, position_column, piece_type, team_type FROM piece WHERE game_id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, gameId);
            return extractPieceMap(statement);
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 오류", e);
        }
    }

    private static List<PieceRecord> extractPieceMap(PreparedStatement statement) {
        try (ResultSet resultSet = statement.executeQuery()) {
            List<PieceRecord> pieceRecords = new ArrayList<>();
            while (resultSet.next()) {
                pieceRecords.add(new PieceRecord(resultSet.getInt("position_row"), resultSet.getInt("position_column"),
                        resultSet.getString("piece_type"), resultSet.getString("team_type")));
            }
            return pieceRecords;
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 오류", e);
        }
    }
}

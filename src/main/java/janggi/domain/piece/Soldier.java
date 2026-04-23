package janggi.domain.piece;

import janggi.domain.movement.Direction;
import janggi.domain.movement.MoveRule;
import janggi.domain.movement.Movement;
import janggi.domain.movement.NormalMoveRule;
import janggi.domain.movement.PalaceMoveRule;
import janggi.domain.team.TeamType;
import java.util.List;

public class Soldier extends AbstractPiece {
    private static final PieceType PIECE_TYPE = PieceType.SOLDIER;
    private static final PieceAction RED_PIECE_ACTION = createPieceAction(TeamType.RED);
    private static final PieceAction BLUE_PIECE_ACTION = createPieceAction(TeamType.BLUE);

    public Soldier(TeamType teamType) {
        super(teamType);
    }

    private static PieceAction createPieceAction(TeamType teamType) {
        if (teamType == TeamType.RED) {
            final List<MoveRule> redMovementStrategies = List.of(
                    new NormalMoveRule(new Movement(Direction.LEFT), 1),
                    new NormalMoveRule(new Movement(Direction.RIGHT), 1),
                    new NormalMoveRule(new Movement(Direction.DOWN), 1),
                    new PalaceMoveRule(Direction.DOWN_LEFT),
                    new PalaceMoveRule(Direction.DOWN_RIGHT));
            return new PieceAction(redMovementStrategies);
        }
        final List<MoveRule> blueMovementStrategies = List.of(
                new NormalMoveRule(new Movement(Direction.LEFT), 1),
                new NormalMoveRule(new Movement(Direction.RIGHT), 1),
                new NormalMoveRule(new Movement(Direction.UP), 1),
                new PalaceMoveRule(Direction.UP_LEFT),
                new PalaceMoveRule(Direction.UP_RIGHT));
        return new PieceAction(blueMovementStrategies);
    }

    @Override
    protected PieceType getPieceType() {
        return PIECE_TYPE;
    }

    @Override
    protected PieceAction getPieceAction() {
        if (teamType == TeamType.RED) {
            return RED_PIECE_ACTION;
        }
        return BLUE_PIECE_ACTION;
    }
}
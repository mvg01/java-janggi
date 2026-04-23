package janggi.domain.piece;

import janggi.domain.movement.Direction;
import janggi.domain.movement.MoveRule;
import janggi.domain.movement.Movement;
import janggi.domain.movement.StepMoveRule;
import janggi.domain.team.TeamType;
import java.util.List;

public class Elephant extends AbstractPiece {
    private static final PieceType PIECE_TYPE = PieceType.ELEPHANT;
    private static final PieceAction PIECE_ACTION;

    static {
        final List<MoveRule> movementStrategies = List.of(
                createElephantRule(Direction.UP, Direction.UP_LEFT),
                createElephantRule(Direction.UP, Direction.UP_RIGHT),
                createElephantRule(Direction.RIGHT, Direction.UP_RIGHT),
                createElephantRule(Direction.RIGHT, Direction.DOWN_RIGHT),
                createElephantRule(Direction.DOWN, Direction.DOWN_LEFT),
                createElephantRule(Direction.DOWN, Direction.DOWN_RIGHT),
                createElephantRule(Direction.LEFT, Direction.DOWN_LEFT),
                createElephantRule(Direction.LEFT, Direction.UP_LEFT));
        PIECE_ACTION = new PieceAction(movementStrategies);
    }

    public Elephant(TeamType teamType) {
        super(teamType);
    }

    private static StepMoveRule createElephantRule(Direction straight, Direction diagonal) {
        return new StepMoveRule(List.of(
                new Movement(straight),
                new Movement(diagonal),
                new Movement(diagonal)
        ));
    }

    @Override
    protected PieceType getPieceType() {
        return PIECE_TYPE;
    }

    @Override
    protected PieceAction getPieceAction() {
        return PIECE_ACTION;
    }
}
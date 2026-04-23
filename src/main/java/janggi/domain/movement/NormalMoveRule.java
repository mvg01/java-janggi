package janggi.domain.movement;

import janggi.domain.Position;
import janggi.domain.board.BoardMediator;
import janggi.domain.team.TeamType;
import java.util.List;

public class NormalMoveRule implements MoveRule {

    private final Movement movementOrder;
    private final int distance;

    public NormalMoveRule(Movement movementOrder, int distance) {
        this.movementOrder = movementOrder;
        this.distance = distance;
    }

    @Override
    public List<Position> execute(Position from, final TeamType teamType, final BoardMediator boardMediator) {
        return movementOrder.calculateTraces(from, teamType, boardMediator, distance);
    }
}

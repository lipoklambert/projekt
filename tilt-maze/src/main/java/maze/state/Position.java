package maze.state;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

/**
 * Represents a 2D position.
 */
@Value
@AllArgsConstructor
public class Position {

    int row;
    int col;

    /**
     * {@return the position whose vertical and horizontal distances from this
     * position are equal to the coordinate changes of the direction given}
     *
     * @param direction a direction that specifies a change in the coordinates
     */
    public Position getNeighbor(
            @NonNull final Direction direction) {

        return new Position(row + direction.getRowChange(), col + direction.getColChange());
    }

    @Override
    public String toString() {
        return String.format("(%d,%d)", row, col);
    }

    /**
     * Represents the four main directions.
     */
    @Getter
    @AllArgsConstructor
    public enum Direction {

        UP(-1, 0),
        RIGHT(0, 1),
        DOWN(1, 0),
        LEFT(0, -1);

        private final int rowChange;
        private final int colChange;

    }
}

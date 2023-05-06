package maze.state;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

import java.util.*;
import java.util.stream.Collectors;

@Value
@AllArgsConstructor
@With
public class MazeState {
    /**
     * The index of the block.
     */
    public static final int BALL = 0;

    private int[][] wall_board = {
            {13, 8, 7, 6, 8, 2, 12},
            {5, 1, 2, 1, 1, 1, 6},
            {5, 4, 3, 5, 1, 3, 10},
            {5, 2, 1, 9, 10, 5, 9},
            {11, 1, 1, 2, 4, 1, 6},
            {8, 3, 15, 5, 2, 1, 3},
            {11, 4, 7, 9, 11, 9, 15}
    };

    List<Position> positions;

    private static final List<Position> INITIAL_STATE = List.of(
            new Position(1, 4),
            new Position(5, 2)
    );

    /**
     * Creates a {@code MazeState} object that corresponds to the original
     * initial state of the maze.
     */
    public MazeState() {
        this.positions = INITIAL_STATE;
    }

    /**
     * Performs a move and returns the new state.
     *
     * @param direction the direction to which the block is moved
     * @return the new state
     */
    public MazeState doMove(
            @NonNull final Position.Direction direction) {
        return switch (direction) {
            case UP:
                yield moveUp();
            case RIGHT:
                yield moveRight();
            case DOWN:
                yield moveDown();
            case LEFT:
                yield moveLeft();
        };
    }

    /**
     * {@return whether the block can be moved to the direction specified}
     *
     * @param direction a direction to which the block is intended to be moved
     */
    public boolean checkMove(Position.Direction direction) {
        return switch (direction) {
            case UP -> canMoveUp();
            case RIGHT -> canMoveRight();
            case DOWN -> canMoveDown();
            case LEFT -> canMoveLeft();
        };
    }


    /**
     * {@return a copy of the position of the piece specified}
     *
     * @param n the number of a piece
     */
    public Position getPosition(final int n) {
        return positions.get(n);
    }

    public boolean isGoal() {
        return positions.get(BALL).getRow() == 5 && positions.get(BALL).getCol() == 2;
    }

    private boolean canMoveUp() {
        int[] cantmoveup = {2, 6, 7, 8, 12, 13, 14};
        for(int i = 0; i < cantmoveup.length; i++){
            if(cantmoveup[i] == wall_board[positions.get(BALL).getRow()][positions.get(BALL).getCol()]){
                return false;
            }
        }
        return true;
    }

    private boolean canMoveRight() {
        int[] cantmoveright = {3, 6, 9, 10, 12, 13, 15};
        for(int i = 0; i < cantmoveright.length; i++){
            if(cantmoveright[i] == wall_board[positions.get(BALL).getRow()][positions.get(BALL).getCol()]){
                return false;
            }
        }
        return true;
    }

    private boolean canMoveDown() {
        int[] cantmovedown = {4, 7, 9, 11, 12, 14, 15};
        for(int i = 0; i < cantmovedown.length; i++){
            if(cantmovedown[i] == wall_board[positions.get(BALL).getRow()][positions.get(BALL).getCol()]){
                return false;
            }
        }
        return true;
    }

    private boolean canMoveLeft() {
        int[] cantmoveleft = {5, 8, 10, 11, 13, 14, 15};
        for(int i = 0; i < cantmoveleft.length; i++){
            if(cantmoveleft[i] == wall_board[positions.get(BALL).getRow()][positions.get(BALL).getCol()]){
                return false;
            }
        }
        return true;
    }


    private MazeState moveUp() {
        final var newPositions = new ArrayList<>(this.positions);
        final var newPosition = getPosition(BALL).getNeighbor(Position.Direction.UP);
        newPositions.set(BALL, newPosition);
        return withPositions(Collections.unmodifiableList(newPositions));
    }

    private MazeState moveRight() {
        final var newPositions = new ArrayList<>(this.positions);
        final var newPosition = getPosition(BALL).getNeighbor(Position.Direction.RIGHT);
        newPositions.set(BALL, newPosition);
        return withPositions(Collections.unmodifiableList(newPositions));
    }

    private MazeState moveDown() {
        final var newPositions = new ArrayList<>(this.positions);
        final var newPosition = getPosition(BALL).getNeighbor(Position.Direction.DOWN);
        newPositions.set(BALL, newPosition);
        return withPositions(Collections.unmodifiableList(newPositions));
    }

    private MazeState moveLeft() {
        final var newPositions = new ArrayList<>(this.positions);
        final var newPosition = getPosition(BALL).getNeighbor(Position.Direction.LEFT);
        newPositions.set(BALL, newPosition);
        return withPositions(Collections.unmodifiableList(newPositions));
    }

    @Override
    public String toString() {
        return positions.stream()
                .map(Position::toString)
                .collect(Collectors.joining(",", "[", "]"));
    }
}

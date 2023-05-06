package maze.state;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class MazeStateTest {
    @Test
    void testDoMove() {
        MazeState state = new MazeState();
        MazeState newState = state.doMove(Position.Direction.DOWN);

        assertNotEquals(state, newState);
        assertEquals(new Position(2, 4), newState.getPosition(MazeState.BALL));
    }

    @Test
    void testCheckMove() {
        MazeState state = new MazeState();
        assertTrue(state.checkMove(Position.Direction.DOWN));
        assertTrue(state.checkMove(Position.Direction.LEFT));
        assertTrue(state.checkMove(Position.Direction.UP));
        assertTrue(state.checkMove(Position.Direction.RIGHT));
    }

    @Test
    void testGetPosition() {
        MazeState state = new MazeState();
        assertEquals(new Position(1, 4), state.getPosition(MazeState.BALL));
    }

    @Test
    void testIsGoal() {
        MazeState state = new MazeState();
        assertFalse(state.isGoal());

        MazeState goalState = new MazeState();
        goalState = goalState.withPositions(List.of(new Position(5, 2)));
        assertTrue(goalState.isGoal());
    }
}

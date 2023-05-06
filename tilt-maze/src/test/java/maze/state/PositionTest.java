package maze.state;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PositionTest {
    @Test
    void testGetNeighbor() {
        Position pos = new Position(1, 1);

        Position neighbor = pos.getNeighbor(Position.Direction.UP);
        assertEquals(new Position(0, 1), neighbor);

        neighbor = pos.getNeighbor(Position.Direction.RIGHT);
        assertEquals(new Position(1, 2), neighbor);

        neighbor = pos.getNeighbor(Position.Direction.DOWN);
        assertEquals(new Position(2, 1), neighbor);

        neighbor = pos.getNeighbor(Position.Direction.LEFT);
        assertEquals(new Position(1, 0), neighbor);
    }

    @Test
    void testToString() {
        Position pos = new Position(3, 4);
        assertEquals("(3,4)", pos.toString());
    }
}

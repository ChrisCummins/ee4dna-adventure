package adventure;

import java.util.HashMap;

import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

/**
 * The maze is a type of room server which offers lazy instantiation of a 2D
 * grid of rooms. Rooms are created on-demand and procedurally. The maze
 * configuration is defined in the maze XML configuration element. The child
 * nodes width and height determine the maze dimensions.
 * 
 * @author Chris Cummins
 */
public final class Maze extends IRoomServerImpl {

    private final DungeonRoomFactory factory;
    private final HashMap<Integer, Room> maze;
    private final int mazeNumberMin;
    private final int mazeNumberMax;

    /**
     * Generate a new maze using the given configuration. Maze properties are
     * defined in the XML configuration file.
     * 
     * @param cfg
     *            The configuration to use.
     * @param poa
     *            A portable object adapter.
     * @throws ConfigParseException
     *             In case of error while parsing the configuration.
     */
    public Maze(Config cfg, POA poa) throws ConfigParseException {
        super(poa);

        final int mazeSize = cfg.getMazeWidth() * cfg.getMazeHeight();

        this.factory = new DungeonRoomFactory(cfg, poa);
        this.maze = new HashMap<Integer, Room>();
        this.mazeNumberMin = -mazeSize / 2;
        this.mazeNumberMax = mazeSize / 2;
    }

    /**
     * Attempts to return a given room within the maze. If the room number is
     * valid, a check is made to ensure the room exists. If it doesn't, it is
     * constructed, and then returned to the user.
     * 
     * @param n
     *            The room number being requested.
     * @throws room_not_found
     *             If the room is out of the maze boundaries.
     */
    @Override
    public Room find_room(int n) throws room_not_found {
        try {
            if (roomNumberIsWithinBounds(n)) {
                if (!maze.containsKey(n))
                    maze.put(n, factory.next(rs, n)); // Lazy instantiation

                return maze.get(n);
            }
        } catch (ServantNotActive | WrongPolicy e) {
            SystemIO.error("getRoom() failed!", e);
        }

        // Fall-back to base class:
        return super.find_room(n);
    }

    // Return whether a room index is "in bounds".
    private boolean roomNumberIsWithinBounds(final int n) {
        return n >= mazeNumberMin && n <= mazeNumberMax;
    }

}

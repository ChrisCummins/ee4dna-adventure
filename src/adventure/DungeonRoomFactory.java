package adventure;

import java.util.Random;

import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import adventure.rooms.DungeonRoom;
import adventure.rooms.MessageWall;

/**
 * A utility class used to procedurally generate random dungeon rooms.
 * 
 * @author Chris Cummins
 */
public class DungeonRoomFactory {

    private final POA poa;

    // Cached configuration options
    private final String[] roomDescriptions;
    private final String user;
    private final int mazeWidth;
    private final int mazeHeight;

    /**
     * Instantiate a new dungeon room factory using the given configuration and
     * POA.
     * 
     * @param cfg
     *            The configuration to use.
     * @param poa
     *            A portable object adapter.
     * @throws ConfigParseException
     *             In case of error while parsing the configuration.
     */
    public DungeonRoomFactory(final Config cfg, final POA poa)
            throws ConfigParseException {
        this.poa = poa;
        this.roomDescriptions = cfg.getDescriptions();
        this.user = cfg.getUser();
        this.mazeWidth = cfg.getMazeWidth();
        this.mazeHeight = cfg.getMazeHeight();
    }

    /**
     * Create a new room with a given room number and callback room server.
     * 
     * @param rs
     *            The callback room server.
     * @param n
     *            The room number.
     * @return A randomly generated maze room.
     * @throws ServantNotActive
     *             In case the POA servant is not active.
     * @throws WrongPolicy
     *             In case the wrong policy is used with the POA.
     */
    public Room next(final CBRoomServer rs, final int n)
            throws ServantNotActive, WrongPolicy {
        SystemIO.log("Generating room " + n);

        final int index = new Random().nextInt(roomDescriptions.length);
        final String description = roomDescriptions[index];
        final RoomImpl room = (n == 0) ? new MessageWall(n, user, rs,
                mazeWidth, mazeHeight) : new DungeonRoom(n, user, description,
                rs, mazeWidth, mazeHeight);
        final org.omg.CORBA.Object o = poa.servant_to_reference(room);

        return RoomHelper.narrow(o);
    }

}

package adventure;

import java.io.Serializable;

import org.omg.PortableServer.POA;

/**
 * The implementation of the IRoomServerPOA, which provides a gateway to the
 * server's rooms.
 * 
 * @author Chris Cummins
 */
public class IRoomServerImpl extends IRoomServerPOA implements Serializable {

    private static final long serialVersionUID = -1055484291122727187L;

    protected transient final POA poa;
    protected transient CBRoomServer rs;

    /**
     * Create a new room server implementation.
     * 
     * @param poa
     *            A portable object adapter.
     */
    public IRoomServerImpl(final POA poa) {
        this.poa = poa;
    }

    /**
     * Set the remote room server callback.
     * 
     * @param rs
     *            The room server object returned by register().
     */
    public CBRoomServer roomServer(final CBRoomServer rs) {
        return this.rs = rs;
    }

    /**
     * Attempts to return a given room number.
     * 
     * @param n
     *            The room number being requested.
     * @throws room_not_found
     *             In case a room does not exist.
     */
    @Override
    public Room find_room(int n) throws room_not_found {
        SystemIO.log("find_room(" + n + ") invoked");

        // Fall-through
        throw new room_not_found("Room '" + n + "' doesn't exist!");
    }

    /**
     * Ping implementation.
     */
    @Override
    public void ping() {
        SystemIO.log("ping() invoked");
    }

}

package adventure.rooms;

import adventure.CBRoomServer;
import adventure.Player;
import adventure.RoomImpl;
import adventure.actions.Dragon;

/**
 * A room type which represents a simple dungeon space, implementing a somewhat
 * bizarre set of instructions. Each maze has a verbose description associated
 * with it.
 * 
 * @author Chris Cummins
 */
public class DungeonRoom extends RoomImpl {

    protected final String description;
    protected final int mazeWidth;
    protected final int mazeHeight;
    protected final int invalidNo;

    /**
     * Construct a new dungeon room.
     * 
     * @param no
     *            The room number.
     * @param user
     *            The username of the room server host.
     * @param description
     *            A string dungeon room description.
     * @param rs
     *            The callback room server.
     * @param mazeWidth
     *            The maze width.
     * @param mazeHeight
     *            The maze height.
     * @param actions
     *            A set of actions that the room implements.
     */
    public DungeonRoom(final int no, final String user,
            final String description, final CBRoomServer rs,
            final int mazeWidth, final int mazeHeight) {

        super(no, user, rs, mazeWidth, mazeHeight);

        this.description = description;
        this.mazeWidth = mazeWidth;
        this.mazeHeight = mazeHeight;
        this.invalidNo = mazeHeight * mazeWidth + 1;

        // Room behaviour extensions:
        this.actions.add(new Dragon(this));
    }

    @Override
    protected void description(final Player p) {
        this.description(p, description);
    }
}

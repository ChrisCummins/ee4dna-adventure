package adventure.rooms;

import java.util.ArrayList;

import adventure.CBRoomServer;
import adventure.Player;
import adventure.RoomImpl;
import adventure.actions.GuessTheNumber;
import adventure.actions.Message;

/**
 * A room type which represents a message wall.
 * 
 * @author Chris Cummins
 */
public class MainRoom extends RoomImpl {

    protected final int mazeWidth;
    protected final int mazeHeight;
    protected final int invalidNo;

    protected final ArrayList<String> messages;

    private final GuessTheNumber game;

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
    public MainRoom(final int no, final String user, final CBRoomServer rs,
            final int mazeWidth, final int mazeHeight) {

        super(no, user, rs, mazeWidth, mazeHeight);

        this.mazeWidth = mazeWidth;
        this.mazeHeight = mazeHeight;
        this.invalidNo = mazeHeight * mazeWidth + 1;
        this.messages = new ArrayList<String>();

        this.game = new GuessTheNumber(this);

        this.actions.add(new Message(this));
        this.actions.add(game);
    }

    @Override
    protected void description(final Player p) {
        this.description(p, "You are facing a large wall, covered in messages.");
    }

    @Override
    public synchronized void player_left(final Player p) {
        game.player_left(p);

        super.player_left(p);
    }
}

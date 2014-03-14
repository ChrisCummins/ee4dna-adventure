package adventure;

import java.util.HashSet;
import java.util.Iterator;

import adventure.actions.Go;
import adventure.actions.Release;
import adventure.actions.Say;
import adventure.actions.Shout;
import adventure.actions.Take;

/**
 * The base room class, offering the bare minimum of functionality to the
 * player, i.e. movement and inter-player communication. Classes may extend this
 * in order to offer additional behaviour. Behaviour is added to rooms by adding
 * items to the actions hash set. When a command is sent to a room, the actions
 * are iterated over until an one successfully processes the command.
 * 
 * @author Chris Cummins
 */
public class RoomImpl extends RoomPOA {

    private static final String[] directions = new String[] { "north", "east",
            "south", "west" };

    // Room properties
    public final int no;
    public final String user;
    public final RoomID id;
    public final ItemLocation location;
    public final CBRoomServer rs;

    // Room state
    protected final HashSet<Action> actions;
    protected final HashSet<Player> players;
    protected final HashSet<Item> items;

    // Maze dimensions
    protected final int mazeSize;
    protected final int mazeWidth;
    protected final int mazeHeight;
    protected final int invalidNo;

    /**
     * Construct a new basic room which implements the specified set of actions.
     * 
     * @param no
     *            The room number.
     * @param user
     *            The username of the room server host.
     * @param rs
     *            The callback room server.
     * @param mazeWidth
     *            The maze width.
     * @param mazeHeight
     *            The maze height.
     * @param actions
     *            A set of actions that the room implements.
     */
    public RoomImpl(final int no, final String user, final CBRoomServer rs,
            final int mazeWidth, final int mazeHeight,
            final HashSet<Action> actions) {

        this.no = no;
        this.user = user;
        this.id = new RoomID(user, no);
        this.location = new ItemLocation();
        this.rs = rs;

        location.r(this.id);

        this.actions = actions;
        this.players = new HashSet<Player>();
        this.items = new HashSet<Item>();

        this.mazeSize = mazeWidth * mazeHeight;
        this.mazeWidth = mazeWidth;
        this.mazeHeight = mazeHeight;
        this.invalidNo = mazeHeight * mazeWidth + 1;

        // Default room behaviour
        this.actions.add(new Go(this));
        this.actions.add(new Shout(this));
        this.actions.add(new Say(this));
        this.actions.add(new Take(this));
        this.actions.add(new Release(this));
    }

    /**
     * Construct a new basic room.
     * 
     * @param no
     *            The room number.
     * @param user
     *            The username of the room server host.
     * @param rs
     *            The callback room server.
     * @param mazeWidth
     *            The maze width.
     * @param mazeHeight
     *            The maze height.
     */
    public RoomImpl(final int no, final String user, final CBRoomServer rs,
            final int mazeWidth, final int mazeHeight) {
        this(no, user, rs, mazeWidth, mazeHeight, new HashSet<Action>());
    }

    /*
     * Call-backs
     */

    @Override
    public void player_entered(final Player p) {
        SystemIO.log("player_entered(" + p.user_name() + ")");

        // Notify other players of arrival
        for (final Player player : players)
            sendMessage(player, p.real_name() + " has entered the room.");

        this.players.add(p);

        description(p); // Describe room to new player
    }

    @Override
    public void player_left(final Player p) {
        SystemIO.log("player_left(" + p.user_name() + ")");

        this.players.remove(p);

        // Notify other players of leaving
        for (final Player other : players)
            sendMessage(other, p.real_name() + " has left the room.");
    }

    @Override
    public void item_added(final Item i) {
        SystemIO.log("item_added(" + i.item_name() + ")");
        items.add(i);
    }

    @Override
    public void item_removed(final Item i) {
        SystemIO.log("item_removed(" + i.item_name() + ")");
        items.remove(i);
    }

    @Override
    public void send_command(final Player p, final String command) {
        final String s = command.trim().toLowerCase();

        // Built in commands
        if (s.matches("^help$")) {
            help(p);
            return;
        } else if (s.matches("^description$")) {
            description(p);
            return;
        }

        // Actions functionality
        for (final Action a : actions) {
            if (a.process(p, command))
                return;
        }

        // Fall-through
        sendMessage(p, "Unrecognised command!");
    }

    @Override
    public void ping() {
        SystemIO.log("ping() invoked on Room '" + user + "'");
    }

    /*
     * Public functionality:
     */

    /**
     * Send a message to a specific user.
     * 
     * @param p
     *            Message recipient.
     * @param msg
     *            Message contents.
     */
    public void sendMessage(final Player p, final String[] msg) {
        try {
            if (players.contains(p)) // Only send message if player is in the
                                     // room
                rs.send_message(no, p, msg);
        } catch (room_not_found e) {
            SystemIO.error("Room not found!", e);
        } catch (player_not_in_room e) {
            SystemIO.error("Player not in room!", e);
            player_left(p); // Remove from our list
        }
    }

    /**
     * Send a message to a specific user.
     * 
     * @param p
     *            Message recipient.
     * @param msg
     *            Message contents.
     */
    public void sendMessage(final Player p, final String msg) {
        sendMessage(p, new String[] { msg });
    }

    /**
     * Send a message to all of the players within the room.
     * 
     * @param msg
     *            Message contents.
     */
    public void sendMessage(final String[] msg) {
        for (final Player p : players)
            sendMessage(p, msg);
    }

    /**
     * Send a message to all of the players within the room.
     * 
     * @param msg
     *            Message contents.
     */
    public void sendMessage(final String msg) {
        for (final Player p : players)
            sendMessage(p, new String[] { msg });
    }

    /**
     * Broadcast a message to all of the players on the server.
     * 
     * @param msg
     *            Message contents.
     */
    public void broadcastMessage(final String[] msg) {
        try {
            rs.broadcast_message(no, msg);
        } catch (room_not_found e) {
            SystemIO.error("Room not found!", e);
        }
    }

    /**
     * Broadcast a message to all of the players on the server.
     * 
     * @param msg
     *            Message contents.
     */
    public void broadcastMessage(final String msg) {
        broadcastMessage(new String[] { msg });
    }

    /**
     * Send a multi-line message to a specific player, one line at a time.
     * 
     * @param p
     *            Message recipient.
     * @param msg
     *            Message contents.
     */
    public void scrollMessage(final Player p, final String[] msg) {
        try {
            for (int i = 0; i < msg.length; i++) {
                sendMessage(p, msg[i]);
                Thread.sleep(1250);
            }
        } catch (InterruptedException e) {
            SystemIO.error("Scroll message interrupted!", e);
        }
    }

    /**
     * Move a player from the current room to a given destination.
     * 
     * @param p
     *            Player to move.
     * @param dest
     *            Destination.
     */
    public void movePlayer(final Player p, final String dest) {
        if (destinationIsRelativeDirection(dest))
            movePlayer(p, new RoomID(user, destinationToRoomNumber(dest)));
        else
            movePlayer(p, new RoomID(dest, 0));
    }

    /**
     * Move a player from the current room to a given destination.
     * 
     * @param p
     *            Player to move.
     * @param dest
     *            Destination.
     */
    public void movePlayer(final Player p, final RoomID dest) {
        try {
            rs.move_player(no, p, dest);
        } catch (room_not_found e) {
            sendMessage(p, "You can't go there");
        } catch (cant_move_player e) {
            SystemIO.error("Can't move player!", e);
        }
    }

    /**
     * Send the help text to a given player.
     * 
     * @param p
     *            Recipient player.
     */
    protected void help(final Player p) {
        sendMessage(p, getActionsHelpText());
    }

    /**
     * Send the room description text to a given player.
     * 
     * @param p
     *            Recipient player.
     */
    protected void description(final Player p) {
        sendMessage(p, "");
    }

    /**
     * Send the room description text to a given player.
     * 
     * @param p
     *            Recipient player.
     * @param message
     *            A description message prefix.
     */
    protected void description(final Player p, final String message) {
        String s = message.length() > 0 ? message + " " : "";

        if (players.size() > 1)
            s += "You are not alone";
        else
            s += "You are alone";

        s += ". From here, you can go ";

        for (final String d : directions) {
            if (destinationToRoomNumber(d) != invalidNo)
                s += d + ", ";
        }

        sendMessage(p, s.substring(0, s.length() - 2) + ".");
    }

    /**
     * Convert a relative destination into an absolute room number.
     * 
     * @param destination
     *            Destination string.
     * @return The destination room number, or invalidNo if destination is nor
     *         relative.
     */
    private int destinationToRoomNumber(final String destination) {
        final int x = (no + mazeWidth / 2) % mazeWidth;       
        final int minRoomNumber = -mazeSize / 2;
        final int maxRoomNumber = mazeSize / 2;

        if (destination.matches("north"))
            return no + mazeWidth <= maxRoomNumber ? no + mazeWidth : invalidNo;
        else if (destination.matches("east"))
            return (x + 1) % mazeWidth > x && (no + 1) <= maxRoomNumber ? no + 1 : invalidNo;
        else if (destination.matches("south"))
            return no - mazeWidth >= minRoomNumber ? no - mazeWidth : invalidNo;
        else if (destination.matches("west"))
            return (x - 1) % mazeWidth < x && (no - 1) >= minRoomNumber ? no - 1 : invalidNo;
        else
            return invalidNo; // Destination is not a relative direction
    }

    // Match a relative direction
    private static boolean destinationIsRelativeDirection(final String direction) {
        return direction.matches("(north)|(east)|(south)|(west)");
    }

    // Get the help text for a given set of actions.
    private String[] getActionsHelpText() {
        String[] s = new String[actions.size() + 2];
        int i = 2;

        s[0] = "/help                     - show this text";
        s[1] = "/description              - describe the room";

        for (Iterator<Action> it = actions.iterator(); it.hasNext();) {
            s[i] = it.next().getHelpText();
            i++;
        }

        return s;
    }

}

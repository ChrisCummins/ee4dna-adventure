package adventure.actions;

import adventure.Action;
import adventure.Player;
import adventure.RoomImpl;

/**
 * Movement command.
 */
public class Go implements Action {

    private final RoomImpl room;

    public Go(final RoomImpl room) {
        this.room = room;
    }

    @Override
    public boolean process(final Player p, final String cmd) {

        if (!cmd.matches("^go\\s+.*"))
            return false;

        room.movePlayer(p, cmd.replaceFirst("go\\s+", ""));

        return true;
    }

    @Override
    public String getHelpText() {
        return "/go <room|username>       - move to a new room";
    }

}

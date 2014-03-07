package adventure.actions;

import adventure.Action;
import adventure.Player;
import adventure.RoomImpl;

/**
 * Inter-room communication.
 */
public class Shout implements Action {

    private final RoomImpl room;

    public Shout(final RoomImpl room) {
        this.room = room;
    }

    @Override
    public boolean process(final Player p, final String cmd) {

        if (!cmd.matches("^shout\\s.+"))
            return false;

        room.broadcastMessage(cmd.replaceFirst("shout\\s+", p.real_name()
                + " shouts: "));

        return true;
    }

    @Override
    public String getHelpText() {
        return "/shout <message>          - shout a message to everyone";
    }

}

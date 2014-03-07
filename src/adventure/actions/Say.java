package adventure.actions;

import adventure.Action;
import adventure.Player;
import adventure.RoomImpl;

/**
 * Inter-player communication.
 */
public class Say implements Action {

    private final RoomImpl room;

    public Say(final RoomImpl room) {
        this.room = room;
    }

    @Override
    public boolean process(final Player p, final String cmd) {

        if (!cmd.matches("^say\\s.+"))
            return false;

        room.sendMessage(cmd.replaceFirst("say\\s+", p.real_name() + " says: "));

        return true;
    }

    @Override
    public String getHelpText() {
        return "/say <message>            - send a message to everyone";
    }

}

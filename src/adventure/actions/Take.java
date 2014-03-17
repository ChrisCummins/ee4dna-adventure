package adventure.actions;

import adventure.Action;
import adventure.ItemLocation;
import adventure.Player;
import adventure.RoomImpl;
import adventure.SystemIO;
import adventure.cant_move_item;
import adventure.room_not_found;

/**
 * Item take.
 */
public class Take implements Action {

    private final RoomImpl room;

    public Take(final RoomImpl room) {
        this.room = room;
    }

    @Override
    public boolean process(final Player p, final String cmd) {

        if (!cmd.matches("^take\\s+.*$"))
            return false;

        try {
            final ItemLocation location = new ItemLocation();
            location.p(p);

            room.rs.move_item(room.no, p, Integer.parseInt(cmd.replaceFirst(
                    "take\\s+", "")), location);
            room.sendMessage(p.real_name() + " picked up an item");
        } catch (NumberFormatException e) {
            room.sendMessage(p, "Not a valid item number!");
        } catch (cant_move_item e) {
            room.sendMessage(p, "You're not holding that item!");
        } catch (room_not_found e) { // Server-side fuck up
            SystemIO.error("Failed to drop item!", e);
        }

        return true;
    }

    @Override
    public String getHelpText() {
        return "/release <id>             - drop an item in the room";
    }

}

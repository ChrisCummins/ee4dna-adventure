package adventure.actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import adventure.Action;
import adventure.Player;
import adventure.RoomImpl;
import adventure.SystemIO;

/**
 * Command to read and write persistent messages.
 */
public class Message implements Action {

    private final RoomImpl room;
    private final File store;

    public Message(final RoomImpl room) {
        this.room = room;
        this.store = new File(room.no + "-messages.log");
    }

    @Override
    public boolean process(final Player p, final String cmd) {

        if (cmd.matches("^write\\s+.*")) {
            writeMessage(p, cmd.replaceFirst("write\\s+", ""));
            room.sendMessage(p.real_name() + " wrote a message on the wall");
            return true;
        } else if (cmd.matches("^read$")) {
            room.sendMessage(p, readMessages());
            return true;
        }

        return false;
    }

    /**
     * Add a new player message
     * 
     * @param p
     *            The message author
     * @param message
     *            String
     */
    private synchronized void writeMessage(final Player p, final String message) {
        try {
            final PrintWriter out = new PrintWriter(new BufferedWriter(
                    new FileWriter(store, true)));
            out.write("\"" + message + "\" - " + p.real_name() + "\n");
            out.close();
            SystemIO.log(store.getPath() + " updated");
        } catch (IOException e) {
            SystemIO.error("Failed to write message!", e);
        }
    }

    /**
     * Return the player messages
     * 
     * @return One message per element.
     */
    private synchronized String[] readMessages() {
        try {
            final BufferedReader br = new BufferedReader(new FileReader(store));

            final StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }

            br.close();

            return sb.toString().split("\n");
        } catch (IOException e) {
            return new String[] { "There's nothing written on the wall" };
        }
    }

    @Override
    public String getHelpText() {
        return "/read OR /write <message>   - read and write messages on the wall";
    }

}

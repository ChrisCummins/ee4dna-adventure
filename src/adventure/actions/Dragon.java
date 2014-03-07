package adventure.actions;

import adventure.Action;
import adventure.Player;
import adventure.RoomImpl;

/**
 * Rawr, a dragon.
 */
public class Dragon implements Action {

    private static final String[] dragon = new String[] {
            "    ^                       ^", "    |\\   \\        /        /|",
            "   /  \\  |\\__  __/|       /  \\",
            "  / /\\ \\ \\ _ \\/ _ /      /    \\",
            " / / /\\ \\ {*}\\/{*}      /  / \\ \\",
            " | | | \\ \\( (00) )     /  // |\\ \\",
            " | | | |\\ \\(V\"\"V)\\    /  / | || \\| ",
            " | | | | \\ |^--^| \\  /  / || || || ",
            "/ / /  | |( WWWW__ \\/  /| || || ||",
            "| | | | | |  \\______\\  / / || || || ",
            "| | | / | | )|______\\ ) | / | || ||",
            "/ / /  / /  /______/   /| \\ \\ || ||",
            "/ / /  / /  /\\_____/  |/ /__\\ \\ \\ \\ \\",
            "| | | / /  /\\______/    \\   \\__| \\ \\ \\",
            "| | | | | |\\______ __    \\_    \\__|_| \\",
            "| | ,___ /\\______ _  _     \\_       \\  |",
            "| |/    /\\_____  /    \\      \\__     \\ |    /\\",
            "|/ |   |\\______ |      |        \\___  \\ |__/  \\",
            "v  |   |\\______ |      |            \\___/     |",
            " |   |\\______ |      |                    __/",
            "  \\   \\________\\_    _\\               ____/",
            "__/   /\\_____ __/   /   )\\_,      _____/",
            "/  ___/  \\uuuu/  ___/___)    \\______/", "VVV  V        VVV  V ",
            "", " RRRRRRAAAAAWWWR, A DRAGON!" };

    private final RoomImpl room;

    public Dragon(final RoomImpl room) {
        this.room = room;
    }

    @Override
    public boolean process(final Player p, final String cmd) {

        if (!cmd.matches("^show\\s+me\\s+a\\s+dragon$"))
            return false;

        room.sendMessage(p, dragon);

        return true;
    }

    @Override
    public String getHelpText() {
        return "/show me a dragon         - best command evah";
    }

}

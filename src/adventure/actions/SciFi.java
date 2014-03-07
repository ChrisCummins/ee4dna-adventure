package adventure.actions;

import adventure.Action;
import adventure.Player;
import adventure.RoomImpl;

/**
 * The Asimov of actions.
 */
public class SciFi implements Action {

    private static final String[] starWars = new String[] {
            "A long time ago, in a galaxy far, far away....", "",
            "It is a period of civil war. Rebel",
            "spaceships, striking from a hidden",
            "base, have won their first victory",
            "against the evil Galactic Empire.", "",
            "During the battle, Rebel spies managed",
            "to steal secret plans to the Empire's",
            "ultimate weapon, the Death Star, an",
            "armored space station with enough",
            "power to destroy an entire planet.", "",
            "Pursued by the Empire's sinister agents,",
            "Princess Leia races home aboard her",
            "starship, custodian of the stolen plans",
            "that can save her people and restore", "freedom to the galaxy..." };

    private final RoomImpl room;

    public SciFi(final RoomImpl room) {
        this.room = room;
    }

    @Override
    public boolean process(final Player p, final String cmd) {

        if (!cmd.matches("^how\\s+does\\s+star\\s+wars\\s+(start|begin)(\\?)?$"))
            return false;

        room.scrollMessage(p, starWars);

        return true;
    }

    @Override
    public String getHelpText() {
        return "/how does star wars begin? - refresh your memory";
    }

}

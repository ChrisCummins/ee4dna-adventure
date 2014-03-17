package adventure.actions;

import java.util.Random;

import adventure.Action;
import adventure.Player;
import adventure.RoomImpl;
import adventure.SystemIO;

/**
 * Guess the number game.
 */
public class GuessTheNumber implements Action {

    private final RoomImpl room;

    private String playerName;
    private int num;

    public GuessTheNumber(final RoomImpl room) {
        this.room = room;
        this.playerName = "";
    }

    @Override
    public boolean process(final Player p, final String cmd) {

        if (cmd.matches("^play guess the number")) {
            if (playerName == "") {
                playerName = p.real_name();
                num = new Random().nextInt(100);
                room.sendMessage(p,
                        "Welcome to guess the number! Type /guess followed by your first guess");
                SystemIO.log("gtn: Player " + p.user_name() + " started game");
            } else
                room.sendMessage(p, "Sorry, someone else is already playing!");

            return true;
        } else if (cmd.matches("^guess\\s+.*")) {

            if (p.real_name().matches(playerName))
                guess(p, cmd.replaceFirst("guess\\s+", ""));
            else {
                if (playerName.matches("")) {
                    room.sendMessage(p, "You're not playing a game!");
                } else {
                    room.sendMessage(p, playerName + " is already playing!");
                    SystemIO.log("gtn: Locked " + p.user_name()
                            + " from existing game");
                }
            }

            return true;
        }

        return false;
    }

    private void guess(final Player p, final String guessString) {
        try {
            int guess = Integer.parseInt(guessString);

            if (guess == num) {
                room.sendMessage(p, "Well done! You got it right. End of game.");
                SystemIO.log("gtn: Player " + p.user_name() + " finished game");
                playerName = "";
            } else if (guess > num)
                room.sendMessage(p, "You guessed too high");
            else
                room.sendMessage(p, "You guessed too low");
        } catch (NumberFormatException e) {
            room.sendMessage(p, "Not a number! " + guessString);
        }
    }

    @Override
    public String getHelpText() {
        return "/play guess the number      - play a round of guess the number!";
    }

    public void player_left(final Player p) {
        if (p.real_name().matches(playerName)) {
            SystemIO.log("gtn: Player " + p.user_name() + " left room");
            playerName = "";
        }
    }

}

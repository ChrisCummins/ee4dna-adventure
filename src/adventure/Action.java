package adventure;

import adventure.Player;

/**
 * The Action interface used to assign an action or behaviour to a room. A class
 * which implements this method must support a command which can be called by
 * the user.
 */
public interface Action {

    /**
     * Attempt to perform an action from the given command. If the action is
     * capable of processing the command, then this method will return true, and
     * may have side effects. Otherwise, it is expected that the method should
     * return false.
     * 
     * @param cmd
     *            The command string to process. E.g. "say Hello"
     * @return True if the action can process the command, else false.
     */
    public boolean process(final Player p, final String cmd);

    /**
     * Return a brief help text for the given action.
     * 
     * @return A string action description.
     */
    public String getHelpText();
}

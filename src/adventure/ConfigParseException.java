package adventure;

/**
 * A checked exception type representing an error in config file parsing. This
 * could be caused by invalid XML syntax within the configuration file, or due
 * to a missing required element or invalid property value.
 * 
 * @author Chris Cummins
 */
public class ConfigParseException extends Exception {

    private static final long serialVersionUID = 1583585327302205187L;

    /**
     * Create a configuration parse exception with the given message string.
     * Messages should describe the cause of the error.
     * 
     * @param message
     *            The message string.
     */
    public ConfigParseException(String message) {
        super(message);
    }

    /**
     * Generate a configuration parse exception with a default error message.
     */
    public ConfigParseException() {
        this("Failed to parse configuration file!");
    }
}

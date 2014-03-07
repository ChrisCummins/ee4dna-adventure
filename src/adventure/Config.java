package adventure;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The configuration object for a room server. This class provides a transparent
 * API which wraps around the underlying XML configuration, parsing the values
 * contained within a given file. The only time that this transparency is broken
 * is in the case of an error during the file parsing, hence the
 * ConfigParseException is used liberally to provide a mechanism for the calling
 * method to recover from such an error.<br/>
 * </br/>
 * 
 * At a bare minimum, the configuration file should obey the standard XML
 * syntax, and contain these fundamental components:</br/></br/>
 * 
 * <pre>
 * {@code
 * <?xml version="1.0"?>
 * 
 * <adventure>
 *   <credentials>
 *     <host><!-- game server url --></host>
 *     <user><!-- user name --></user>
 *     <key><!-- secret hash --></key>
 *   </credentials>
 * 
 *   <rooms>
 *     <room id="Main"></room>
 *   </rooms>
 * </adventure>}
 * </pre>
 * 
 * @author Chris Cummins
 */
public class Config {

    private final Document doc;
    private final Element credentials;
    private final Element maze;

    /**
     * Constructs a configuration from a given XML file. The implementation uses
     * a DOM style parser, providing lazy evaluation of properties on an
     * as-needed basis.
     * 
     * @param file
     *            The configuration file to parse.
     * @throws ConfigParseException
     *             In case of error while parsing the file.
     * @throws FileNotFoundException
     *             In case the given file does not exist.
     */
    public Config(final File file) throws ConfigParseException,
            FileNotFoundException {
        if (!file.exists()) { // Sanity check for config file
            throw new FileNotFoundException("Configuration file '"
                    + file.getAbsolutePath() + "' not found!");
        }

        try {
            DocumentBuilderFactory fb = DocumentBuilderFactory.newInstance();

            doc = fb.newDocumentBuilder().parse(file);
            doc.getDocumentElement().normalize();

            credentials = getElement(doc, "credentials");
            maze = getElement(doc, "maze");
        } catch (Exception e) {
            throw new ConfigParseException(
                    "Failed to parse configuration file '" + file.getName()
                            + "'!");
        }
    }

    /**
     * Constructions a configuration from the XML file at the given path.
     * 
     * @param path
     *            A relative or absolute path to a configuration file.
     * @throws ConfigParseException
     *             In case of error while parsing the file.
     * @throws FileNotFoundException
     *             In case the given file does not exist.
     */
    public Config(final String path) throws ConfigParseException,
            FileNotFoundException {
        this(new File(path));
    }

    /**
     * Retrieves the URL of the game server. It is set within the credentials
     * tag as a string enclosed within host tags.
     * 
     * @return The game server host, as a string.
     * @throws ConfigParseException
     *             In case of error while parsing the configuration file.
     */
    public String getGameServerHost() throws ConfigParseException {
        return getElement(credentials, "host");
    }

    /**
     * Retrieves the user name for the game server session. The user name is set
     * within the credentials tag as a string enclosed within user tags.
     * 
     * @return User name, as a string.
     * @throws ConfigParseException
     *             In case of error while parsing the configuration file.
     */
    public String getUser() throws ConfigParseException {
        return getElement(credentials, "user");
    }

    /**
     * Retrieves the secret hash from the configuration. The hash is set within
     * the credentials tag as a string enclosed within key tags.
     * 
     * @return Secret hash as a string.
     * @throws ConfigParseException
     *             In case of error while parsing the configuration file.
     */
    public String getSecretHash() throws ConfigParseException {
        return getElement(credentials, "key");
    }

    public int getMazeWidth() throws ConfigParseException {
        try {
            return Integer.parseInt(getElement(maze, "width"));
        } catch (NumberFormatException e) {
            throw new ConfigParseException("Maze width is not a number!");
        }
    }

    public int getMazeHeight() throws ConfigParseException {
        try {
            return Integer.parseInt(getElement(maze, "height"));
        } catch (NumberFormatException e) {
            throw new ConfigParseException("Maze height is not a number!");
        }
    }

    public String[] getDescriptions() throws ConfigParseException {
        final NodeList list = getElements("rooms", "description");
        final String[] descriptions = new String[list.getLength()];

        for (int i = 0; i < descriptions.length; i++) {
            final Element n = (Element) list.item(i);
            descriptions[i] = n.getTextContent().replaceAll("\n", "")
                    .replaceAll("\\s+", " ");
            ;
        }

        return descriptions;
    }

    /*
     * DOM traversal methods:
     */

    private Element getElement(final Document root, final String name)
            throws ConfigParseException {
        try {
            return (Element) root.getElementsByTagName(name).item(0);
        } catch (Exception e) {
            throw new ConfigParseException("Failed to retrieve element '"
                    + name + "'!");
        }
    }

    private Element getElement(final String name) throws ConfigParseException {
        return getElement(doc, name);
    }

    private String getElement(final Element parent, final String name)
            throws ConfigParseException {
        try {
            return parent.getElementsByTagName(name).item(0).getTextContent();
        } catch (Exception e) {
            throw new ConfigParseException("Failed to retrieve element '"
                    + name + "'!");
        }
    }

    private NodeList getElements(final Element parent, final String name)
            throws ConfigParseException {
        try {
            return parent.getElementsByTagName(name);
        } catch (Exception e) {
            throw new ConfigParseException("Failed to retrieve elements '"
                    + name + "'!");
        }
    }

    private NodeList getElements(final String parent, final String name)
            throws ConfigParseException {
        return getElements(getElement(parent), name);
    }
}

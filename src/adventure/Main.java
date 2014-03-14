package adventure;

import java.io.File;
import java.io.FileNotFoundException;

import org.omg.CORBA.ORBPackage.InvalidName;

/**
 * The main executable unit of the room server.
 * 
 * @author Chris Cummins
 */
public abstract class Main {

    /**
     * The default configuration file to use. The RoomServer shell script
     * executes the program from within a run/ subdirectory, so we look for
     * adventure.xml in the parent dir.
     */
    public static final String DEFAULT_CONFIG = "../adventure.xml";

    public static final String DEFAULT_SAVE = "../adventure.sav";

    /**
     * Run a room server. Optionally can be provided with a path to a
     * configuration file, else uses the default value.
     * 
     * @param args
     *            Run time arguments. If arguments contain one or more strings,
     *            then the first one will be used as the path to the
     *            configuration file.
     */
    public static void main(String[] args) {
        try {
            SystemIO.log("Reading configuration file...");
            final Config config = new Config(getConfigPath(args));
            final RoomServerProcess ps;
            
            if (fileExists(getSavePath(args))) {
                SystemIO.log("Reading save file...");
                
                SystemIO.log("Server is starting up...");
                ps = new RoomServerProcess(config);
            } else {
                SystemIO.log("Server is starting up...");
                ps = new RoomServerProcess(config);
            }

            SystemIO.log("Server is ready..");
            ps.run();

        } catch (FileNotFoundException | ConfigParseException e) {
            SystemIO.fatal_error(e.getMessage());
        } catch (org.omg.CORBA.BAD_PARAM e) {
            SystemIO.fatal_error("Invalid game server host!");
        } catch (org.omg.CORBA.COMM_FAILURE e) {
            SystemIO.fatal_error("Failed to connect to game server!");
        } catch (org.omg.CORBA.UNKNOWN e) {
            SystemIO.fatal_error("Server side error!");
        } catch (registration_failed e) {
            SystemIO.fatal_error("Failed to register user!");
        } catch (InvalidName e) {
            SystemIO.fatal_error("Could not find POA!");
        } catch (Exception e) {
            // Default uncaught exception handler:
            SystemIO.fatal_error("Uncaught exception!", e);
        }
    }

    private static String getConfigPath(String[] args) {
        return args.length >= 1 ? args[0] : DEFAULT_CONFIG;
    }

    private static String getSavePath(String[] args) {
        return args.length >= 2 ? args[1] : DEFAULT_SAVE;
    }

    private static boolean fileExists(final String path) {
        return new File(path).isFile();
    }
}

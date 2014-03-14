package adventure;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import java.util.logging.Logger;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

/**
 * A thread of serving room requests, which handles the network IO and acts as
 * middleware between the game server and individual rooms.
 * 
 * @author Chris Cummins
 */
public final class RoomServerProcess extends Thread {

    private static final String CORBA_TRANSPORT = "javax.enterprise.resource.corba._DEFAULT_.rpc.transport";

    private final Config cfg;
    private final ORB orb;
    private final POA poa;

    private final GameServer gs;
    private final CBRoomServer rs;
    private final IRoomServerImpl rsi;

    private boolean shutdown_started = false;

    /**
     * Create a room server process.
     * 
     * @param config
     *            The server configuration.
     * @throws registration_failed
     *             In case the attempt to register a user with the game server
     *             fails.
     * @throws WrongPolicy
     *             In case of an error in instantiating the room server
     *             implementation.
     * @throws ServantNotActive
     *             In case of invalid properties or security protocols.
     * @throws ConfigParseException
     *             In case of an error while parsing the configuration file.
     * @throws AdapterInactive
     *             In case of an error while activating the portable object
     *             adapter.
     * @throws InvalidName
     *             In case a service cannot be found.
     */
    public RoomServerProcess(final Config config) throws registration_failed,
            ServantNotActive, WrongPolicy, ConfigParseException,
            AdapterInactive, InvalidName {

        setCorbaLogLevel(java.util.logging.Level.SEVERE); // Log everything

        this.cfg = config;
        this.orb = getORB();
        this.poa = getPOA();

        this.gs = getGameServer(); // Connect to game server
        this.rsi = getIRoomServerImpl();
        this.rs = rsi.roomServer(getRoomServer()); // Register self with game
                                                   // server

        // Register shutdown hook
        Runtime.getRuntime().addShutdownHook(new ShutdownThread());
    }

    /**
     * Begin the server process. Never returns.
     */
    @Override
    public void run() {
        orb.run();
        SystemIO.log("Server shutdown complete");
    }

    /*
     * Private helper getters:
     */

    private void setCorbaLogLevel(java.util.logging.Level level) {
        final Logger logger = Logger.getLogger(CORBA_TRANSPORT);
        logger.setLevel(level);
    }

    private ORB getORB() {
        return ORB.init(new String[0], new Properties());
    }

    private POA getPOA() throws InvalidName, AdapterInactive {
        org.omg.CORBA.Object o = orb.resolve_initial_references("RootPOA");
        final POA poa = POAHelper.narrow(o);

        poa.the_POAManager().activate();

        return poa;
    }

    private GameServer getGameServer() throws ConfigParseException {
        final String host = cfg.getGameServerHost();
        final org.omg.CORBA.Object o = orb.string_to_object(host);

        return GameServerHelper.narrow(o);
    }

    private CBRoomServer getRoomServer() throws ConfigParseException,
            ServantNotActive, WrongPolicy, registration_failed {
        final String user = cfg.getUser();
        final String hash = cfg.getSecretHash();
        final IRoomServer s = getIRoomServer();

        return gs.register(user, hash, s);
    }

    private IRoomServerImpl getIRoomServerImpl() throws ConfigParseException {
        return (IRoomServerImpl) new Maze(cfg, poa);
    }

    private IRoomServer getIRoomServer() throws ServantNotActive, WrongPolicy {
        org.omg.CORBA.Object o = poa.servant_to_reference(rsi);

        return IRoomServerHelper.narrow(o);
    }

    /**
     * Tidies up at the end of execution. Responsible for unregistering the room
     * server.
     * 
     * @author Chris Cummins
     */
    private class ShutdownThread extends Thread {

        /**
         * Perform shutdown.
         */
        @Override
        public void run() {
            if (!shutdown_started) {

                shutdown_started = true;
                SystemIO.warning("Server shutdown started");
                
                try {
                    SystemIO.log("Saving state to 'adventure.sav'");
                    FileOutputStream fileOut =
                            new FileOutputStream("adventure.sav");
                            ObjectOutputStream out = new ObjectOutputStream(fileOut);
                            out.writeObject(rsi);
                            out.close();
                            fileOut.close();
                    
                    rs.unregister(); // Unregister with game server
                    orb.shutdown(false); // Initiate ORB shutdown
                } catch (Exception e) {
                    SystemIO.fatal_error("Failed to shutdown server!");
                }
            }
        }
    }

    /**
     * Wrapper to Main.main(String[] args), required for the 'RunServer' script.
     * 
     * @param args
     *            Run time arguments, passed to Main.main().
     */
    public static void main(String[] args) {
        Main.main(args);
    }
}

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
* @title  OnlineUsers
* @description Maintains list of online users in a mysql table
* @date 2010-12-01
* @author croemmich
*/
public class OnlineUsers extends Plugin  {
	private Listener l 						   = new Listener(this);
	protected static final Logger log 		   = Logger.getLogger("Minecraft");
	public static String name 				   = "OnlineUsers";
	public static String version 			   = "1.3";
	public static String propFile 			   = "online-users.properties";
	public static String hModProps			   = "server.properties";
	
	public static PropertiesFile props;
	
	public static String dataSource            = "mysql";
    public static boolean hmodMySql            = true;
    public static String driver                = "com.mysql.jdbc.Driver";
    public static String user                  = "root";
    public static String pass                  = "root";
    public static String db                    = "jdbc:mysql://localhost:3306/minecraft";
    public static String table                 = "users_online";
    public static boolean removeOfflineUsers   = true;
    public static boolean removeBannedUsers    = true;
    public static String connectorJar          = "mysql-connector-java-bin.jar";
    public static String destination           = "mysql";
    public static String flatfile              = "online_users.txt";
    public static String flatfileTemplate      = "online_users.template";
    public static String flatfileData          = "online_users.data";
    
    private static ArrayList<String> bannedUsers	   = new ArrayList<String>();
    private static OnlineUsersDataSource ds;
    
	public void enable() {
		if (!initProps()) {
			log.severe(name + ": Could not initialise " + propFile);
			this.disable();
			return;
		}
		
		if (destination.equalsIgnoreCase("mysql")) {
			ds = new OnlineUsersMySQL();
		} else {
			ds = new OnlineUsersFlatfile();
		}
		
		if (!ds.init()) {
			log.severe(name + ": Could not init the datasource");
			this.disable();
			return;
		}

		ds.setAllOffline();
		initOnlineUsers();
		log.info(name + " " + version + " enabled");
	}
	
	public void disable() {
		ds.setAllOffline();
		if (removeOfflineUsers) {
			ds.removeAllUsers();
		}
		log.info(name + " " + version + " disabled");
	}
	
	public void initialize() {
		etc.getLoader().addListener( PluginLoader.Hook.DISCONNECT, l, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener( PluginLoader.Hook.LOGIN, l, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener( PluginLoader.Hook.BAN, l, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener( PluginLoader.Hook.IPBAN, l, this, PluginListener.Priority.MEDIUM);
	}
	
	public boolean initProps() {
		props = new PropertiesFile(hModProps);
		dataSource = props.getString("data-source", dataSource);
		
		props = new PropertiesFile(propFile);
		destination = props.getString("destination", "mysql");
		flatfile = props.getString("flatfile", "online_users.txt");
		flatfileTemplate = props.getString("flatfile-template", "online_users.template");
		flatfileData = props.getString("flatfile-data", "online_users.data");
		hmodMySql = props.getBoolean("use-hmod-mysql-conn", true);
		driver = props.getString("driver", "com.mysql.jdbc.Driver");
        user = props.getString("user", "root");
        pass = props.getString("pass", "root");
        db = props.getString("db", "jdbc:mysql://localhost:3306/minecraft");
        table = props.getString("table", "users_online");
        connectorJar = props.getString("mysql-connector-jar", connectorJar);
        removeOfflineUsers = props.getBoolean("remove-offline-users", true);
        removeBannedUsers = props.getBoolean("remove-banned-users", true);
        
        File file = new File(propFile);
        return file.exists();
	}
	
	public void addBannedUser(String name) {
		bannedUsers.add(name);
	}
	
	public boolean isBannedUser (String name) {
		return bannedUsers.contains(name);
	}
	
	public void removeBannedUser (String name) {
		bannedUsers.remove(name);
	}
	
	public void initOnlineUsers() {
		List<Player> players = etc.getServer().getPlayerList();
		Iterator<Player> itr = players.iterator();
		while (itr.hasNext()) {
			ds.addUser(itr.next().getName());
		}
	}
	
	public class Listener extends PluginListener {
		OnlineUsers p;
		
		public Listener(OnlineUsers plugin) {
			p = plugin;
		}

		public void onLogin(Player player) {
			ds.addUser(player.getName());
		}

		public void onDisconnect(Player player) {
			if (removeOfflineUsers || (removeBannedUsers && isBannedUser(player.getName())))
				ds.removeUser(player.getName());
			else {
				ds.setUserOffline(player.getName());
			}
		}
		
		@Override
		public synchronized void onBan(Player mod, Player player, String reason) {
			addBannedUser (player.getName());
			this.onDisconnect(player);
			removeBannedUser(player.getName());
		}

		@Override
		public synchronized void onIpBan(Player mod, Player player, String reason) {
			onBan(mod, player, reason);
		}
	}
}
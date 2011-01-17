
package org.bukkit.croemmich.onlineusers;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

/**
* @title  OnlineUsers
* @description Maintains list of online users in a mysql table
* @date 2010-01-15
* @author croemmich
*/
public class OnlineUsers extends JavaPlugin {
	protected static final Logger log = Logger.getLogger("Minecraft");
	
    public OnlineUsersPlayerListener l = new OnlineUsersPlayerListener(this);
    
    public static final String name                         = "OnlineUsers";
	public static final String version                      = "1.0";
    
    public static final String directory       = "OnlineUsers/";
    public static final String configFile      = "online_users.settings";
    public static iProperty settings;
    
    public static OnlineUsersDataSource ds;
    public static String dataSource            = "mysql";
    public static String driver                = "com.mysql.jdbc.Driver";
    public static String user                  = "root";
    public static String pass                  = "root";
    public static String db                    = "jdbc:mysql://localhost:3306/minecraft";
    public static String table                 = "users_online";
    public static boolean removeOfflineUsers   = true;
    //public static boolean removeBannedUsers    = true;
    //public static boolean removeKickedUsers    = true;
    public static String connectorJar          = "mysql-connector-java-bin.jar";
    public static String destination           = "flatfile";
    public static String flatfile              = "online_users.txt";
    public static String flatfileTemplate      = "online_users.template";
    public static String flatfileData          = "online_users.data";
    

    public OnlineUsers(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);

        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, l, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, l, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND, l, Priority.Normal, this);
    }

    @Override
    public void onDisable() {
    	ds.setAllOffline();
		if (removeOfflineUsers) {
			ds.removeAllUsers();
		}

		log.info(name + " " + version + " disabled");
    }

    @Override
    public void onEnable() {
    	
    	if (!initProps()) {
			log.severe(name + ": Could not initialise " + configFile);
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		if (destination.equalsIgnoreCase("mysql")) {
			ds = new OnlineUsersMySQL();
		} else {
			ds = new OnlineUsersFlatfile();
		}
		
		if (!ds.init()) {
			log.severe(name + ": Could not init the datasource");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		ds.setAllOffline();
		initOnlineUsers();

        System.out.println( name + " version " + version + " is enabled!" );
    }
    
	public boolean initProps() {
		(new File(directory)).mkdir();
    	settings = new iProperty(directory + configFile);
    	
		destination = settings.getString("destination", "flatfile");
		flatfile = settings.getString("flatfile", "online_users.txt");
		flatfileTemplate = settings.getString("flatfile-template", "online_users.template");
		flatfileData = settings.getString("flatfile-data", "online_users.data");
		driver = settings.getString("driver", "com.mysql.jdbc.Driver");
        user = settings.getString("user", "root");
        pass = settings.getString("pass", "root");
        db = settings.getString("db", "jdbc:mysql://localhost:3306/minecraft");
        table = settings.getString("table", "users_online");
        connectorJar = settings.getString("mysql-connector-jar", connectorJar);
        removeOfflineUsers = settings.getBoolean("remove-offline-users", true);
        //removeBannedUsers = settings.getBoolean("remove-banned-users", true);
        //removeKickedUsers = settings.getBoolean("remove-kicked-users", true);
        
        File file = new File(directory + configFile);
        return file.exists();
	}
	
	public void initOnlineUsers() {
		Player[] players = this.getServer().getOnlinePlayers();
		for (Player player : players) {
			ds.addUser(player.getName());
		}
	}
	
	public OnlineUsersDataSource getDs() {
		return ds;
	}
}

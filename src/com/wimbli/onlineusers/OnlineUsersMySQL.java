package com.wimbli.onlineusers;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class OnlineUsersMySQL extends OnlineUsersDataSource {
	
	private String name = "OnlineUsers";

	protected static final Logger log 		   = Logger.getLogger("Minecraft");
    private static String sqlTruncateTable	   = "TRUNCATE `"+OnlineUsers.table+"`";
    private static String sqlMakeTable 		   = "CREATE TABLE IF NOT EXISTS `"+OnlineUsers.table+"` ("+
    												"`name` varchar(32) NOT NULL, " +
    												"`time` datetime DEFAULT NULL, " +
    												"PRIMARY KEY (`name`))";
    private static String sqlCheckTableExist   = "SHOW TABLES LIKE '"+OnlineUsers.table+"'";
    private static String sqlAlterTableOnline  = "ALTER TABLE `"+OnlineUsers.table+"` ADD `online` bit(1) NOT NULL DEFAULT 0";
    private static String sqlAlterTableOnline2 = "ALTER TABLE `"+OnlineUsers.table+"` CHANGE  `online`  `online` TINYINT( 1 ) UNSIGNED NOT NULL DEFAULT  '0'";
    private static String sqlOnlineUser 	   = "REPLACE INTO `"+OnlineUsers.table+"` (`name`, `time`, `online`) VALUES (?, NOW(), 1)";
    private static String sqlOfflineUser 	   = "UPDATE `"+OnlineUsers.table+"` SET `online`=0 WHERE `name`=?";
    private static String sqlDeleteOfflineUser = "DELETE FROM `"+OnlineUsers.table+"` WHERE `name`=?";
    private static String sqlSetAllOffline     = "UPDATE `"+OnlineUsers.table+"` SET `online`=0";
    
	
	
	@Override
	public boolean init() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {};
		
		return createTable();
	}

	@Override
	public boolean addUser(String username) {
		return execute(sqlOnlineUser,username);
	}

	@Override
	public boolean removeUser(String username) {
		return execute (sqlDeleteOfflineUser, username);
	}

	@Override
	public boolean setUserOffline(String username) {
		return execute (sqlOfflineUser, username);
	}

	@Override
	public boolean setAllOffline() {
		return execute (sqlSetAllOffline);
	}
	
	@Override
	public boolean removeAllUsers() {
		return execute (sqlTruncateTable);
	}
	
	private Connection getConnection() throws SQLException {
		Connection conn = null;
		try{
			conn = DriverManager.getConnection(OnlineUsers.db,OnlineUsers.user,OnlineUsers.pass);
		} catch (Exception e) {
			log.severe(name + ": " + e.getMessage());
		}
		checkConnection(conn);
		return conn;
	}
	
	private boolean checkConnection (Connection conn) throws SQLException {
		if (conn == null) {
				log.severe("Could not connect to the database. Check your credentials in online-users.settings");
			throw new SQLException();
		}
		if (!conn.isValid(5)) {
        	log.severe("Could not connect to the database.");
        	throw new SQLException();
        }
		return true;
	}
	
	private boolean execute(String sql) {
		return execute(sql, null);
	}
	
	private boolean execute(String sql, String player) {
		Connection conn = null;
        PreparedStatement ps = null;
        try {
        	conn = getConnection();
        	ps = conn.prepareStatement(sql);
        	if (player != null && !player.equalsIgnoreCase("")) {
        		ps.setString(1, player);
        	}
        	
        	if (ps.execute()) {
        		return true;
        	}
        } catch (SQLException ex) {
        	log.severe(name + ": " + ex.getMessage());
        	String msg = name + ": could not execute the sql \"" + sql + "\"";
        	if (player != null ) {
        		msg += "    ?=" +player;
        	}
        	log.severe(msg);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
            	log.severe(name + ": " + ex.getMessage());
            }
        }
        return false;
	}
	
	private boolean createTable() {
		Connection conn = null;
        Statement s = null;
        try {
        	conn = getConnection();
        	s = conn.createStatement();
        	s.executeUpdate(sqlMakeTable);
        	try {
        		s.executeUpdate(sqlAlterTableOnline);
        		s.executeUpdate(sqlAlterTableOnline2);
        		log.info(name + ": Updating Table");
        	} catch (SQLException ex2){}
        	ResultSet rs = s.executeQuery(sqlCheckTableExist);
        	if (rs.first()) {
        		return true;
        	}
        } catch (SQLException ex) {
        	log.severe(name + ": " + ex.getMessage());
        } finally {
            try {
                if (s != null) {
                    s.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
            	log.severe(name + ": " + ex.getMessage());
            }
        }
        return false;
	}
}
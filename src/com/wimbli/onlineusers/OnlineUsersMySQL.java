package com.wimbli.onlineusers;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
import java.util.UUID;

public class OnlineUsersMySQL extends OnlineUsersDataSource {
	
	private String name = "OnlineUsers";

	protected static final Logger log 		   = Logger.getLogger("Minecraft");
    private static String sqlTruncateTable	   = "TRUNCATE `"+OnlineUsers.table+"`";
	private static String sqlDropTable         = "DROP TABLE `"+OnlineUsers.table+"`";
    private static String sqlMakeTable 		   = "CREATE TABLE IF NOT EXISTS `"+OnlineUsers.table+"` ("+
    												"`uuid` varchar(36) NOT NULL, " +
    												"`name` varchar(32) NOT NULL, " +
    												"`time` datetime DEFAULT NULL, " +
    												"`online` tinyint(1) UNSIGNED NOT NULL DEFAULT 0, " +
    												"`time_total` int DEFAULT 0, " +
    												"PRIMARY KEY (`uuid`))";
    private static String sqlOnlineUser 	   = "INSERT INTO `"+OnlineUsers.table+"` (`name`, `uuid`, `time`, `online`) VALUES (?, ?, NOW(), 1) ON DUPLICATE KEY UPDATE `name`=?, `time`=NOW(), `online`=1";
    private static String sqlOfflineUser 	   = "UPDATE `"+OnlineUsers.table+"` SET `time_total` = IF(`online`=1, `time_total` + TIMESTAMPDIFF(SECOND, `time`, NOW()), `time_total`), `online`=0 WHERE `uuid`=?";
    private static String sqlDeleteOfflineUser = "DELETE FROM `"+OnlineUsers.table+"` WHERE `uuid`=?";
    private static String sqlSetAllOffline     = "UPDATE `"+OnlineUsers.table+"` SET `time_total` = IF(`online`=1, `time_total` + TIMESTAMPDIFF(SECOND, `time`, NOW()), `time_total`), `online`=0";

	// these are run see if update for older databases is needed, and then update them if so
    private static String sqlCheckTableExist   = "SHOW TABLES LIKE '"+OnlineUsers.table+"'";
    private static String sqlCheckTableUUID    = "SHOW COLUMNS FROM `"+OnlineUsers.table+"` WHERE `Field` = 'uuid'";

	
	@Override
	public boolean init() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {};
		
		return createTable();
	}

	@Override
	public boolean addUser(String username, UUID uuid) {
		return execute(sqlOnlineUser, username, uuid.toString());
	}

	@Override
	public boolean removeUser(String username, UUID uuid) {
		return execute (sqlDeleteOfflineUser, uuid.toString());
	}

	@Override
	public boolean setUserOffline(String username, UUID uuid) {
		return execute (sqlOfflineUser, uuid.toString());
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
		return execute(sql, null, null);
	}
	private boolean execute(String sql, String player) {
		return execute(sql, player, null);
	}

	private boolean execute(String sql, String player, String uuid) {
		Connection conn = null;
        PreparedStatement ps = null;
        try {
        	conn = getConnection();
        	ps = conn.prepareStatement(sql);
        	if (player != null && !player.equalsIgnoreCase("")) {
        		ps.setString(1, player);
				if (uuid != null && !uuid.equalsIgnoreCase("")) {
					ps.setString(2, uuid);
					ps.setString(3, player);
				}
        	}

        	if (ps.execute()) {
        		return true;
        	}
        } catch (SQLException ex) {
        	log.severe(name + ": " + ex.getMessage());
        	String msg = name + ": could not execute the sql \"" + sql + "\"";
        	if (player != null ) {
        		msg += "    ?player=" +player;
        	}
        	if (uuid != null ) {
        		msg += "    ?uuid=" +uuid;
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
		ResultSet rs = null;
        try {
        	conn = getConnection();
        	s = conn.createStatement();
        	s.executeUpdate(sqlMakeTable);

			try {
				// make sure `uuid` column exists, otherwise table is outdated and needs to be dropped and made again
	        	rs = s.executeQuery(sqlCheckTableUUID);
	        	if (!rs.first())
				{
					log.info(name + ": Table outdated and missing UUID column, dropping and recreating");
		        	s.executeUpdate(sqlDropTable);
		        	s.executeUpdate(sqlMakeTable);
				}
				rs.close();
        	} catch (SQLException ex2){}
        	rs = s.executeQuery(sqlCheckTableExist);
        	if (rs.first()) {
				rs.close();
				s.close();
				conn.close();
        		return true;
        	}
        } catch (SQLException ex) {
        	log.severe(name + ": " + ex.getMessage());
        } finally {
            try {
				if (rs != null)
					rs.close();
                if (s != null)
                    s.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
            	log.severe(name + ": " + ex.getMessage());
            }
        }
        return false;
	}
}
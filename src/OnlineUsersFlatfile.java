import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OnlineUsersFlatfile extends OnlineUsersDataSource {

	protected static final Logger log = Logger.getLogger("Minecraft");
	private static final int REMOVE = 0;
	private static final int UPDATE = 1;
	private static final int CREATE = 2;

	@Override
	public synchronized boolean init() {
		return (initUserFile() && initTemplateFile());
	}

	private boolean initUserFile() {
		try {
			File f = new File(OnlineUsers.flatfile);
			if (f.exists()) {
				if (!f.setLastModified(System.currentTimeMillis())) {
					throw new IOException("Could not touch file");
				}
			} else {
				f.createNewFile();
			}
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	private boolean initTemplateFile() {
		String location = OnlineUsers.flatfileTemplate;
		if (!new File(location).exists()) {
			FileWriter writer = null;
			try {
				writer = new FileWriter(location);
				writer.write("#This is the template file for OnlineUsers\r\n");
				writer.write("#Replacement Values Are:\r\n");
				writer.write("#{username}, {timestamp}, {shorttime}, {longtime}, {online-true-false}, {online-offline}, {online-0-1}\r\n");
				writer.write("#\r\n");
				writer.write("#Anything you place in this file will be generated in your online-users file\r\n");
				writer.write("#Be sure to leave the #beginusers and #endusers lines in!\r\n");
				writer.write("Online Users Are:\r\n");
				writer.write("#beginusers\r\n");
				writer.write("{username}:{shorttime}:{online-offline}\r\n");
				writer.write("#endusers\r\n");
			} catch (Exception e) {
				log.log(Level.SEVERE, "Exception while creating " + location, e);
			} finally {
				try {
					if (writer != null) {
						writer.close();
					}
				} catch (IOException e) {
					log.log(Level.SEVERE, "Exception while closing writer for "
							+ location, e);
				}
			}
		}
		return false;
	}

	@Override
	public synchronized boolean addUser(String username) {
		return addUser(username, 1);
	}

	private synchronized boolean addUser(String username, int status) {
		removeUser(username);
		BufferedWriter bw = null;
		String location = OnlineUsers.flatfile;
		try {
			bw = new BufferedWriter(new FileWriter(location, true));
			bw.newLine();
			bw.append(username);
		} catch (Exception e2) {
			log.log(Level.SEVERE, "Exception while writing new user to "
					+ location, e2);
		} finally {
			try {
				if (bw != null) {
					bw.close();
					return true;
				}
			} catch (IOException ex) {
			}
		}
		return false;
	}

	@Override
	public synchronized boolean removeUser(String username) {
		return removeUser(username, REMOVE);
	}

	private synchronized boolean removeUser(String username, int status) {
		FileWriter writer = null;
		String location = OnlineUsers.flatfile;

		try {
			// Now to save...
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					location)));
			String line = "";
			StringBuilder toSave = new StringBuilder();

			while ((line = reader.readLine()) != null) {
				if (!line.equalsIgnoreCase(username.toLowerCase())) {
					toSave.append(line).append("\r\n");
				}
			}
			reader.close();

			writer = new FileWriter(location);
			writer.write(toSave.toString());
		} catch (Exception e1) {
			log.log(Level.SEVERE, "Exception while removing player '"
					+ username + "' from " + location, e1);
		} finally {
			try {
				if (writer != null) {
					writer.close();
					return true;
				}
			} catch (IOException ex) {
			}
		}
		return false;
	}

	@Override
	public boolean setUserOffline(String username) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setAllOffline() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAllUsers() {
		// TODO Auto-generated method stub
		return false;
	}
}
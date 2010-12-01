import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Flatfile extends DataSource {

	protected static final Logger log = Logger.getLogger("Minecraft");
	private static final int REMOVE = 0;
	private static final int UPDATE = 1;
	private static final int CREATE = 2;
	
	@Override
	public synchronized boolean init() {
		try {
		File f = new File(OnlineUsers.flatfile);
        if (f.exists())
        {
            if (!f.setLastModified(System.currentTimeMillis()))
            {
                throw new IOException("Could not touch file");
            }
        }
        else
        {
            f.createNewFile();
        }
		return true;
		} catch (Exception e) {}
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
            log.log(Level.SEVERE, "Exception while writing new user to " + location, e2);
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
            BufferedReader reader = new BufferedReader(new FileReader(new File(location)));
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
            log.log(Level.SEVERE, "Exception while removing player '" + username + "' from " + location, e1);
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
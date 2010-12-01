public abstract class DataSource {
	
	public DataSource() {
		
	}
	
	public abstract boolean init();
	
	public abstract boolean addUser(String username);
	
	public abstract boolean removeUser(String username);
	
	public abstract boolean setUserOffline(String username);
	
	public abstract boolean setAllOffline();
	
	public abstract boolean removeAllUsers();
	
}
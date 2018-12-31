package xyz.thebad.badssh;

public class Main {

	/**
	 * Library test class, use it as examples if you need to
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		SSH ssh = new SSH();
		ssh.connect("localhost");
		ssh.authenticate("root", "mySuperSecurePassword");
		
		if(!ssh.isValid()) {
			throw new Exception("Invalid connection");
		}
		
		System.out.println(ssh.ls("/root"));
		
		System.out.println(ssh.exec("ls"));
		
	}
	
}

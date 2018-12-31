package xyz.thebad.badssh;

public class Main {

	/**
	 * Library test class, use it as examples if you need to
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		SSH ssh = new SSH();
		ssh.connect("");
		ssh.authenticate("", "");
		
		if(!ssh.isValid()) {
			throw new Exception("Invalid connection");
		}
		
		ssh.exec("touch mod.test");
		
		ssh.chmod("/root/mod.test", 7777);
		
	}
	
}

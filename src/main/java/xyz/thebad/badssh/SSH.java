package xyz.thebad.badssh;

import java.io.IOException;
import java.security.Security;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.FileSystemFile;

public class SSH {

	private SSHClient ssh;
	private SFTPClient sftp;
	private Session session;
	
	/**
	 * Create and initialiaze a new SSH object, ready to open a new connection
	 */
	public SSH() {
		Security.addProvider(new BouncyCastleProvider());
		
		ssh = new SSHClient();
		ssh.addHostKeyVerifier(new PromiscuousVerifier());
	}
	
	/**
	 * Connect to an host.
	 * @param host Hostname to connect under the format: <ip>:<port>
	 * @throws IOException
	 */
	public void connect(String host) throws IOException {
		ssh.connect(host);
		ssh.loadKnownHosts();
	}
	
	/**
	 * Authenticate using username and password.
	 * @param username
	 * @param password
	 * @throws IOException
	 */
	public void authenticate(String username, String password) throws IOException {
		ssh.authPassword(username, password);
		session = ssh.startSession();
		sftp = ssh.newSFTPClient();
	}
	
	/**
	 * Authenticate using public key.
	 * @param username Username
	 * @param certificatePaths Paths to every certificate you want to use
	 * @throws IOException
	 */
	public void authenticate(String username, String... certificatePaths) throws IOException {
		ssh.authPublickey(username, certificatePaths);
		session = ssh.startSession();
		sftp = ssh.newSFTPClient();
	}
	
	/**
	 * Get internal SSH client.
	 * @return internal SSH client
	 */
	public SSHClient getSSH() {
		return ssh;
	}
	
	/**
	 * Get internal SFTP client.
	 * @return internal SFTP client
	 */
	public SFTPClient getSFTP() {
		return sftp;
	}
	
	/**
	 * Get internal SSH session.
	 * @return internal SSH session
	 */
	public Session getSession() {
		return session;
	}
	
	/**
	 * Check if client is connected or not.
	 * @return true if ssh connection is open, false otherwise
	 */
	public boolean isConnected() {
		return ssh.isConnected();
	}
	
	/**
	 * Check if user is authenticated.
	 * @return true if user is authenticated, false otherwise
	 */
	public boolean isAuthenticated() {
		return ssh.isAuthenticated();
	}
	
	/**
	 * Check if SSH session is open.
	 * @return true if session is open, false otherwise
	 */
	public boolean isSessionOpen() {
		return session.isOpen();
	}
	
	/**
	 * Check if client is connected AND authenticated.
	 * @return if client is connected and authenticated
	 */
	public boolean isValid() {
		return ssh.isConnected() && ssh.isAuthenticated();
	}
	
	/**
	 * Execute a command and wait for it to complete, don't use for command that recquire user interactions, use instead: TODO.
	 * @param command Command to execute
	 * @return Command output
	 * @throws IOException
	 */
	public String exec(String command) throws IOException {
		Command cmd = session.exec(command);
		return IOUtils.readFully(cmd.getInputStream()).toString();
	}
	
	/**
	 * List all files and directories at the specified location.
	 * @param path Path where to list files and directories
	 * @return A list of resource informations about content of the location
	 * @throws IOException
	 */
	public List<RemoteResourceInfo> ls(String path) throws IOException {
		return sftp.ls(path);
	}
	
	/**
	 * Perform a resource download via SFTP.
	 * @param source Path to which file to download
	 * @param dest Path to where the file will be downloaded
	 * @throws IOException
	 */
	public void downloadFile(String source, String dest) throws IOException {
		sftp.get(source, new FileSystemFile(dest));
	}
	
	/**
	 * Perform a resource upload via SFTP.
	 * @param localPath Path to which file to upload
	 * @param remotePath Path to where the file will be uploaded
	 * @throws IOException
	 */
	public void uploadFile(String localPath, String remotePath) throws IOException {
		sftp.put(localPath, remotePath);
	}
	
	public void chmod(String path, int perms) throws IOException {
		sftp.chmod(path, perms);
	}
	
	public void chown(String path, int userId) throws IOException {
		sftp.chown(path, userId);
	}
	
	public void chgrp(String path, int groupId) throws IOException {
		sftp.chgrp(path, groupId);
	}
	
	/**
	 * Close all connection.
	 * @throws IOException
	 */
	public void close() throws IOException {
		session.close();
		ssh.close();
		sftp.close();
	}
	
}

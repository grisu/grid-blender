package grisu.frontend.blender;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginException;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.control.login.LoginParams;
import grith.jgrith.CredentialHelpers;
import grith.jgrith.myProxy.LocalMyProxy;
import grith.jgrith.plainProxy.LocalProxy;
import grith.jgrith.plainProxy.PlainProxy;

import java.io.File;
import java.io.IOException;

import jline.ConsoleReader;

import org.apache.commons.lang.StringUtils;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;

public class GridBlenderUtils {

	public static char[] askForPassword(String question) {

		char[] password = null;
		try {
			final ConsoleReader consoleReader = new ConsoleReader();
			password = consoleReader.readLine(question, new Character('*'))
					.toCharArray();
		} catch (final Exception e) {
			System.err.println("Couldn't read password input: "
					+ e.getLocalizedMessage());
			System.exit(1);
		}

		return password;

	}

	public static ServiceInterface login(BlenderCommandLineArgs args) {

		LoginParams loginParams = null;
		ServiceInterface si = null;

		String username = null;

		if (args.isUsername()) {
			username = args.getUsername();
		}

		if (StringUtils.isNotBlank(username)) {
			// means myproxy or shib login
			if (!args.isIdp()) {
				// means myproxy login
				final char[] password = askForPassword("Please enter your myproxy password: ");

				loginParams = new LoginParams(
				// "http://localhost:8080/xfire-backend/services/grisu",
				// "https://ngportal.vpac.org/grisu-ws/soap/EnunciateServiceInterfaceService",
				// "https://ngportal.vpac.org/grisu-ws/services/grisu",
				// "https://ngportal.vpac.org/grisu-ws/soap/GrisuService",
				// "http://localhost:8080/enunciate-backend/soap/GrisuService",
						"Local",
						// "Dummy",
						username, password);

				try {
					si = LoginManager
							.login(null, null, null, null, loginParams);
					return si;
				} catch (final RuntimeException e) {
					System.err.println(e.getLocalizedMessage());
					System.exit(1);
				} catch (final LoginException e) {
					System.err.println(e.getLocalizedMessage());
					System.exit(1);
				}

				// possibly store local proxy
				if (args.isSaveLocalProxy()) {
					try {
						LocalMyProxy.getDelegationAndWriteToDisk(username,
								password, 3600 * 24);
					} catch (final Exception e) {
						System.err
								.println("Couldn't write myproxy credential to disk.");
					}
				}

			} else {
				// means shib login
				loginParams = new LoginParams(
				// "http://localhost:8080/xfire-backend/services/grisu",
				// "https://ngportal.vpac.org/grisu-ws/soap/EnunciateServiceInterfaceService",
				// "https://ngportal.vpac.org/grisu-ws/services/grisu",
				// "https://ngportal.vpac.org/grisu-ws/soap/GrisuService",
				// "http://localhost:8080/enunciate-backend/soap/GrisuService",
						"Local",
						// "Dummy",
						null, null);

				final char[] password = askForPassword("Please enter the password for IDP '"
						+ args.getIdp() + "': ");

				try {
					si = LoginManager.login(null, password, args.getUsername(),
							args.getIdp(), loginParams);

					if (args.isSaveLocalProxy()) {
						System.err
								.println("Saving shib proxy credentials to disk not supported yet.");
					}

					return si;
				} catch (final RuntimeException e) {
					System.err.println(e.getLocalizedMessage());
					System.exit(1);
				} catch (final LoginException e) {
					System.err.println(e.getLocalizedMessage());
					System.exit(1);
				}

			}
		} else {
			// means certlogin

			loginParams = new LoginParams(
			// "http://localhost:8080/xfire-backend/services/grisu",
			// "https://ngportal.vpac.org/grisu-ws/soap/EnunciateServiceInterfaceService",
			// "https://ngportal.vpac.org/grisu-ws/services/grisu",
			// "https://ngportal.vpac.org/grisu-ws/soap/GrisuService",
			// "http://localhost:8080/enunciate-backend/soap/GrisuService",
					"Local",
					// "Dummy",
					null, null);

			GlobusCredential cred = null;
			if (LocalProxy.validGridProxyExists()) {
				try {
					cred = LocalProxy.loadGlobusCredential();
				} catch (final GlobusCredentialException e) {
					System.err.println(e.getLocalizedMessage());
					System.exit(1);
				}
			} else {
				final char[] password = askForPassword("Please enter you private key passphrase: ");

				try {
					cred = CredentialHelpers.unwrapGlobusCredential(PlainProxy
							.init(password, 24));
				} catch (final Exception e) {
					System.err.println(e.getLocalizedMessage());
					System.exit(1);
				}

				if (args.isSaveLocalProxy()) {
					try {
						CredentialHelpers.writeToDisk(cred, new File(
								LocalProxy.PROXY_FILE));
					} catch (final IOException e) {
						System.err
								.println("Could not write proxy credential to disk: "
										+ e.getLocalizedMessage());
					}
				}

			}
			try {
				// means using existing proxy
				si = LoginManager.login(cred, null, null, null, loginParams);
				return si;
			} catch (final RuntimeException e) {
				System.err.println(e.getLocalizedMessage());
				System.exit(1);
			} catch (final LoginException e) {
				System.err.println(e.getLocalizedMessage());
				System.exit(1);
			}

		}
		throw new RuntimeException(
				"Could not login for some unknown reason. This is most probably the result of markus.binsteiner@arcs.org.au being stupid. Please contact him.");
	}

}

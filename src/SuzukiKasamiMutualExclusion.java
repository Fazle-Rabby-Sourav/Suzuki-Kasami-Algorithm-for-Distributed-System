import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class SuzukiKasamiMutualExclusion {

	public static void main(String[] args) {

		// Read the config of nodes
		BufferedReader bufferedReader = null;
		File file = new File("");
		String absolutePath = file.getAbsolutePath();

//		File nodes = new File(absolutePath + "\\src\\nodes.config");
		File nodes = new File(absolutePath + "\\nodes.config");
		try {
			bufferedReader = new BufferedReader(new FileReader(nodes));
			int numberOfNodes = 0;
			String nodeAddress = bufferedReader.readLine();
			ArrayList<String> nodeList = new ArrayList<String>();

			while (nodeAddress != null) {
				nodeList.add(nodeAddress);
				numberOfNodes++;
				nodeAddress = bufferedReader.readLine();
			}

			int[] siteNumber = new int[numberOfNodes];
			String[] ipAddress = new String[numberOfNodes];
			int[] port = new int[numberOfNodes];

			String[] tmpAddress = null;

			for (int counter = 0; counter < numberOfNodes; counter++) {
				tmpAddress = nodeList.get(counter).split(" ");
				siteNumber[counter] = Integer.parseInt(tmpAddress[0]);
				ipAddress[counter] = tmpAddress[1];
				port[counter] = Integer.parseInt(tmpAddress[2]);
			}
			
			Scanner scanner = new Scanner(System.in);

			int thisSiteNumber = 0;
			int isCorrectSiteNumber = 0;

			do {
				System.out.print("Enter site number (1-" + numberOfNodes + "): ");
				while (!scanner.hasNextInt()) {
					System.out.println("That's not a number!");
					scanner.next();
				}
				thisSiteNumber = Integer.parseInt(scanner.nextLine());
				if (thisSiteNumber >= 1 && thisSiteNumber <= numberOfNodes) {
					isCorrectSiteNumber = 1;
				} else {
					System.out.println("The site number you entered is out of range. Please enter the correct site number between 1 to  " + numberOfNodes);
				}

			} while (isCorrectSiteNumber == 0);
			
			int hasToken = 0;

			if (thisSiteNumber == 1) {
				hasToken = 1;
			}

			Site localSite = new Site(numberOfNodes, thisSiteNumber, hasToken, ipAddress, port);

			// Open a socket
			ListenToBroadcast listenToBroadcast = new ListenToBroadcast(localSite, port[thisSiteNumber - 1]);
			listenToBroadcast.start();
			String inputQuery = "";
			while (!inputQuery.equalsIgnoreCase("quit")) {
				System.out.println("Press ENTER to enter CS: ");
				Scanner scan_query = new Scanner(System.in);
				inputQuery = scan_query.nextLine();
				System.out.println("Site-"+thisSiteNumber + " is trying to enter Critical Section");
				if (localSite.token == 1) {

					localSite.processingCS = 1;
					System.out.println("Site-"+thisSiteNumber+" has token. Executing in the Critical Section.....");

					Thread.sleep(Utils.CS_EXECUTE_TIME);

					localSite.processingCS = 0;

					System.out.println("Site-"+thisSiteNumber+" is exiting Critical Section.");
					System.out.println("");

					exitCS(localSite, thisSiteNumber, numberOfNodes, ipAddress, port, numberOfNodes);

				} else {
					System.out.println("Site-"+thisSiteNumber+" doesn't have token. So Site-"+thisSiteNumber+" is requesting token");
					localSite.requestCriticalSection();
					System.out.println("Site-"+thisSiteNumber+" is waiting for token.");
					
					localSite.processingCS = 1;

					while (localSite.token == 0) {
						Thread.sleep(Utils.WAITTING_FOR_TOKEN_TIME);
					}

					System.out.println("Site-"+thisSiteNumber+" has received token. Executing in Critical Section.....");
					Thread.sleep(Utils.CS_EXECUTE_TIME);
					localSite.processingCS = 0;
					System.out.println("Site-"+thisSiteNumber+ " is exiting Critical Section.");
					System.out.println("");
					exitCS(localSite, thisSiteNumber, numberOfNodes, ipAddress, port, numberOfNodes);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void exitCS(Site localSite, int thisSiteNumber, int numberOfNodes, String[] ipAddress, int[] port, int numOfSites) {

		localSite.LN[thisSiteNumber - 1] = localSite.RN[thisSiteNumber - 1];

		// Send updated LN value to all sites
		String message = "ln," + thisSiteNumber + "," + localSite.LN[thisSiteNumber - 1];

		for (int i = 0; i < numOfSites; i++) {
			if(i==thisSiteNumber-1) {
				continue;
			}

			try {
				Socket socket = new Socket(ipAddress[i], port[i]);
				OutputStream outputStream = socket.getOutputStream();
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
				BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
				bufferedWriter.write(message);
				bufferedWriter.flush();
				socket.close();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		for (int i = 0; i < numberOfNodes; i++) {
			if (localSite.RN[i] == localSite.LN[i] + 1) {
				if (!localSite.tokenQueue.contains(i + 1)) {
					localSite.tokenQueue.add(i + 1);
				}
			}
		}

		if (localSite.tokenQueue.size() > 0) {
			localSite.sendToken(localSite.tokenQueue.poll());
		}
	}
}
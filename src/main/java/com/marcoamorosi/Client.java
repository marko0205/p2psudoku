package com.marcoamorosi;

import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import implementation.Sudoku;
import implementation.SudokuGameImp;
import interfaces.ClientInterface;
import interfaces.MessageListener;



public class Client implements ClientInterface {

	@Option(name = "-m", aliases = "--masterip", usage = "the master peer ip address", required = true)
	private static String master;

	@Option(name = "-id", aliases = "--identifierpeer", usage = "the unique identifier for this peer", required = true)
	private static int id;

	SudokuGameImp peer;
	TextIO textIO = null;
	TextTerminal<?> terminal = null;
	String book_clear = null;
	String book_menu = null;
	boolean menu;
	String user = null;
	Thread t1 = null;
	Thread t2 = null;
	boolean f = false;

    public Client(String[] args) throws CmdLineException {
		CmdLineParser parser = new CmdLineParser(this);
		parser.parseArgument(args);
		System.out.println("\nPeer: " + id + " on Master: " + master + "\n");
        try {
            peer = new SudokuGameImp(id, master, new MessageListenerImpl(id));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class threadScoreBoard extends Thread {
    	ReadWriteLock lock = new ReentrantReadWriteLock();
    	Sudoku gameInstance;

        public threadScoreBoard(Sudoku gameInstance) {
            super();
            this.gameInstance=gameInstance;
        }

		@Override
        public void run() {

            System.out.println("MyThread score - START "+Thread.currentThread().getName());

    		ArrayList<String> scores = gameInstance.getAndUpdateScores();
    		terminal.resetToBookmark(book_clear);

    		int x = scores.size()-1;
			lock.writeLock();
    		terminal.printf(" --------- G A M E  F I N I S H ---------\n\n");

    		terminal.print("Players\t\tScores\n");
    		for(; x>=0; x--)
    			terminal.println(scores.get(x).toString());

			try {
				Thread.sleep(3000);
				showMenu();
			} catch (Exception e) {
				e.printStackTrace();
			}
            System.out.println("MyThread score - END "+Thread.currentThread().getName());

		}


    }

    public class MyThread extends Thread {
    	Sudoku gameInstance;

        public MyThread(Sudoku gameInstance) {
            super();
            this.gameInstance=gameInstance;
        }

        public MyThread() {
		}

		@Override
        public void run() {
            System.out.println("MyThread board- START "+Thread.currentThread().getName());
            try {
            	// ! is an escape char
        		String colums = "ABCDEFGHI!";
        		String patternCol = "[a-iA-I]|!";
        		String patternRow = "[0-8]|!";
        		String patternVal = "[1-9]|!";

        		String gameName = gameInstance.getName();
        		Integer board[][] =  gameInstance.getGame();
        		ArrayList<String> scores = gameInstance.getAndUpdateScores();
    			terminal.resetToBookmark(book_clear);
    			printBoard(board, scores);

            		//while(true) {

        			String jInLetter = textIO.newStringInputReader().read("Insert (Format A0=2): ");
        			String arr[] = jInLetter.split("");

        			int j = colums.indexOf(arr[0].toUpperCase());

        			if(arr[0].matches(patternCol) && arr[1].matches(patternRow) && arr[3].matches(patternVal)) {
        				peer.placeNumber(gameName, Integer.parseInt(arr[1]), j, Integer.parseInt(arr[3]));
        			//	break;
        			//}

        		}
            } catch (NumberFormatException | ClassNotFoundException e) {
              //  e.printStackTrace();
            }
            System.out.println("MyThread board- END "+Thread.currentThread().getName());

        }
    }

    class MessageListenerImpl implements MessageListener {
		int peerid;

		public MessageListenerImpl(int peerid) {
			this.peerid = peerid;
		}

		@Override
		public Object parseMessage(Object obj) {

			Sudoku gameInstance = (Sudoku) obj;

			peer.setGameInstance(gameInstance);

			if(t1.isAlive() && t1 != null)
				t1.interrupt();

			terminal.resetToBookmark(book_clear);

			if(gameInstance.isFinisched()) {

				t2 = new threadScoreBoard(gameInstance);
				t2.start();

			}
			else {
				t1 = new MyThread(gameInstance);
				t1.start();
			}

			return "success";
		}

	}


	public static void main(String[] args) {

		Client client;
		try {
			client = new Client(args);
			client.start();
		} catch (CmdLineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	@Override
	public void start() {

		textIO = TextIoFactory.getTextIO();
		terminal = textIO.getTextTerminal();

		terminal.getProperties().setInputColor("white");
		terminal.getProperties().setPromptColor("white");

		terminal.printf("\nStaring peer id: %d on master node: %s\n", id, master);

		terminal.setBookmark(book_clear);

		try {
			printWelcome();
		} catch (Exception e) {
			e.printStackTrace();
		}

		terminal.resetToBookmark(book_clear);

		try {
			showMenu();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	@Override
	public void showMenu() throws Exception {
		menu = true;
		while (menu) {

			printMenu();

			int option = textIO.newIntInputReader().withMaxVal(3).withMinVal(1).read("Option");
			switch (option) {
			case 1 : { startNewGame(); menu = false; break;}
			case 2 : { joinExistingGame(); menu = false; break;}
			case 3 : {leaveNetwork(); break;}
			default : {break;}
			}
		}
	}

	@Override
	public void startNewGame() throws Exception {
		terminal.resetToBookmark(book_clear);
		
		terminal.printf(" --------- N E W  G A M E ---------\n");
		terminal.printf("\nPlease enter game name\n");
		String gameName = textIO.newStringInputReader().read("Name:");

		// cattura con try...
		Sudoku gameInstance =  peer.generateNewSudoku(gameName);

		//terminal.printf("\033[H\033[2J");

		t1 = new MyThread(gameInstance);
		t1.start();
		//inGame(gameInstance);

	}


	@Override
	public void inGame(Sudoku gameInstace) throws NumberFormatException, ClassNotFoundException {
		String colums = "ABCDEFGHI!";
		String patternCol = "[a-iA-I]|!";
		String patternRow = "[0-8]|!";

		String gameName = gameInstace.getName();
		Integer board[][] =  gameInstace.getGame();
		ArrayList<String> scores = gameInstace.getAndUpdateScores();
		terminal.printf("\033[H\033[2J");
		while(true) {
			terminal.resetToBookmark(book_clear);
			printBoard(board, scores);

			String jInLetter = textIO.newStringInputReader().read("Insert (Format A0=2): ");

			String arr[] = jInLetter.split("");
//			while (!jInLetter.matches(patternCol)) {
//				jInLetter = textIO.newStringInputReader().read("Please enter a valid colum (from A to I): ");
//			}

			int j = colums.indexOf(arr[0].toUpperCase());
			//String i = textIO.newStringInputReader().read("Row: ");
//			while (!i.matches(patternRow)) {
//				i = textIO.newStringInputReader().read("Please enter a valid row (from 0 to 8): ");
//			}


			//String val = textIO.newStringInputReader().read("Value: ");


			peer.placeNumber(gameName, Integer.parseInt(arr[1]), j, Integer.parseInt(arr[3]));
		}

	}


	@Override
	public void printScoreBoard(Sudoku gameInstance) {
		terminal.printf("\033[H\033[2J");
		ArrayList<String> scores = gameInstance.getAndUpdateScores();
		terminal.resetToBookmark(book_clear);

		int x = scores.size()-1;

		terminal.printf(" --------- G A M E  F I N I S H ---------\n\n");

		terminal.print("Players\t\tScores\n");
		for(; x>=0; x--)
			terminal.println(scores.get(x).toString());

		try {
			showMenu();
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	@Override
	public void printBoard(Integer board[][], ArrayList<String> scores) {
//		terminal.resetToBookmark(book_clear);
		terminal.printf("\033[H\033[2J");
		int A = 0;
		int B = 0;
		terminal.printf("    A  B  C   D  E  F   G  H  I        Player\t\tScore   \n");
		int x = scores.size()-1;
		for (int i = 0; i < 9; i++) {
			if (i == A) {
				if( x >= 0) {
					terminal.printf("   ----------------------------- \t" + scores.get(x).toString() + "\n");
					x--;
				}
				else
					terminal.printf("   ----------------------------- \n");
				A = A + 3;
			}
			for (int j = 0; j < 9; j++) {
				if (B == j) {
					if(j == 0) {
						terminal.printf(""+i+" ");
					}
					terminal.printf("|");
					B = B + 3;
				}
				terminal.printf(" " + board[i][j] + " ");
			}
			B = 0;
			if ( x >= 0) {
				terminal.printf("| \t"+scores.get(x).toString()); x--;
			}
			else
				terminal.printf("|");
			terminal.printf("\n");

		}
		terminal.printf("   -----------------------------\n");

	}


	@Override
	public void joinExistingGame() throws Exception {

		terminal.resetToBookmark(book_clear);
		terminal.printf(" --------- G A M E  L I S T ---------\n\n");
		peer.updateGamesList();

		if(peer.gameList.isEmpty()) {
			terminal.printf("No game avaiable, create a new one (type r to refresh, e to exit)\n");
			String choice = textIO.newStringInputReader().read();
			if(choice.equals("e"))
				showMenu();
			if(choice.equals("r"))
				joinExistingGame();
		}
		else {
			for (String a : peer.gameList)
				terminal.printf(a+"\n");
			terminal.printf("\nEnter a game name (r to refresh)\n");

			String gname = textIO.newStringInputReader().read();
			if(gname.equals("r"))
				joinExistingGame();

			peer.join(gname);

			t1 = new MyThread(peer.searchGame(gname));

			t1.start();

			//inGame(gameInstance);

		//	final String sname = textIO.newStringInputReader().read();

		}

	}

	@Override
	public void printMenu() {
		terminal.resetToBookmark(book_clear);
		terminal.printf(" Hello "+ user +", pick a choiche: \n");
		terminal.printf("\n ---------------------\n");
		terminal.printf("| 1 - Create new game |\n");
		terminal.printf("| 2 - Join a game     |\n");
		terminal.printf("| 3 - Exit            |\n");
		terminal.printf(" ---------------------\n");
	}

	@Override
	public void leaveNetwork() {
		terminal.printf("\nARE YOU SURE TO LEAVE THE NETWORK?\n");
		boolean exit = textIO.newBooleanInputReader().withDefaultValue(false).read("exit?");

		if (exit) {
			peer.leaveNetwork();
			System.exit(0);
		}
	}


	@Override
	public void printWelcome() throws Exception {
		terminal.printf("\n------------------------------------------\n");
		terminal.printf("\n W E L C O M E  T O  S U D O K U  G A M E \n");
		terminal.printf("\n------------------------------------------\n");
		terminal.printf("\nTo begin choose a nickname: \n");

		 String nick = textIO.newStringInputReader().read("Nickname:");
		 while (!peer.addToPlayerList(nick)) {
			 nick = textIO.newStringInputReader().read("Nickname già esistente! Nick: ");

		 }
		 peer.addToPlayerList(nick);
		 user = nick;

	}


}


package com.marcoamorosi;

import java.util.ArrayList;

import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import implementation.Sudoku;
import implementation.SudokuGameImp;
import interfaces.MessageListener;

public class ClientStatic {
    @Option(name = "-m", aliases = "--masterip", usage = "the master peer ip address", required = true)
	private static String master;

	@Option(name = "-id", aliases = "--identifierpeer", usage = "the unique identifier for this peer", required = true)
	private static int id;

	SudokuGameImp peer;
	TextIO textIO = null;
	TextTerminal<?> terminal = null;
	String book_clear = null;
	boolean mainMenu;
	String userNick = null;
    String gameName = null;
	String lastScoreDone = null;
	

    public ClientStatic(String[] args) throws CmdLineException {
		CmdLineParser parser = new CmdLineParser(this);
		parser.parseArgument(args);
		System.out.println("\nPeer: " + id + " on Master: " + master + "\n");
        try {
            peer = new SudokuGameImp(id, master, new MessageListenerImpl(id));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class MessageListenerImpl implements MessageListener {
		int peerid;

		public MessageListenerImpl(int peerid) {
			this.peerid = peerid;
		}
	
		public Object parseMessage(Object obj) {

			return "success";
		}
	}

	public static void main(String[] args) {

		ClientStatic client;
		try {
			client = new ClientStatic(args);
			client.start();
		} catch (CmdLineException e) {
			e.printStackTrace();
		}

	}


	
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


	public void showMenu() throws Exception {
		mainMenu = true;
		
		while (true) {

			if(mainMenu) {
				printMenu();

				int option = textIO.newIntInputReader().withMaxVal(3).withMinVal(1).read("Option");
				switch (option) {
				case 1 : { startNewGame(); break; }
				case 2 : { joinExistingGame(); break; }
				case 3 : { leaveNetwork(); break;}
				default : { }
				}
			}
			else {
				printMenuInGame();

				int option = textIO.newIntInputReader().withMaxVal(3).withMinVal(1).read("Option");
				switch (option) {
				case 1 : { getSudokuMenu(peer.searchGame(gameName)); break;}
				case 2 : { inGame(); break; }
				case 3 : { leaveSudoku(); break;}
				default : { }
				}
			}

		}
	}

    public void startNewGame() throws Exception {
		terminal.resetToBookmark(book_clear);
		terminal.printf("\033[H\033[2J");
		
		terminal.printf(" --------- N E W  G A M E ---------\n");
		terminal.printf("\nPlease enter game name\n");
		gameName = textIO.newStringInputReader().read("Name:");

		if(peer.generateNewSudoku(gameName) != null) {
			mainMenu= false;
		} 
		else
			terminal.printf("This match is already avaible join it from the menu\n");

	}

	
	public void getSudokuMenu(Sudoku gameInstance) throws Exception {
		//terminal.printf("\033[H\033[2J");

		if(!gameInstance.isFinisched()) {
			Integer board[][] =  gameInstance.getGame();
			ArrayList<String> scores = gameInstance.getAndUpdateScores();
			printBoard(board, scores);
		}
		else {
			printScoreBoard(gameInstance);
			leaveSudoku();
			//mainMenu=true;
		}
	}

	public void inGame() throws NumberFormatException, ClassNotFoundException {
		String colums = "ABCDEFGHI!";
		String patternCol = "[a-iA-I]|!";
		String patternRow = "[0-8]|!";
		String patternVal = "[1-9]|!";

		while(true) {

			String jInLetter = textIO.newStringInputReader().read("Insert (Format A0=2): ");
			String arr[] = jInLetter.split("");
			int j = colums.indexOf(arr[0].toUpperCase());

			if(arr[0].matches(patternCol) && arr[1].matches(patternRow) && arr[3].matches(patternVal)) {

				int score = peer.placeNumber(gameName, Integer.parseInt(arr[1]), j, Integer.parseInt(arr[3]));
				if(score == 0) {
					lastScoreDone= "Value aready present! +0 point\n";
					//terminal.printf("Value aready present! +0 point\n");
				}
				if(score > 0) {
					lastScoreDone="Congratulation successful insert! +1 point\n";
					//terminal.printf("Congratulation successful insert! +1 point\n");
				}
				if(score < 0) {
					lastScoreDone="Insert not valid! -1 point\n";
					//terminal.printf("Insert not valid! -1 point\n");
				}
				if(score == 99 ){
					lastScoreDone="Congratulations you insert the last value! \n";
					//terminal.printf("Congratulations you insert the last value! \n");
				}
				break;
			}
			else 
				terminal.printf("Please respect the format A0=2\n");
		}
		try {
			getSudokuMenu(peer.searchGame(gameName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void printScoreBoard(Sudoku gameInstance) {
		terminal.printf("\033[H\033[2J");
		ArrayList<String> scores = gameInstance.getAndUpdateScores();
		terminal.resetToBookmark(book_clear);

		terminal.printf(" --------- G A M E  F I N I S H ---------\n\n");
		terminal.print("Players\t\tScores\n");

		int x = scores.size()-1;
		for(; x>=0; x--)
			terminal.println(scores.get(x).toString());
	}

	public void printBoard(Integer board[][], ArrayList<String> scores) {
		terminal.resetToBookmark(book_clear);
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


	public void joinExistingGame() throws Exception {
		terminal.printf("\033[H\033[2J");
		terminal.resetToBookmark(book_clear);
		terminal.printf(" --------- G A M E  L I S T ---------\n\n");
		peer.downloadGameList();

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

			gameName = textIO.newStringInputReader().read();
			if(gameName.equals("r"))
				joinExistingGame();

			peer.join(gameName);
			mainMenu= false; 
		
		}

	}

	
	public void printMenu() {
		terminal.resetToBookmark(book_clear);
		terminal.printf(" Hello "+ userNick +", pick a choiche: \n");
		terminal.printf("\n ---------------------\n");
		terminal.printf("| 1 - Create new game |\n");
		terminal.printf("| 2 - Join a game     |\n");
		terminal.printf("| 3 - Exit            |\n");
		terminal.printf(" ---------------------\n");
	}

    public void printMenuInGame() {
		terminal.resetToBookmark(book_clear);
		if (lastScoreDone==null)
			terminal.printf(" Game: "+ gameName +", pick a choiche: \n");
		else 
		terminal.printf(lastScoreDone);
		terminal.printf("\n ----------------------\n");
		terminal.printf("| 1 - Get/Refresh game |\n");
		terminal.printf("| 2 - Place a number   |\n");
		terminal.printf("| 3 - Left game        |\n");
		terminal.printf(" ----------------------\n");
	}

	public void leaveNetwork() {
		terminal.printf("\nARE YOU SURE TO LEAVE THE NETWORK?\n");
		boolean exit = textIO.newBooleanInputReader().withDefaultValue(false).read("exit?");

		if (exit) {
			peer.leaveNetwork();
			System.exit(0);
		}
	}


	public void leaveSudoku() throws Exception {
		peer.leaveSudoku(gameName);
		lastScoreDone=null;
		mainMenu=true;
	}
	
	public void printWelcome() throws Exception {
		terminal.printf("\n------------------------------------------\n");
		terminal.printf("\n W E L C O M E  T O  S U D O K U  G A M E \n");
		terminal.printf("\n------------------------------------------\n");
		terminal.printf("\nTo begin choose a nickname: \n");

		String userNick = textIO.newStringInputReader().read("Nickname:");
		 while (!peer.addToPlayerList(userNick)) {
			userNick = textIO.newStringInputReader().read("Nickname già esistente! Nick: ");
		 }

	}
}

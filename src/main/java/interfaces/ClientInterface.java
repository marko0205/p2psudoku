package interfaces;

import java.util.ArrayList;

import implementation.Sudoku;

public interface ClientInterface {

	void start() throws Exception;

	void showMenu() throws Exception;

	void startNewGame() throws Exception;

	void inGame(Sudoku gameInstace) throws NumberFormatException, ClassNotFoundException;

	void printScoreBoard(Sudoku gameInstance);

	void printBoard(Integer board[][], ArrayList<String> scores);

	void joinExistingGame() throws Exception;

	void printMenu();

	void leaveNetwork();

	void printWelcome() throws Exception;

}
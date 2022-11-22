package implementation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Sudoku implements Serializable {

	private final String game_name;
	private ArrayList<Player> players = new ArrayList<>();
	private ArrayList<String> scores = new ArrayList<>();
	private boolean isFinisched = false;

	private ArrayList<Integer[][]> sudokuList = new ArrayList<>();

	private Integer[][] matrixToSolve = new Integer[9][9];
	private Integer[][] matrixCorrect = new Integer[9][9];

	private Integer[][] matrix1ToSolve = { { 3, 0, 6, 5, 0, 8, 4, 0, 0 }, { 5, 2, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 8, 7, 0, 0, 0, 0, 3, 1 }, { 0, 0, 3, 0, 1, 0, 0, 8, 0 }, { 9, 0, 0, 8, 6, 3, 0, 0, 5 },
			{ 0, 5, 0, 0, 9, 0, 6, 0, 0 }, { 1, 3, 0, 0, 0, 0, 2, 5, 0 }, { 0, 0, 0, 0, 0, 0, 0, 7, 4 },
			{ 0, 0, 5, 2, 0, 6, 3, 0, 0 } };

	private Integer[][] matrix1Correct = { { 3, 1, 6, 5, 7, 8, 4, 9, 2 }, { 5, 2, 9, 1, 3, 4, 7, 6, 8 },
			{ 4, 8, 7, 6, 2, 9, 5, 3, 1 }, { 2, 6, 3, 4, 1, 5, 9, 8, 7 }, { 9, 7, 4, 8, 6, 3, 1, 2, 5 },
			{ 8, 5, 1, 7, 9, 2, 6, 4, 3 }, { 1, 3, 8, 9, 4, 7, 2, 5, 6 }, { 6, 9, 2, 3, 5, 1, 8, 7, 4 },
			{ 7, 4, 5, 2, 8, 6, 3, 1, 9 } };

	private Integer[][] matrix2Correct = {
			{ 7, 6, 5, 8, 4, 3, 2, 1, 9 },
            { 4, 1, 2, 6, 9, 7, 8, 5, 3 },
            { 9, 3, 8, 2, 5, 1, 7, 6, 4 },
            { 3, 2, 4, 1, 8, 6, 5, 9, 7 },
            { 1, 8, 9, 5, 7, 4, 6, 3, 2 },
            { 6, 5, 7, 9, 3, 2, 4, 8, 1 },
            { 8, 7, 1, 4, 6, 9, 3, 2, 5 },
            { 5, 9, 3, 7, 2, 8, 1, 4, 6 },
            { 2, 4, 6, 3, 1, 5, 9, 7, 8 }
         };

	private Integer[][] matrix3Correct = {
			{5, 7, 2, 6, 1, 8, 3, 9, 4},
			{1, 9, 4, 5, 2, 3, 7, 8, 6},
			{8, 3, 6, 7, 9, 4, 1, 5, 2},
			{2, 8, 1, 3, 4, 5, 6, 7, 9},
			{9, 6, 7, 2, 8, 1, 4, 3, 5},
			{4, 5, 3, 9, 6, 7, 8, 2, 1},
			{6, 1, 9, 8, 7, 2, 5, 4, 3},
			{7, 4, 5, 1, 3, 9, 2, 6, 8},
			{3, 2, 8, 4, 5, 6, 9, 1, 7}
		};

	Sudoku(String game_name, Player player) {
		this.game_name = game_name;
		sudokuList.add(matrix1Correct);
		sudokuList.add(matrix2Correct);
		sudokuList.add(matrix3Correct);
		this.matrixCorrect = sudokuList.get(new Random().nextInt(sudokuList.size()));
		createSudoku(matrixCorrect);
		addPlayer(player);
	}

	public Sudoku(String game_name) {
		this.game_name = game_name;
	}

	public Sudoku() {
		this.game_name = "";
	}
	
	public Integer[][] getGame() {
		return matrixToSolve;
	}

	public String getName() {
		return this.game_name;
	}

	public boolean isFinisched() {
		return isFinisched;
	}

	public Player getPlayerByName(String _player_name) {
		for(Player p : players)
		{
			if(p.getNickname().contains(_player_name));
			return p;
		}
		return null;
	}

	public ArrayList<String> getAndUpdateScores() {
		scores.clear();
		for (Player p : players) {
			scores.add(p.getNickname() + "\t\t" + p.getScore());
		}
		return scores;
	}

	public void updatePlayerScore(int x, String nick) {
		for (Player p : players) {
			if(p.getNickname().equals(nick))
				if(x>0)
					p.addPoint();
				else
					p.removePoint();
		}
	}

	public Integer putNumber(Integer value, int i, int j, String player) {

		if (matrixCorrect[i][j].equals(value)) {
			if (matrixToSolve[i][j] != value  && matrixToSolve[i][j] == 0) {
				updatePlayerScore(1, player);
				matrixToSolve[i][j] = value;
				//matrixToSolve = matrixCorrect; // debug only
				if(countZero(matrixToSolve)) {
					isFinisched = true;
					return 99;
				}
				else
					return 1;
			} else
				return 0;
		} else {
			if(matrixToSolve[i][j] != 0) {
				return 0;
			}
			updatePlayerScore(0, player);
			return -1;
		}
	}

	public int getRandomNumber(int min, int max) {
	    return (int) ((Math.random() * (max - min)) + min);
	}


	public void createSudoku(Integer[][] SudokuRandom) {
		int arr[] = {0, 9, 9, 9, 9, 9, 9, 9, 9, 9};
		int x=0;
		int numOnRowToCanc = 7;

		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++)
				matrixToSolve[i][j] = SudokuRandom[i][j];
				
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < numOnRowToCanc; j++) {
				x = getRandomNumber(0, 9);
				if(matrixToSolve[i][x] != 0 ) {
					if (arr[matrixToSolve[i][x]] > 0) {
						arr[matrixToSolve[i][x]]--;
						matrixToSolve[i][x] = 0;
					}
					else
						numOnRowToCanc++;
				}
			}

		}
	}

	public void printGame() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++)
				System.out.print(" " + matrixToSolve[i][j] + " ");
			System.out.println();
		}
	}

	public boolean addPlayer(Player player) {
		if (!players.contains(player)) {
			players.add(player);
			return true;
		}
		return false;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void removePlayer(Player user) {
		players.remove(user);
	}

	public int getPlayersNumber() {
		return players.size();
	}

	public boolean countZero(Integer[][] sudoku) {
		int count=0;
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++)
				if(matrixToSolve[i][j]==0)
					count++;
		return (count == 0) ? true : false ;
	}

}

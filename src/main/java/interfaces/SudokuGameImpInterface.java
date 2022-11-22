package interfaces;

import java.util.ArrayList;

import implementation.Player;
import implementation.Sudoku;
import net.tomp2p.p2p.Peer;

public interface SudokuGameImpInterface {

    public Sudoku generateNewSudoku(String game_name) throws Exception;

    public void sendMessage();
    
    public void syncGameList(ArrayList<String> gameList) throws Exception;
    
    public boolean addToPlayerList(String player_nick) throws Exception;

    public boolean findPlayer(String player_nick) throws Exception;

    public boolean join(String game_name) throws Exception;

    public Sudoku searchGame(String game_name) throws ClassNotFoundException;

    public Integer placeNumber(String game_name, int _i, int _j, int _value) throws ClassNotFoundException;

    public void setGameInstance(Sudoku game_instance);

    public void downloadGameList() throws Exception;

    public void updatePlayerList() throws Exception;

    public boolean leaveNetwork();

    public void setUser(Player user);

    public Peer getPeer();
    
    public ArrayList<String> getGameList();
}

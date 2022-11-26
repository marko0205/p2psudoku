package implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import interfaces.MessageListener;

class MessageListenerImpl implements MessageListener {
    int peerid;

    public MessageListenerImpl(int peerid) {
        this.peerid = peerid;
    }

    @Override
    public Object parseMessage(Object obj) {

        return "success";
    }

}

public class SudokuGameImpTest {

    private static SudokuGameImp peer1;
	private static SudokuGameImp peer2;
	private static SudokuGameImp peer3;
	private static SudokuGameImp peer4;
    private static Player player1;
    private static Player player2;
    private static Player player3;
    private static Player player4;

	@BeforeEach
	public void init() throws Exception{

		peer1 = new SudokuGameImp(0, "127.0.0.1", new MessageListenerImpl(0)); 
		peer2 = new SudokuGameImp(1, "127.0.0.1", new MessageListenerImpl(1)); 
		peer3 = new SudokuGameImp(2, "127.0.0.1", new MessageListenerImpl(2)); 
		peer4 = new SudokuGameImp(3, "127.0.0.1", new MessageListenerImpl(3)); 

        player1 = new Player("p1", peer1.getPeer().peerAddress());
        player2 = new Player("p2", peer2.getPeer().peerAddress());
        player3 = new Player("p3", peer3.getPeer().peerAddress());
        player4 = new Player("p4", peer4.getPeer().peerAddress());

        peer1.setUser(player1);
        peer2.setUser(player2);
        peer3.setUser(player3);
        peer4.setUser(player4);
    }


	@AfterEach
	public void terminate() {
		peer1.leaveNetwork();
		peer2.leaveNetwork();
		peer3.leaveNetwork();
		peer4.leaveNetwork();
		peer1 = null;
		peer2 = null;
		peer3 = null;
		peer4 = null;
	}
    
    @Test
    void testAddToPlayerList() throws Exception {
        String nick = "Marco";
        assertTrue(peer1.addToPlayerList(nick));
        // if another user try to join with another nick...
        assertFalse(peer1.addToPlayerList(nick));
    }

    //@Test
    void testFindPlayer() {
        // already been covered by the previous test
    }

    //@Test
    void testUpdatePlayerList() {
        // already been covered by the test: testAddToPlayerList() 
    }

    @Test
    void testGenerateNewSudoku() throws Exception {
        String gameName = "Sudoku1";
        assertNotNull(peer1.generateNewSudoku(gameName));
        
        // if the game name is already used return null
        assertNull(peer2.generateNewSudoku(gameName));
    }

    @Test
    void testJoin() throws Exception {
        String existingGameName = "Sudoku1";
        String notExistingGameName = "Sudoku2";

        peer1.generateNewSudoku(existingGameName);
        
        assertTrue(peer2.join(existingGameName));
        assertFalse(peer3.join(notExistingGameName));
    }

    @Test
    void testLeaveNetwork() throws Exception {

        peer1.addToPlayerList(player1.getNickname());
        assertTrue(peer1.leaveNetwork()); 
        assertFalse(peer1.leaveNetwork());
    }

    @Test
    void testPlaceNumber() throws Exception {
        String existingGameName = "Sudoku1";

        String notExistingGameName = "Sudoku2";

        peer1.generateNewSudoku(existingGameName);
        peer2.join(existingGameName);

        // insert into a valid game, return the point gained or losed 
        assert 0==peer2.placeNumber(existingGameName, 0, 0, 1) || 1==peer2.placeNumber(existingGameName, 0, 0, 1) || -1==peer2.placeNumber(existingGameName, 0, 0, 1);

        // insert into a not valid game, return 0
        assertEquals(null,  peer2.placeNumber(notExistingGameName, 0, 0, 1));

    }

    @Test
    void testSearchGame() throws Exception {
        
        String existingGameName = "Sudoku1";
        String notExistingGameName = "Sudoku2";

        peer1.generateNewSudoku(existingGameName);
        assertNotNull(peer1.searchGame(existingGameName));
        assertNull(peer2.searchGame(notExistingGameName));
        
    }

    @Test
    void testLeaveSudoku() throws Exception{
        String existingGameName = "Sudoku";

        peer3.generateNewSudoku(existingGameName);       
        assertTrue(peer3.leaveSudoku(existingGameName));

        // if all the players left the sudoku this will be deleted
        assertFalse(peer4.join(existingGameName));
    }

   // @Test
    void testSendMessage() {
        // this method doesn't return anything, just send messages at the peer on a specific game (NOT USED in the StaticClient)
    }

    //@Test 
    void testSetGameInstance() {
        // this method is used by the Client, for sync the Sudoku instance every time a player try to place a number (send a message), no rethurn values (NOT USED in the StaticClient)
    }

    @Test
    void testSyncGameList() throws Exception {
        ArrayList<String> gameList = new ArrayList<>();
        String existingGameName = "Sudoku1";

        // before creating the game Sudoku1
        peer1.downloadGameList();
        gameList = peer1.getGameList();
        assertEquals(0, gameList.size());

         // after creating the game Sudoku1
        peer1.gameList.add(existingGameName);
        peer1.syncGameList(gameList);
        peer1.downloadGameList();
        gameList = peer1.getGameList();

        assertEquals(1, gameList.size());
    }


}

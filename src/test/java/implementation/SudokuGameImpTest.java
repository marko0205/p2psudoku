package implementation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.marcoamorosi.Client;

import interfaces.MessageListener;

class MessageListenerImpl implements MessageListener {
    int peerid;

    public MessageListenerImpl(int peerid) {
        this.peerid = peerid;
    }

    @Override
    public Object parseMessage(Object obj) {

        Sudoku gameInstance = (Sudoku) obj;

        return "success";
    }

}

public class SudokuGameImpTest {

    private static SudokuGameImp peer1;
	private static SudokuGameImp peer2;
	private static SudokuGameImp peer3;
	private static SudokuGameImp peer4;


	@BeforeEach
	public void init() throws Exception{

        Player player1 = new Player("p1", null);
        Player player2 = new Player("p2", null);
        Player player3 = new Player("p3", null);
        Player player4 = new Player("p4", null);
		peer1 = new SudokuGameImp(0, "127.0.0.1", new MessageListenerImpl(0)); peer1.setUser(player1);
		peer2 = new SudokuGameImp(1, "127.0.0.1", new MessageListenerImpl(1)); peer2.setUser(player2);
		peer3 = new SudokuGameImp(2, "127.0.0.1", new MessageListenerImpl(2)); peer3.setUser(player3);
		peer4 = new SudokuGameImp(3, "127.0.0.1", new MessageListenerImpl(3)); peer4.setUser(player4);
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
        assertFalse(peer2.addToPlayerList(nick));
    }

    @Test
    void testFindPlayer() {

    }

    @Test
    void testGenerateNewSudoku() {

    }

    @Test
    void testGetGameInstance() {

    }

    @Test
    void testGetSudoku() {

    }

    @Test
    void testJoin() {

    }

    @Test
    void testLeaveNetwork() {

    }

    @Test
    void testPlaceNumber() {

    }

    @Test
    void testSearchGame() {

    }

    @Test
    void testSendMessage() {

    }

    @Test
    void testSetGameInstance() {

    }

    @Test
    void testUpdateGamesList() {

    }

    @Test
    void testUpdatePlayerList() {

    }
}

package implementation;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import interfaces.MessageListener;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDirect;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.ObjectDataReply;
import net.tomp2p.storage.Data;

public class SudokuGameImp  {
	final private Peer peer;
	final private PeerDHT _dht;
	final private int DEFAULT_MASTER_PORT = 4000;

	public ArrayList<String> gameList = new ArrayList<>();
	public ArrayList<String> playerList = new ArrayList<>();
	public Player user;
	public Sudoku game_instance;

	public void setUser(Player user) {
		this.user = user;
	}

	public SudokuGameImp(int _id, String _master_peer, final MessageListener _listener) throws Exception {
		peer = new PeerBuilder(Number160.createHash(_id)).ports(DEFAULT_MASTER_PORT + _id).start();
		_dht = new PeerBuilderDHT(peer).start();

		FutureBootstrap fb = peer.bootstrap().inetAddress(InetAddress.getByName(_master_peer))
				.ports(DEFAULT_MASTER_PORT).start();
		fb.awaitUninterruptibly();
		if (fb.isSuccess()) {
			peer.discover().peerAddress(fb.bootstrapTo().iterator().next()).start().awaitUninterruptibly();
		} else {
			throw new Exception("Error in master peer bootstrap.");
		}

		peer.objectDataReply(new ObjectDataReply() {

			@Override
			public Object reply(PeerAddress sender, Object request) throws Exception {
				return _listener.parseMessage(request);
			}
		});

		// init players list
        try {
            _dht.get(Number160.ONE).start().awaitUninterruptibly();
        } catch (Exception e) {
            _dht.put(Number160.ONE).data(new Data(playerList)).start().awaitUninterruptibly();
        }
		// init games list
        try {
            _dht.get(Number160.ZERO).start().awaitUninterruptibly();
        } catch (Exception e) {
            _dht.put(Number160.ZERO).data(new Data(gameList)).start().awaitUninterruptibly();
        }
	}

	public Sudoku generateNewSudoku(String game_name) throws Exception{

		try {
			game_instance = new Sudoku(game_name, user);
			FutureGet get = _dht.get(Number160.createHash(game_name)).start().awaitUninterruptibly();

			if (get.isSuccess()) {

				if (!get.isEmpty()) { // Match name already used 
					return null;
				}
				_dht.put(Number160.createHash(game_name)).data(new Data(game_instance)).start().awaitUninterruptibly();
				//System.out.print("Generate new sudoku: partita inserita nella dht\n");
				updateGamesList();
				gameList.add(game_name);
				addToGameList(gameList);
				return game_instance;
			}

			} catch (Exception e) {
				e.printStackTrace();
		}
		return null;

	}

	public void sendMessage() {
		/*			try {
		FutureGet get = _dht.get(Number160.createHash(game_name)).start().awaitUninterruptibly();
			if (get.isSuccess()) {
				Sudoku gameIstance = (Sudoku) get.dataMap().values().iterator().next().object();*/
		ArrayList<Player> peers_on_game = game_instance.getPlayers();
		for (Player peer : peers_on_game) {
			//System.out.println("Message Sended-... ");
			FutureDirect futureDirect = _dht.peer().sendDirect(peer.getAddress()).object(game_instance).start();
			futureDirect.awaitUninterruptibly();
		}

	}

	private void addToGameList(ArrayList<String> gameList) throws Exception{

		try {
				_dht.put(Number160.ZERO).data(new Data(gameList)).start().awaitUninterruptibly();
				//System.out.print("game added: "+ gameList.toString());
			} catch ( Exception e) {
				e.printStackTrace();
			}
	}

	public boolean addToPlayerList(String player_nick) throws Exception{

		if (findPlayer(player_nick)) {
			try {
				playerList.add(player_nick);
				_dht.put(Number160.ONE).data(new Data(playerList)).start().awaitUninterruptibly();
				user = new Player(player_nick, peer.peerAddress());
				} catch (Exception e) {
					e.printStackTrace();
				}
			return true;
		}
		else
			return false;


	}

	public boolean findPlayer(String player_nick) throws Exception {
		updatePlayerList();

		if (playerList.contains(player_nick))
			return false;
		else return true;
	}


	public boolean join(String game_name) throws Exception {

    	FutureGet get = _dht.get(Number160.createHash(game_name)).start().awaitUninterruptibly();

		if (get.isSuccess()) {

			if (!get.isEmpty()) {
				game_instance = (Sudoku) get.dataMap().values().iterator().next().object();
			}
			game_instance.addPlayer(this.user);
			_dht.put(Number160.createHash(game_name)).data(new Data(game_instance)).start().awaitUninterruptibly();
			sendMessage();//return gameInstace.getGame();
			return true;
		}

		return false;
	}

	public Sudoku searchGame(String game_name) throws ClassNotFoundException {

		try {
			FutureGet futureGet = _dht.get(Number160.createHash(game_name)).start();
			futureGet.awaitUninterruptibly();

			if (!futureGet.isSuccess() || futureGet.isEmpty())
				return null;
			return (Sudoku) futureGet.dataMap().values().iterator().next().object();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Integer[][] getSudoku(String game_name) {
		return null;
	}

	public Integer placeNumber(String game_name, int _i, int _j, int _value) throws ClassNotFoundException {
		//this.game_instance = searchGame(game_name);

		int score = 0;
		try {
			FutureGet futureGet = _dht.get(Number160.createHash(game_name)).start();
			futureGet.awaitUninterruptibly();

			if (!futureGet.isSuccess() || futureGet.isEmpty())
				return null;

			score = game_instance.putNumber(_value, _i, _j, user.getNickname());
			if (score != 0)
				_dht.put(Number160.createHash(game_name)).data(new Data(game_instance)).start().awaitUninterruptibly();
			
				// game finisced, remove the match from the list
			if (score == 99) {
				updateGamesList();
				gameList.remove(game_name);
				addToGameList(gameList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			//sendMessage();
		}

		sendMessage();

		return null;

		//////////// rebuild
//		if (game_instance == null)
//			return null;
//
//
//		System.out.println("i: " +_i + " J: " + _j + " val: " + _value);
//		int score = this.game_instance.putNumber(_value, _i, _j, user);
//
//		try {
//			_dht.put(Number160.createHash(game_name)).data(new Data(this.game_instance)).start().awaitUninterruptibly();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		sendMessage();
//		return null;
	}

    public Sudoku getGameInstance() {
		return game_instance;
	}

	public void setGameInstance(Sudoku game_instance) {
		this.game_instance = game_instance;
	}

	public void updateGamesList() throws Exception {

		try {
			FutureGet get = _dht.get(Number160.ZERO).start().awaitUninterruptibly();
			if (!get.isEmpty()) {
				gameList = (ArrayList<String>) get.dataMap().values().iterator().next().object();
				//System.out.println("Game received " + gameList.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    public void updatePlayerList() throws Exception {

        try {
        	FutureGet get = _dht.get(Number160.ONE).start().awaitUninterruptibly();
			if(get.isSuccess()) {
	    		if (!get.isEmpty()) {
	        		playerList = (ArrayList<String>) get.dataMap().values().iterator().next().object();
	        	}
			}

        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

	public boolean leaveNetwork() {
		try {
			updatePlayerList();
			FutureGet get = _dht.get(Number160.ONE).start().awaitUninterruptibly();

			if(get.isSuccess()) {

				if(!get.isEmpty()) {
					playerList = (ArrayList<String>) get.dataMap().values().iterator().next().object();
					playerList.remove(user.getNickname());
					_dht.put(Number160.ONE).data(new Data(playerList)).start().awaitUninterruptibly();
				}
				return true;
			}
			
			_dht.peer().announceShutdown().start().awaitUninterruptibly();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}

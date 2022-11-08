package implementation;

import java.io.Serializable;

import net.tomp2p.peers.PeerAddress;

public class Player implements Serializable {

	private final String nickname;
	private final PeerAddress address;
	private int score;

	public Player(String nickname, PeerAddress address) {
		this.nickname = nickname;
		this.address = address;
	}


	public void addPoint() {
		this.score++;
	}

	public void removePoint() {
		this.score--;
	}


	public int getScore() {
		return score;
	}


	public void setScore(int score) {
		this.score = score;
	}


	public String getNickname() {
		return nickname;
	}


	public PeerAddress getAddress() {
		return address;
	}


}

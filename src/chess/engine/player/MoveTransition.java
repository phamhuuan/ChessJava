package chess.engine.player;

import chess.engine.board.Board;

public class MoveTransition {
	private final Board toBoard;
	private final MoveStatus moveStatus;

	public MoveTransition(Board toBoard, MoveStatus moveStatus) {
		this.toBoard = toBoard;
		this.moveStatus = moveStatus;
	}

	public Board getToBoard() {
		return toBoard;
	}

	public MoveStatus getMoveStatus() {
		return moveStatus;
	}
}

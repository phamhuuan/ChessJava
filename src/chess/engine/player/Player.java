package chess.engine.player;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.Move;
import chess.engine.piece.King;
import chess.engine.piece.Piece;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {
	protected final Board board;
	protected final King playerKing;
	private final List<Move> legalMoves;
	private final boolean isInCheck;

	public Player(Board board, List<Move> playerLegalMove, List<Move> opponentLegalMove) {
		this.board = board;
		this.playerKing = establishKing();
		this.isInCheck = !Player.calculateAttackOnTile(this.playerKing.getPiecePositionX(), this.playerKing.getPiecePositionY(), opponentLegalMove).isEmpty();
 		this.legalMoves = ImmutableList.copyOf(Iterables.concat(playerLegalMove, calculateKingCastles(playerLegalMove, opponentLegalMove)));
	}

	public List<Move> getLegalMoves() {
		return legalMoves;
	}

	protected static List<Move> calculateAttackOnTile(int piecePositionX, int piecePositionY, List<Move> opponentLegalMove) {
		final List<Move> attackMove = new ArrayList<>();
		for(final Move move : opponentLegalMove) {
			if(piecePositionX == move.getDestinationPositionX() && piecePositionY == move.getDestinationPositionY()) {
				attackMove.add(move);
			}
		}
		return ImmutableList.copyOf(attackMove);
	}

	private King establishKing() {
		for(final Piece piece : getActivePieces()) {
			if(piece.getPieceType().isKing()) {
				return (King)piece;
			}
		}
		throw new RuntimeException("Invalid board");
	}

	public boolean isMoveLegal(final Move move) {
		return legalMoves.contains(move);
	}

	public boolean isInCheck() {
		return isInCheck;
	}

	public boolean isInCheckmate() {
		return isInCheck && doesNotHaveEscapeMoves();
	}

	public boolean isInStaleMate() {
		return !isInCheck && doesNotHaveEscapeMoves();
	}

	protected boolean doesNotHaveEscapeMoves() {
		for (Move move : legalMoves) {
			if (makeMove(move).getMoveStatus().isDone()) {
				return false;
			}
		}
		return true;
	}

	public boolean isCastled() {
		return false;
	}

	public MoveTransition makeMove(final Move move) {
		if(!isMoveLegal(move)) {
			return new MoveTransition(board, MoveStatus.ILLEGAL_MOVE);
		}
		final Board transitionBoard = move.execute();
		if(transitionBoard == null || transitionBoard.getCurrentPlayer().getOpponent().isInCheck()) {
			return new MoveTransition(board, MoveStatus.LEAVES_PLAYER_IN_CHECK);
		}
		return new MoveTransition(transitionBoard, MoveStatus.DONE);
	}

	public MoveTransition unmakeMove(final Move move) {
		return new MoveTransition(move.undo(), MoveStatus.DONE);
	}

	public King getPlayerKing() {
		return playerKing;
	}

	public abstract List<Piece> getActivePieces();
	public abstract Alliance getAlliance();
	public abstract Player getOpponent();
	protected abstract List<Move> calculateKingCastles(List<Move> playerLegals, List<Move> opponentLegals);
}

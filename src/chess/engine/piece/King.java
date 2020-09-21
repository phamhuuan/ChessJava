package chess.engine.piece;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Move;
import chess.engine.board.Move.AttackMove;
import chess.engine.board.Move.MajorMove;
import chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
	private final int[][] CANDIDATE_MOVE_COORDINATES = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
	private final boolean isCastled;
	private final boolean kingSideCastleCapable;
	private final boolean queenSideCastleCapable;

	public King(int piecePositionX, int piecePositionY, Alliance pieceAlliance, boolean isFirstMove, boolean isCastled, boolean kingSideCastleCapable, boolean queenSideCastleCapable) {
		super(PieceType.KING, piecePositionX, piecePositionY, pieceAlliance, isFirstMove);
		this.isCastled = isCastled;
		this.kingSideCastleCapable = kingSideCastleCapable;
		this.queenSideCastleCapable = queenSideCastleCapable;
	}

	public boolean isCastled() {
		return isCastled;
	}

	public boolean isKingSideCastleCapable() {
		return kingSideCastleCapable;
	}

	public boolean isQueenSideCastleCapable() {
		return queenSideCastleCapable;
	}

	@Override
	public List<Move> calculateLegalMoves(Board board) {
		final List<Move> legalMoves = new ArrayList<>();
		for(final int[] currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
			int candidateDestinationPositionX = getPiecePositionX() + currentCandidateOffset[0], candidateDestinationPositionY = getPiecePositionY() + currentCandidateOffset[1];
			if (BoardUtils.isValidTileCoordinate(candidateDestinationPositionX, candidateDestinationPositionY)) {
				final Tile candidateDestinationTile = board.getTile(candidateDestinationPositionX, candidateDestinationPositionY);
				if(!candidateDestinationTile.isTileOccupied()) {
					legalMoves.add(new MajorMove(board, this, candidateDestinationPositionX, candidateDestinationPositionY));
				} else {
					final Piece pieceAtDestination = candidateDestinationTile.getPiece();
					final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
					if(getPieceAlliance() != pieceAlliance) {
						legalMoves.add(new AttackMove(board, this, candidateDestinationPositionX, candidateDestinationPositionY, pieceAtDestination));
					}
				}
			}
		}
		return ImmutableList.copyOf(legalMoves);
	}

	@Override
	public King movePiece(final Move move) {
		return new King(move.getDestinationPositionX(), move.getDestinationPositionY(), move.getMovedPiece().getPieceAlliance(), true, move.isCastlingMove(), false, false);
	}

	@Override
	public String toString() {
		return PieceType.KING.toString();
	}
}

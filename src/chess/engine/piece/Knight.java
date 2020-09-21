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

public class Knight extends Piece {
	private final int[][] CANDIDATE_MOVE_COORDINATES = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};

	public Knight(int piecePositionX, int piecePositionY, Alliance pieceAlliance, boolean isFirstMove) {
		super(PieceType.KNIGHT, piecePositionX, piecePositionY, pieceAlliance, isFirstMove);
	}

	@Override
	public List<Move> calculateLegalMoves(Board board) {
		final List<Move> legalMoves = new ArrayList<>();
		for(final int[] currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
			int candidateDestinationPositionX, candidateDestinationPositionY;
			candidateDestinationPositionX = getPiecePositionX() + currentCandidateOffset[0];
			candidateDestinationPositionY = getPiecePositionY() + currentCandidateOffset[1];
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
	public Knight movePiece(final Move move) {
		return new Knight(move.getDestinationPositionX(), move.getDestinationPositionY(), move.getMovedPiece().getPieceAlliance(), true);
	}

	@Override
	public String toString() {
		return PieceType.KNIGHT.toString();
	}
}

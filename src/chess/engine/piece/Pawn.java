package chess.engine.piece;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.BoardUtils;
import chess.engine.board.Move;
import chess.engine.board.Move.*;
import chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
	private final int[][] CANDIDATE_MOVE_COORDINATES = {{0, 1}, {0, 2}, {1, 1}, {-1, 1}};

	public Pawn(int piecePositionX, int piecePositionY, Alliance pieceAlliance, boolean isFirstMove) {
		super(PieceType.PAWN, piecePositionX, piecePositionY, pieceAlliance, isFirstMove);
	}

	@Override
	public List<Move> calculateLegalMoves(Board board) {
		final List<Move> legalMoves = new ArrayList<>();
		for (final int[] currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
			int candidateDestinationPositionX = getPiecePositionX() + currentCandidateOffset[0], candidateDestinationPositionY = getPiecePositionY() + getPieceAlliance().getDirection() * currentCandidateOffset[1];
			if (!BoardUtils.isValidTileCoordinate(candidateDestinationPositionX, candidateDestinationPositionY)) {
				continue;
			}
			if (currentCandidateOffset[0] == 0 && currentCandidateOffset[1] == 1 && !board.getTile(candidateDestinationPositionX, candidateDestinationPositionY).isTileOccupied()) {
				if (getPieceAlliance().isPawnPromotionSquare(candidateDestinationPositionY)) {
					legalMoves.add(new PawnPromotion(new PawnMove(board, this, candidateDestinationPositionX, candidateDestinationPositionY)));
				} else {
					legalMoves.add(new PawnMove(board, this, candidateDestinationPositionX, candidateDestinationPositionY));
				}
			} else if (currentCandidateOffset[0] == 0 && currentCandidateOffset[1] == 2 && isFirstMove() && ((getPieceAlliance().isBlack() && getPiecePositionY() == 1) || (getPieceAlliance().isWhite()) && getPiecePositionY() == 6)) {
				final int behindCandidateDestinationPositionX = getPiecePositionX(), behindCandidateDestinationPositionY = getPiecePositionY() + getPieceAlliance().getDirection();
				if (!board.getTile(behindCandidateDestinationPositionX, behindCandidateDestinationPositionY).isTileOccupied() && !board.getTile(candidateDestinationPositionX, candidateDestinationPositionY).isTileOccupied()) {
					legalMoves.add(new PawnJump(board, this, candidateDestinationPositionX, candidateDestinationPositionY));
				}
			} else if (currentCandidateOffset[1] == 1 && currentCandidateOffset[0] != 0) {
				final Tile candidateDestinationTile = board.getTile(candidateDestinationPositionX, candidateDestinationPositionY);
				if (candidateDestinationTile.isTileOccupied()) {
					final Piece pieceAtDestination = candidateDestinationTile.getPiece();
					final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
					if (getPieceAlliance() != pieceAlliance) {
						if (getPieceAlliance().isPawnPromotionSquare(candidateDestinationPositionY)) {
							legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidateDestinationPositionX, candidateDestinationPositionY, pieceAtDestination)));
						} else {
							legalMoves.add(new PawnAttackMove(board, this, candidateDestinationPositionX, candidateDestinationPositionY, pieceAtDestination));
						}
					}
				} else if (board.getEnPassantPawn() != null && (board.getEnPassantPawn().getPiecePositionY() == getPiecePositionY() && (board.getEnPassantPawn().getPiecePositionX() == getPiecePositionX() + currentCandidateOffset[0]))) {
					final Piece pieceAtDestination = board.getEnPassantPawn();
					if (getPieceAlliance() != pieceAtDestination.getPieceAlliance()) {
						legalMoves.add(new PawnEnPassantAttackMove(board, this, candidateDestinationPositionX, candidateDestinationPositionY, pieceAtDestination));
					}
				}
			}
		}
		return ImmutableList.copyOf(legalMoves);
	}

	public Piece getPromotionPiece() {
		return new Queen(getPiecePositionX(), getPiecePositionY(), getPieceAlliance(), false);
	}

	@Override
	public Pawn movePiece(final Move move) {
		return new Pawn(move.getDestinationPositionX(), move.getDestinationPositionY(), move.getMovedPiece().getPieceAlliance(), false);
	}

	@Override
	public String toString() {
		return PieceType.PAWN.toString();
	}
}

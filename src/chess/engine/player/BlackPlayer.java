package chess.engine.player;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.Move;
import chess.engine.board.Move.KingSideCastleMove;
import chess.engine.board.Move.QueenSideCastleMove;
import chess.engine.board.Tile;
import chess.engine.piece.Piece;
import chess.engine.piece.Rook;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class BlackPlayer extends Player {

	public BlackPlayer(Board board, List<Move> whiteStandardLegalMoves, List<Move> blackStandardLegalMoves) {
		super(board, blackStandardLegalMoves, whiteStandardLegalMoves);

	}

	@Override
	public List<Piece> getActivePieces() {
		return board.getBlackPiece();
	}

	@Override
	public Alliance getAlliance() {
		return Alliance.BLACK;
	}

	@Override
	public Player getOpponent() {
		return board.getWhitePlayer();
	}

	@Override
	protected List<Move> calculateKingCastles(List<Move> playerLegals, List<Move> opponentLegals) {
		final List<Move> kingCastles = new ArrayList<>();
		if(playerKing.isFirstMove() && !isInCheck()) {
			if(!board.getTile(5, 0).isTileOccupied() && !board.getTile(6, 0).isTileOccupied()) {
				final Tile rookTile = board.getTile(7, 0);
				if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove() && rookTile.getPiece().getPieceType().isRook()) {
					if(Player.calculateAttackOnTile(5, 0, opponentLegals).isEmpty() && Player.calculateAttackOnTile(6, 0, opponentLegals).isEmpty()) {
						kingCastles.add(new KingSideCastleMove(board, playerKing, 6, 0, (Rook) rookTile.getPiece(), rookTile.getPositionX(), rookTile.getPositionY(), 5, 0));
					}
				}
			}
			if(!board.getTile(1, 0).isTileOccupied() && !board.getTile(2, 0).isTileOccupied() && !board.getTile(3, 0).isTileOccupied()) {
				final Tile rookTile = board.getTile(0, 0);
				if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove() && rookTile.getPiece().getPieceType().isRook()) {
					if(Player.calculateAttackOnTile(2, 0, opponentLegals).isEmpty() && Player.calculateAttackOnTile(3, 0, opponentLegals).isEmpty()) {
						kingCastles.add(new QueenSideCastleMove(board, playerKing, 2, 0, (Rook) rookTile.getPiece(), rookTile.getPositionX(), rookTile.getPositionY(), 3, 0));
					}
				}
			}
		}
		return ImmutableList.copyOf(kingCastles);
	}
}

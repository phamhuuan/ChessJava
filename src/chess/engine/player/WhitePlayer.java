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

public class WhitePlayer extends Player {

	public WhitePlayer(Board board, List<Move> whiteStandardLegalMoves, List<Move> blackStandardLegalMoves) {
		super(board, whiteStandardLegalMoves, blackStandardLegalMoves);
	}

	@Override
	public List<Piece> getActivePieces() {
		return board.getWhitePiece();
	}

	@Override
	public Alliance getAlliance() {
		return Alliance.WHITE;
	}

	@Override
	public Player getOpponent() {
		return board.getBlackPlayer();
	}

	@Override
	protected List<Move> calculateKingCastles(List<Move> playerLegals, List<Move> opponentLegals) {
		final List<Move> kingCastles = new ArrayList<>();
		if(playerKing.isFirstMove() && !isInCheck()) {
			if(!board.getTile(5, 7).isTileOccupied() && !board.getTile(6, 7).isTileOccupied()) {
				final Tile rookTile = board.getTile(7, 7);
				if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove() && rookTile.getPiece().getPieceType().isRook()) {
					if(Player.calculateAttackOnTile(5, 7, opponentLegals).isEmpty() && Player.calculateAttackOnTile(6, 7, opponentLegals).isEmpty()) {
						kingCastles.add(new KingSideCastleMove(board, playerKing, 6, 7, (Rook) rookTile.getPiece(), rookTile.getPositionX(), rookTile.getPositionY(), 5, 7));
					}
				}
			}
			if(!board.getTile(1, 7).isTileOccupied() && !board.getTile(2, 7).isTileOccupied() && !board.getTile(3, 7).isTileOccupied()) {
				final Tile rookTile = board.getTile(0, 7);
				if(rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove() && rookTile.getPiece().getPieceType().isRook()) {
					if(Player.calculateAttackOnTile(2, 7, opponentLegals).isEmpty() && Player.calculateAttackOnTile(3, 7, opponentLegals).isEmpty()) {
						kingCastles.add(new QueenSideCastleMove(board, playerKing, 2, 7, (Rook) rookTile.getPiece(), rookTile.getPositionX(), rookTile.getPositionY(), 3, 7));
					}
				}
			}
		}
		return ImmutableList.copyOf(kingCastles);
	}
}

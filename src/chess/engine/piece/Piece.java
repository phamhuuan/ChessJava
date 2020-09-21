package chess.engine.piece;

import chess.engine.Alliance;
import chess.engine.board.Board;
import chess.engine.board.Move;

import java.util.List;

public abstract class Piece {

	private final PieceType pieceType;
	private final int piecePositionX, piecePositionY;
	private final Alliance pieceAlliance;
	private final boolean isFirstMove;


	public Piece(PieceType pieceType, final int piecePositionX, final int piecePositionY, final Alliance pieceAlliance, final boolean isFirstMove) {
		this.pieceType = pieceType;
		this.piecePositionX = piecePositionX;
		this.piecePositionY = piecePositionY;
		this.pieceAlliance = pieceAlliance;
		this.isFirstMove = isFirstMove;
	}

	@Override
	public boolean equals(final Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof Piece)) {
			return false;
		}
		final Piece piece = (Piece) object;
		return piecePositionX == piece.getPiecePositionX() && piecePositionY == piece.getPiecePositionY() && pieceType == piece.getPieceType() && pieceAlliance == piece.getPieceAlliance() && isFirstMove == piece.isFirstMove();
	}

	public int getPieceValue() {
		return pieceType.getPieceValue();
	}

	public int getPiecePositionX() {
		return piecePositionX;
	}

	public int getPiecePositionY() {
		return piecePositionY;
	}

	public boolean isFirstMove() {
		return isFirstMove;
	}

	public PieceType getPieceType() {
		return pieceType;
	}

	public Alliance getPieceAlliance() {
		return pieceAlliance;
	}

	public abstract List<Move> calculateLegalMoves(final Board board);

	public abstract Piece movePiece(Move move);

	public enum PieceType {
		PAWN("P", 1) {
			@Override
			public boolean isKing() {
				return false;
			}

			@Override
			public boolean isRook() {
				return false;
			}

		},
		KNIGHT("N", 3) {
			@Override
			public boolean isKing() {
				return false;
			}

			@Override
			public boolean isRook() {
				return false;
			}

		},
		BISHOP("B", 3) {
			@Override
			public boolean isKing() {
				return false;
			}

			@Override
			public boolean isRook() {
				return false;
			}

		},
		ROOK("R", 5) {
			@Override
			public boolean isKing() {
				return false;
			}

			@Override
			public boolean isRook() {
				return true;
			}

		},
		QUEEN("Q", 9) {
			@Override
			public boolean isKing() {
				return false;
			}

			@Override
			public boolean isRook() {
				return false;
			}

		},
		KING("K", 1000) {
			@Override
			public boolean isKing() {
				return true;
			}

			@Override
			public boolean isRook() {
				return false;
			}

		};
		private final String pieceName;
		private final int pieceValue;

		PieceType(String pieceName, int pieceValue) {
			this.pieceName = pieceName;
			this.pieceValue = pieceValue;
		}

		@Override
		public String toString() {
			return this.pieceName;
		}

		public abstract boolean isKing();

		public abstract boolean isRook();

		public int getPieceValue() {
			return pieceValue;
		}
	}
}

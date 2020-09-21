package chess.engine.board;

import chess.engine.board.Board.Builder;
import chess.engine.piece.Pawn;
import chess.engine.piece.Piece;
import chess.engine.piece.Rook;

public abstract class Move {
	protected final Board board;
	protected final Piece movedPiece;
	protected final int destinationPositionX, destinationPositionY;
	protected final boolean isFirstMove;

	public Move(Board board, Piece movedPiece, int destinationPositionX, int destinationPositionY) {
		this.board = board;
		this.movedPiece = movedPiece;
		this.destinationPositionX = destinationPositionX;
		this.destinationPositionY = destinationPositionY;
		this.isFirstMove = movedPiece.isFirstMove();
	}

	public Move(Board board, int destinationPositionX, int destinationPositionY) {
		this.board = board;
		this.destinationPositionX = destinationPositionX;
		this.destinationPositionY = destinationPositionY;
		this.movedPiece = null;
		this.isFirstMove = false;
	}

	/**
	 * get current position of piece
	 * @return x
	 */
	private int getCurrentPositionX() {
		return movedPiece.getPiecePositionX();
	}

	/**
	 * get current position of piece
	 * @return y
	 */
	private int getCurrentPositionY() {
		return movedPiece.getPiecePositionY();
	}

	/**
	 * get destination position of piece
	 * @return x
	 */
	public int getDestinationPositionX() {
		return destinationPositionX;
	}

	/**
	 * get destination position of piece
	 * @return y
	 */
	public int getDestinationPositionY() {
		return destinationPositionY;
	}

	/**
	 * get moved piece
	 * @return moved piece
	 */
	public Piece getMovedPiece() {
		return movedPiece;
	}

	public Board getBoard() {
		return board;
	}

	/**
	 * execute move
	 * @return next board
	 */
	public Board execute() {
		final Builder builder = new Builder();
		for (final Piece piece : board.getCurrentPlayer().getActivePieces()) {
			if (!movedPiece.equals(piece)) {
				builder.setPiece(piece);
			}
		}
		for (final Piece piece : board.getCurrentPlayer().getOpponent().getActivePieces()) {
			builder.setPiece(piece);
		}
		//move the moved piece
		builder.setPiece(movedPiece.movePiece(this));
		builder.setNextMoveMaker(board.getCurrentPlayer().getOpponent().getAlliance());
		return builder.build();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (!(object instanceof Move)) return false;
		Move move = (Move) object;
		return getCurrentPositionX() == move.getCurrentPositionX() && getCurrentPositionY() == move.getCurrentPositionY() && getDestinationPositionX() == move.getDestinationPositionX() && getDestinationPositionY() == move.getDestinationPositionY() && getMovedPiece().equals(move.getMovedPiece());
	}

	/**
	 * get status of piece
	 * @return if is attack ? true : false
	 */
	public boolean isAttack() {
		return false;
	}

	/**
	 * get status of piece
	 * castling move still WIP
	 * @return is castling move ? true : false
	 */
	public boolean isCastlingMove() {
		return false;
	}

	/**
	 * get attacked piece
	 * @return attacked piece
	 */
	public Piece getAttackedPiece() {
		return null;
	}

	/**
	 * handle undo move
	 * @return previous board
	 */
	public Board undo() {
		final Builder builder = new Builder();
		for(Piece piece : board.getAllPieces()) {
			builder.setPiece(piece);
		}
		builder.setNextMoveMaker(this.board.getCurrentPlayer().getAlliance());
		return builder.build();
	}

	public static final class MajorMove extends Move {
		public MajorMove(Board board, Piece movedPiece, int destinationPositionX, int destinationPositionY) {
			super(board, movedPiece, destinationPositionX, destinationPositionY);
		}

		@Override
		public boolean equals(Object object) {
			return this == object || object instanceof MajorMove && super.equals(object);
		}

		@Override
		public String toString() {
			return movedPiece.getPieceType().toString() + ":" + BoardUtils.positionToCoordinate(movedPiece.getPiecePositionX(), movedPiece.getPiecePositionY()) + "->" + BoardUtils.positionToCoordinate(getDestinationPositionX(), getDestinationPositionY());
		}
	}

	public static class AttackMove extends Move {
		private final Piece attackedPiece;

		public AttackMove(Board board, Piece movedPiece, int destinationPositionX, int destinationPositionY, Piece attackedPiece) {
			super(board, movedPiece, destinationPositionX, destinationPositionY);
			this.attackedPiece = attackedPiece;
		}

		@Override
		public boolean equals(final Object object) {
			if (this == object) return true;
			if (!(object instanceof AttackMove)) return false;
			AttackMove attackMove = (AttackMove) object;
			return super.equals(attackMove) && getAttackedPiece().equals(attackMove.getAttackedPiece());
		}

		@Override
		public String toString() {
			return movedPiece.getPieceType().toString() + ":" + BoardUtils.positionToCoordinate(movedPiece.getPiecePositionX(), movedPiece.getPiecePositionY()) + "x" + BoardUtils.positionToCoordinate(getDestinationPositionX(), getDestinationPositionY());
		}

		@Override
		public boolean isAttack() {
			return true;
		}

		@Override
		public Piece getAttackedPiece() {
			return attackedPiece;
		}
	}

	public static final class PawnMove extends Move {
		public PawnMove(Board board, Piece movedPiece, int destinationPositionX, int destinationPositionY) {
			super(board, movedPiece, destinationPositionX, destinationPositionY);
		}

		@Override
		public boolean equals(final Object other) {
			return this == other || other instanceof PawnMove && super.equals(other);
		}

		@Override
		public String toString() {
			return movedPiece.getPieceType().toString() + ":" + BoardUtils.positionToCoordinate(movedPiece.getPiecePositionX(), movedPiece.getPiecePositionY()) + "->" + BoardUtils.positionToCoordinate(getDestinationPositionX(), getDestinationPositionY());
		}
	}

	public static final class PawnPromotion extends Move {
		final Move decoratedMove;
		final Pawn promotedPawn;
		public PawnPromotion(final Move decoratedMove) {
			super(decoratedMove.getBoard(), decoratedMove.getMovedPiece(), decoratedMove.getDestinationPositionX(), decoratedMove.getDestinationPositionY());
			this.decoratedMove = decoratedMove;
			this.promotedPawn = (Pawn) decoratedMove.getMovedPiece();
		}

		@Override
		public boolean equals(final Object object) {
			return this == object || object instanceof PawnPromotion && (super.equals(object));
		}

		@Override
		public Board execute() {
			final Board pawnMovedBoard = decoratedMove.execute();
			final Builder builder = new Builder();
			for (final Piece piece : pawnMovedBoard.getCurrentPlayer().getActivePieces()) {
				if (!promotedPawn.equals(piece)) {
					builder.setPiece(piece);
				}
			}
			for (final Piece piece : pawnMovedBoard.getCurrentPlayer().getOpponent().getActivePieces()) {
				builder.setPiece(piece);
			}
			builder.setPiece(promotedPawn.getPromotionPiece().movePiece(this));
			builder.setNextMoveMaker(pawnMovedBoard.getCurrentPlayer().getAlliance());
			return builder.build();
		}

		@Override
		public boolean isAttack() {
			return decoratedMove.isAttack();
		}

		@Override
		public Piece getAttackedPiece() {
			return decoratedMove.getAttackedPiece();
		}
	}

	public static final class PawnJump extends Move {
		public PawnJump(Board board, Piece movedPiece, int destinationPositionX, int destinationPositionY) {
			super(board, movedPiece, destinationPositionX, destinationPositionY);
		}

		@Override
		public Board execute() {
			final Builder builder = new Builder();
			for (final Piece piece : board.getCurrentPlayer().getActivePieces()) {
				if (!this.movedPiece.equals(piece)) {
					builder.setPiece(piece);
				}
			}
			for (final Piece piece : board.getCurrentPlayer().getOpponent().getActivePieces()) {
				builder.setPiece(piece);
			}
			final Pawn movedPawn = (Pawn) movedPiece.movePiece(this);
			builder.setPiece(movedPawn);
			builder.setEnPassantPawn(movedPawn);
			builder.setNextMoveMaker(board.getCurrentPlayer().getOpponent().getAlliance());
			return builder.build();
		}

		@Override
		public boolean equals(final Object other) {
			return this == other || other instanceof PawnJump && super.equals(other);
		}

		@Override
		public String toString() {
			return movedPiece.getPieceType().toString() + ":" + BoardUtils.positionToCoordinate(movedPiece.getPiecePositionX(), movedPiece.getPiecePositionY()) + "->" + BoardUtils.positionToCoordinate(getDestinationPositionX(), getDestinationPositionY());
		}
	}

	public static class PawnAttackMove extends AttackMove {
		public PawnAttackMove(Board board, Piece movedPiece, int destinationPositionX, int destinationPositionY, Piece attackedPiece) {
			super(board, movedPiece, destinationPositionX, destinationPositionY, attackedPiece);
		}

		@Override
		public boolean equals (final Object object) {
			return this == object || object instanceof PawnAttackMove && super.equals(object);
		}

		@Override
		public String toString() {
			return movedPiece.getPieceType().toString() + ":" + BoardUtils.positionToCoordinate(movedPiece.getPiecePositionX(), movedPiece.getPiecePositionY()) + "x" + BoardUtils.positionToCoordinate(getDestinationPositionX(), getDestinationPositionY());
		}
	}

	public static final class PawnEnPassantAttackMove extends PawnAttackMove {
		public PawnEnPassantAttackMove(Board board, Piece movedPiece, int destinationPositionX, int destinationPositionY, Piece attackedPiece) {
			super(board, movedPiece, destinationPositionX, destinationPositionY, attackedPiece);
		}

		@Override
		public boolean equals(final Object object) {
			return this == object || object instanceof PawnEnPassantAttackMove && super.equals(object);
		}

		@Override
		public Board execute() {
			final Builder builder = new Builder();
			for(final Piece piece : board.getCurrentPlayer().getActivePieces()) {
				if (!movedPiece.equals(piece)) {
					builder.setPiece(piece);
				}
			}
			for(final Piece piece : board.getCurrentPlayer().getOpponent().getActivePieces()) {
				if (!piece.equals(getAttackedPiece())) {
					builder.setPiece(piece);
				}
			}
			builder.setPiece(movedPiece.movePiece(this));
			builder.setNextMoveMaker(board.getCurrentPlayer().getOpponent().getAlliance());
			return builder.build();
		}
	}

	public static abstract class CastleMove extends Move {
		protected final Rook castleRook;
		protected final int castleRookStartX, castleRookStartY, castleRookPositionX, castleRookPositionY;

		public CastleMove(Board board, Piece movedPiece, int destinationPositionX, int destinationPositionY, Rook castleRook, int castleRookStartX, int castleRookStartY, int castleRookPositionX, int castleRookPositionY) {
			super(board, movedPiece, destinationPositionX, destinationPositionY);
			this.castleRook = castleRook;
			this.castleRookStartX = castleRookStartX;
			this.castleRookStartY = castleRookStartY;
			this.castleRookPositionX = castleRookPositionX;
			this.castleRookPositionY = castleRookPositionY;
		}

		@Override
		public boolean isCastlingMove() {
			return true;
		}

		@Override
		public Board execute() {
			final Builder builder = new Builder();
			for (final Piece piece : board.getCurrentPlayer().getActivePieces()) {
				if (!movedPiece.equals(piece) && !castleRook.equals(piece)) {
					builder.setPiece(piece);
				}
			}
			for (final Piece piece : board.getCurrentPlayer().getOpponent().getActivePieces()) {
				builder.setPiece(piece);
			}
			builder.setPiece(movedPiece.movePiece(this));
			builder.setPiece(new Rook(castleRookPositionX, castleRookPositionY, castleRook.getPieceAlliance(), false));
			builder.setNextMoveMaker(board.getCurrentPlayer().getOpponent().getAlliance());
			return builder.build();
		}

		@Override
		public boolean equals(final Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof CastleMove)) {
				return false;
			}
			final CastleMove otherCastleMove = (CastleMove) other;
			return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
		}

		public Rook getCastleRook() {
			return castleRook;
		}
	}

	public static class KingSideCastleMove extends CastleMove {
		public KingSideCastleMove(Board board, Piece movedPiece, int destinationPositionX, int destinationPositionY, Rook castleRook, int castleRookStartX, int castleRookStartY, int castleRookPositionX, int castleRookPositionY) {
			super(board, movedPiece, destinationPositionX, destinationPositionY, castleRook, castleRookStartX, castleRookStartY, castleRookPositionX, castleRookPositionY);
		}

		@Override
		public boolean equals(final Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof KingSideCastleMove)) {
				return false;
			}
			final KingSideCastleMove otherKingSideCastleMove = (KingSideCastleMove) other;
			return super.equals(otherKingSideCastleMove) && this.castleRook.equals(otherKingSideCastleMove.getCastleRook());
		}

		@Override
		public String toString() {
			return "0-0";
		}
	}

	public static class QueenSideCastleMove extends CastleMove {
		public QueenSideCastleMove(Board board, Piece movedPiece, int destinationPositionX, int destinationPositionY, Rook castleRook, int castleRookStartX, int castleRookStartY, int castleRookPositionX, int castleRookPositionY) {
			super(board, movedPiece, destinationPositionX, destinationPositionY, castleRook, castleRookStartX, castleRookStartY, castleRookPositionX, castleRookPositionY);
		}

		@Override
		public boolean equals(final Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof QueenSideCastleMove)) {
				return false;
			}
			final QueenSideCastleMove otherQueenSideCastleMove = (QueenSideCastleMove) other;
			return super.equals(otherQueenSideCastleMove) && this.castleRook.equals(otherQueenSideCastleMove.getCastleRook());
		}

		@Override
		public String toString() {
			return "0-0-0";
		}
	}

	public static class MoveFactory {
		public MoveFactory() {
			throw new RuntimeException("Not instantiable");
		}

		/**
		 * create a move
		 * @param board current board
		 * @param currentPositionX current coordinate
		 * @param currentPositionY current coordinate
		 * @param destinationPositionX destination coordinate
		 * @param destinationPositionY destination coordinate
		 * @return new move
		 */
		public static Move createMove(Board board, int currentPositionX, int currentPositionY, int destinationPositionX, int destinationPositionY) {
			for (final Move move : board.getAllLegalMove()) {
				if (move.getCurrentPositionX() == currentPositionX && move.getCurrentPositionY() == currentPositionY && move.getDestinationPositionX() == destinationPositionX && move.getDestinationPositionY() == destinationPositionY) {
					return move;
				}
			}
			return null;
		}
	}
}

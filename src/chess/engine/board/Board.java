package chess.engine.board;

import chess.engine.Alliance;
import chess.engine.piece.*;
import chess.engine.player.BlackPlayer;
import chess.engine.player.Player;
import chess.engine.player.WhitePlayer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
	private final List<Tile> gameBoard;
	private final List<Piece> whitePiece, blackPiece;
	private final WhitePlayer whitePlayer;
	private final BlackPlayer blackPlayer;
	private final Player currentPlayer;
	private final Pawn enPassantPawn;

	public Board(Builder builder) {
		this.gameBoard = createGameBoard(builder);
		this.whitePiece = calculateActivePiece(this.gameBoard, Alliance.WHITE);
		this.blackPiece = calculateActivePiece(this.gameBoard, Alliance.BLACK);
		this.enPassantPawn = builder.enPassantPawn;
		final List<Move> whiteStandardLegalMoves = calculateLegalMove(this.whitePiece), blackStandardLegalMoves = calculateLegalMove(this.blackPiece);
		this.whitePlayer = new WhitePlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
		this.blackPlayer = new BlackPlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
		this.currentPlayer = builder.nextMoveMaker.choosePlayer(whitePlayer, blackPlayer);
	}

	public Pawn getEnPassantPawn() {
		return enPassantPawn;
	}

	/**
	 * get white piece
	 * @return white piece
	 */
	public List<Piece> getWhitePiece() {
		return whitePiece;
	}

	/**
	 * get black piece
	 * @return black piece
	 */
	public List<Piece> getBlackPiece() {
		return blackPiece;
	}

	public WhitePlayer getWhitePlayer() {
		return whitePlayer;
	}

	public BlackPlayer getBlackPlayer() {
		return blackPlayer;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		int i = 0;
		for (Tile tile : gameBoard) {
			final String tileText = tile.toString();
			builder.append(String.format("%3s", tileText));
			if (++i % 8 == 0) {
				builder.append("\n");
			}
		}
		return builder.toString();
	}

	/**
	 * calculate legal move
	 * for each piece in pieces
	 * add all legal moves of each piece
	 * @param pieces list of alive piece
	 * @return list legal moves of all piece
	 */
	private List<Move> calculateLegalMove(List<Piece> pieces) {
		final List<Move> legalMoves = new ArrayList<>();
		for (final Piece piece : pieces) {
			legalMoves.addAll(piece.calculateLegalMoves(this));
		}
		return ImmutableList.copyOf(legalMoves);
	}

	/**
	 * calculate active piece
	 * @param gameBoard current game board
	 * @param alliance white or black
	 * @return list of active piece
	 */
	private static List<Piece> calculateActivePiece(final List<Tile> gameBoard, final Alliance alliance) {
		final List<Piece> activePieces = new ArrayList<>();
		for (final Tile tile : gameBoard) {
			if (tile.isTileOccupied()) {
				final Piece piece = tile.getPiece();
				if (piece.getPieceAlliance() == alliance) {
					activePieces.add(piece);
				}
			}
		}
		return ImmutableList.copyOf(activePieces);
	}

	/**
	 * get tile [x, y]
	 * @param positionX x
	 * @param positionY y
	 * @return tile has coordinate [x, y]
	 */
	public Tile getTile(final int positionX, final int positionY) {
		return gameBoard.get(positionX + positionY * 8);
	}

	/**
	 * draw a game board
	 * @param builder builder
	 * @return list of tiles
	 */
	private static List<Tile> createGameBoard(Builder builder) {
		final Tile[] tiles = new Tile[64];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				tiles[i + 8 * j] = Tile.createTile(i, j, builder.boardConfig.get(i + 8 * j));
			}
		}
		return ImmutableList.copyOf(tiles);
	}

	/**
	 * r n b q k b n r
	 * p p p p p p p p
	 * - - - - - - - -
	 * - - - - - - - -
	 * - - - - - - - -
	 * - - - - - - - -
	 * P P P P P P P P
	 * R N B Q K B N R
	 * @return new board
	 */
	public static Board createStandardBoard() {
		final Builder builder = new Builder();
		builder.setPiece(new Rook(0, 0, Alliance.BLACK, true));
		builder.setPiece(new Knight(1, 0, Alliance.BLACK, true));
		builder.setPiece(new Bishop(2, 0, Alliance.BLACK, true));
		builder.setPiece(new Queen(3, 0, Alliance.BLACK, true));
		builder.setPiece(new King(4, 0, Alliance.BLACK, true, false, true, true));
		builder.setPiece(new Bishop(5, 0, Alliance.BLACK, true));
		builder.setPiece(new Knight(6, 0, Alliance.BLACK, true));
		builder.setPiece(new Rook(7, 0, Alliance.BLACK, true));
		builder.setPiece(new Pawn(0, 1, Alliance.BLACK, true));
		builder.setPiece(new Pawn(1, 1, Alliance.BLACK, true));
		builder.setPiece(new Pawn(2, 1, Alliance.BLACK, true));
		builder.setPiece(new Pawn(3, 1, Alliance.BLACK, true));
		builder.setPiece(new Pawn(4, 1, Alliance.BLACK, true));
		builder.setPiece(new Pawn(5, 1, Alliance.BLACK, true));
		builder.setPiece(new Pawn(6, 1, Alliance.BLACK, true));
		builder.setPiece(new Pawn(7, 1, Alliance.BLACK, true));

		builder.setPiece(new Rook(0, 7, Alliance.WHITE, true));
		builder.setPiece(new Knight(1, 7, Alliance.WHITE, true));
		builder.setPiece(new Bishop(2, 7, Alliance.WHITE, true));
		builder.setPiece(new Queen(3, 7, Alliance.WHITE, true));
		builder.setPiece(new King(4, 7, Alliance.WHITE, true, false, true, true));
		builder.setPiece(new Bishop(5, 7, Alliance.WHITE, true));
		builder.setPiece(new Knight(6, 7, Alliance.WHITE, true));
		builder.setPiece(new Rook(7, 7, Alliance.WHITE, true));
		builder.setPiece(new Pawn(0, 6, Alliance.WHITE, true));
		builder.setPiece(new Pawn(1, 6, Alliance.WHITE, true));
		builder.setPiece(new Pawn(2, 6, Alliance.WHITE, true));
		builder.setPiece(new Pawn(3, 6, Alliance.WHITE, true));
		builder.setPiece(new Pawn(4, 6, Alliance.WHITE, true));
		builder.setPiece(new Pawn(5, 6, Alliance.WHITE, true));
		builder.setPiece(new Pawn(6, 6, Alliance.WHITE, true));
		builder.setPiece(new Pawn(7, 6, Alliance.WHITE, true));

		builder.setNextMoveMaker(Alliance.WHITE);
		return builder.build();
	}

	/**
	 * get all white and black pieces
	 * @return list of all pieces
	 */
	Iterable<Piece> getAllPieces() {
		return Iterables.unmodifiableIterable(Iterables.concat(whitePiece, blackPiece));
	}

	/**
	 * get all white and black legal moves
	 * @return list of all legal moves
	 */
	public Iterable<Move> getAllLegalMove() {
		return Iterables.unmodifiableIterable(Iterables.concat(whitePlayer.getLegalMoves(), blackPlayer.getLegalMoves()));
	}

	public static class Builder {
		private final Map<Integer, Piece> boardConfig;
		private Alliance nextMoveMaker;
		private Pawn enPassantPawn;

		public Builder() {
			this.boardConfig = new HashMap<>();
		}

		public void setPiece(Piece piece) {
			this.boardConfig.put(piece.getPiecePositionX() + 8 * piece.getPiecePositionY(), piece);
		}

		public void setNextMoveMaker(Alliance nextMoveMaker) {
			this.nextMoveMaker = nextMoveMaker;
		}

		public Board build() {
			return new Board(this);
		}

		public void setEnPassantPawn(Pawn enPassantPawn) {
			this.enPassantPawn = enPassantPawn;
		}
	}
}

package chess.engine.board;

import chess.engine.piece.Piece;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public abstract class Tile {

	protected final int positionX, positionY;
	private static final Map<Integer, EmptyTile> EMPTY_TILE_CACHE = createAllPossibleEmptyTile();

	private static Map<Integer, EmptyTile> createAllPossibleEmptyTile() {
		final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();

		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				emptyTileMap.put(i + 8 * j, new EmptyTile(i, j));
			}
		}
		return ImmutableMap.copyOf(emptyTileMap);
	}

	public static Tile createTile(final int positionX, final int positionY, final Piece piece) {
		return piece != null ? new OccupiedTile(positionX, positionY, piece) : EMPTY_TILE_CACHE.get(positionX + positionY * 8);
	}

	private Tile(int positionX, int positionY) {
		this.positionX = positionX;
		this.positionY = positionY;
	}

	@Override
	public abstract String toString();

	public abstract boolean isTileOccupied();

	public abstract Piece getPiece();

	public int getPositionX() {
		return positionX;
	}

	public int getPositionY() {
		return positionY;
	}

	public static final class EmptyTile extends Tile {
		public EmptyTile(int positionX, int positionY) {
			super(positionX, positionY);
		}

		@Override
		public boolean isTileOccupied() {
			return false;
		}

		@Override
		public Piece getPiece() {
			return null;
		}

		@Override
		public String toString() {
			return "-";
		}
	}

	public static final class OccupiedTile extends Tile {
		private final Piece pieceOnTile;

		public OccupiedTile(int positionX, int positionY, Piece pieceOnTile) {
			super(positionX, positionY);
			this.pieceOnTile = pieceOnTile;
		}

		@Override
		public boolean isTileOccupied() {
			return true;
		}

		@Override
		public Piece getPiece() {
			return pieceOnTile;
		}

		@Override
		public String toString() {
			return getPiece().getPieceAlliance().isWhite() ? getPiece().toString() : getPiece().toString().toLowerCase();
		}
	}
}

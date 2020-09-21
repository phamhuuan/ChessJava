package chess.gui;

import chess.engine.board.Board;
import chess.engine.board.Move;
import chess.engine.board.Move.MoveFactory;
import chess.engine.board.Tile;
import chess.engine.piece.Piece;
import chess.engine.player.MoveTransition;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static javax.swing.SwingUtilities.*;

public class Table {
	private final JFrame gameFrame;
	private final GameHistoryPanel gameHistoryPanel;
	private final TakenPiecesPanel takenPiecesPanel;
	private final BoardPanel boardPanel;
	private Board chessBoard;
	private Tile sourceTile, destinationTile;
	private Piece humanMovedPiece;
	private BoardDirection boardDirection;
	private boolean highlightLegalMoves;
	private final MoveLog moveLog;

	private static final Dimension GAME_FRAME = new Dimension(800, 600);
	private static final Dimension BOARD_FRAME = new Dimension(600, 600);
	private static final Dimension TILE_FRAME = new Dimension(10, 10);
	private static final Table INSTANCE = new Table();

	public Table() {
		gameFrame = new JFrame("Chess");
		gameFrame.setLayout(new BorderLayout());
		final JMenuBar tableMenuBar = createTableMenuBar();
		gameFrame.setJMenuBar(tableMenuBar);
		gameFrame.setSize(GAME_FRAME);
		chessBoard = Board.createStandardBoard();
		gameHistoryPanel = new GameHistoryPanel();
		takenPiecesPanel = new TakenPiecesPanel();
		boardPanel = new BoardPanel();
		moveLog = new MoveLog();
		boardDirection = BoardDirection.NORMAL;
		highlightLegalMoves = true;
		gameFrame.add(takenPiecesPanel, BorderLayout.WEST);
		gameFrame.add(boardPanel, BorderLayout.CENTER);
		gameFrame.add(gameHistoryPanel, BorderLayout.EAST);
		gameFrame.setVisible(true);
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.setResizable(false);
	}

	public JFrame getGameFrame() {
		return gameFrame;
	}

	public static Dimension getBoardFrame() {
		return BOARD_FRAME;
	}

	public static Dimension getTileFrame() {
		return TILE_FRAME;
	}

	public static Table getINSTANCE() {
		return INSTANCE;
	}

	public GameHistoryPanel getGameHistoryPanel() {
		return gameHistoryPanel;
	}

	public TakenPiecesPanel getTakenPiecesPanel() {
		return takenPiecesPanel;
	}

	public BoardPanel getBoardPanel() {
		return boardPanel;
	}

	public Board getChessBoard() {
		return chessBoard;
	}

	public Tile getSourceTile() {
		return sourceTile;
	}

	public Tile getDestinationTile() {
		return destinationTile;
	}

	public Piece getHumanMovedPiece() {
		return humanMovedPiece;
	}

	public BoardDirection getBoardDirection() {
		return boardDirection;
	}

	public boolean isHighlightLegalMoves() {
		return highlightLegalMoves;
	}

	public MoveLog getMoveLog() {
		return moveLog;
	}

	public static Table get() {
		return INSTANCE;
	}

	private JMenuBar createTableMenuBar() {
		final JMenuBar tableMenuBar = new JMenuBar();
		tableMenuBar.add(createOptionMenu());
		tableMenuBar.add(createPreferencesMenu());
		return tableMenuBar;
	}

	private JMenu createOptionMenu() {
		final JMenu optionsMenu = new JMenu("Menu");
		optionsMenu.setMnemonic(KeyEvent.VK_O);
		final JMenuItem newGame = new JMenuItem("New game");
		newGame.addActionListener(actionEvent -> undoAllMoves());
		optionsMenu.add(newGame);
		final JMenuItem undoMove = new JMenuItem("Undo last move");
		undoMove.addActionListener(actionEvent -> {
			if(Table.get().getMoveLog().size() > 0) {
				undoLastMove();
			}
		});
		optionsMenu.add(undoMove);
		final JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(actionEvent -> System.exit(0));
		optionsMenu.add(exit);
		return optionsMenu;
	}

	private JMenu createPreferencesMenu() {
		final JMenu preferencesMenu = new JMenu("Preferences");
		final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
		flipBoardMenuItem.addActionListener(actionEvent -> {
			boardDirection = boardDirection.opposite();
			boardPanel.drawBoard(chessBoard);
		});
		preferencesMenu.add(flipBoardMenuItem);
		preferencesMenu.addSeparator();
		final JCheckBoxMenuItem legalMoveHighlighterCheckbox = new JCheckBoxMenuItem("Highlight legal moves", true);
		legalMoveHighlighterCheckbox.addActionListener(actionEvent -> highlightLegalMoves = legalMoveHighlighterCheckbox.isSelected());
		preferencesMenu.add(legalMoveHighlighterCheckbox);
		return preferencesMenu;
	}

	public void show() {
		Table.get().getMoveLog().clear();
		Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
		Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
		Table.get().getBoardPanel().drawBoard(Table.get().getChessBoard());
	}

	private void undoLastMove() {
		final Move lastMove = Table.get().getMoveLog().removeMove(Table.get().getMoveLog().size() - 1);
		this.chessBoard = this.chessBoard.getCurrentPlayer().unmakeMove(lastMove).getToBoard();
		Table.get().getMoveLog().removeMove(lastMove);
		Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
		Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
		Table.get().getBoardPanel().drawBoard(chessBoard);
	}

	private void undoAllMoves() {
		for(int i = Table.get().getMoveLog().size() - 1; i >= 0; i--) {
			final Move lastMove = Table.get().getMoveLog().removeMove(Table.get().getMoveLog().size() - 1);
			this.chessBoard = this.chessBoard.getCurrentPlayer().unmakeMove(lastMove).getToBoard();
		}
		Table.get().getMoveLog().clear();
		System.out.println(Table.get().getMoveLog().size());
		Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
		Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
		Table.get().getBoardPanel().drawBoard(chessBoard);
	}

	private boolean getHighlightLegalMoves() {
		return this.highlightLegalMoves;
	}

	private class BoardPanel extends JPanel {
		private final List<TilePanel> boardTiles;

		public BoardPanel() {
			super(new GridLayout(8, 8));
			boardTiles = new ArrayList<>();
			for(int i = 0; i < 8; i++) {
				for(int j = 0; j < 8; j++) {
					final TilePanel tilePanel = new TilePanel(this, j, i);
					boardTiles.add(tilePanel);
					add(tilePanel);
				}
			}
			setPreferredSize(BOARD_FRAME);
			validate();
		}

		public void drawBoard(Board chessBoard) {
			removeAll();
			for(final TilePanel tilePanel : boardDirection.traverse(boardTiles)) {
				tilePanel.drawTile(chessBoard);
				add(tilePanel);
			}
			validate();
			repaint();
		}
	}

	public static class MoveLog {
		private final List<Move> moves;

		public MoveLog() {
			moves = new ArrayList<>();
		}

		public List<Move> getMoves() {
			return moves;
		}

		public void addMove(final Move move) {
			moves.add(move);
		}

		public int size() {
			return moves.size();
		}

		public void clear() {
			moves.clear();
		}

		public Move removeMove(final int index) {
			return moves.remove(index);
		}

		public void removeMove(final Move move) {
			moves.remove(move);
		}
	}

	private class TilePanel extends JPanel {
		private final int positionX, positionY;

		public TilePanel(BoardPanel boardPanel, int positionX, int positionY) {
			super(new GridBagLayout());
			this.positionX = positionX;
			this.positionY = positionY;
			setPreferredSize(TILE_FRAME);
			assignTileColor();
			assignTilePieceIcon(chessBoard);
			addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent mouseEvent) {
					if(isRightMouseButton(mouseEvent)) {
						sourceTile = null;
						destinationTile = null;
						humanMovedPiece = null;
					} else if (isLeftMouseButton(mouseEvent)) {
						if(sourceTile == null) {
							sourceTile = chessBoard.getTile(positionX, positionY);
							humanMovedPiece = sourceTile.getPiece();
							if(humanMovedPiece == null) {
								sourceTile = null;
							}
						} else {
							destinationTile = chessBoard.getTile(positionX, positionY);
							final Move move = MoveFactory.createMove(chessBoard, sourceTile.getPositionX(), sourceTile.getPositionY(), destinationTile.getPositionX(), destinationTile.getPositionY());
							final MoveTransition transition = chessBoard.getCurrentPlayer().makeMove(move);
							if(transition.getMoveStatus().isDone()) {
								chessBoard = transition.getToBoard();
								moveLog.addMove(move);
							}
							sourceTile = null;
							destinationTile = null;
							humanMovedPiece = null;
						}
						invokeLater(() -> {
							gameHistoryPanel.redo(chessBoard, moveLog);
							takenPiecesPanel.redo(moveLog);
							boardPanel.drawBoard(chessBoard);
							if (chessBoard.getCurrentPlayer().isInCheckmate() || chessBoard.getCurrentPlayer().isInStaleMate()) {
								JOptionPane.showMessageDialog(null, chessBoard.getCurrentPlayer().getOpponent().getAlliance() + " win!");
								undoAllMoves();
							}
						});
					}
				}

				@Override
				public void mousePressed(MouseEvent mouseEvent) {

				}

				@Override
				public void mouseReleased(MouseEvent mouseEvent) {

				}

				@Override
				public void mouseEntered(MouseEvent mouseEvent) {

				}

				@Override
				public void mouseExited(MouseEvent mouseEvent) {

				}
			});
			validate();
		}

		private void highlightLegals(final Board board) {
			if(highlightLegalMoves) {
				for(final Move move : pieceLegalMove(board)) {
					if(move.getDestinationPositionX() == positionX && move.getDestinationPositionY() == positionY) {
						try {
							add(new JLabel(new ImageIcon(ImageIO.read(new File("art/misc/green_dot.png")))));
						} catch (IOException exception) {
							exception.printStackTrace();
						}
					}
				}
			}
		}

		private List<Move> pieceLegalMove(Board board) {
			if(humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.getCurrentPlayer().getAlliance()) {
				return humanMovedPiece.calculateLegalMoves(board);
			}
			return Collections.emptyList();
		}

		private void assignTilePieceIcon(Board board) {
			removeAll();
			if(board.getTile(positionX, positionY).isTileOccupied()) {
				try {
					final BufferedImage image = ImageIO.read(new File("art/simple/" + board.getTile(positionX, positionY).getPiece().getPieceAlliance().toString().substring(0, 1) + board.getTile(positionX, positionY).getPiece().toString() + ".gif"));
					add(new JLabel(new ImageIcon(image)));
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}
		}

		private void assignTileColor() {
			setBackground((positionX + positionY) % 2 == 0 ? new Color(255, 206, 158) : new Color(209, 139, 71));
		}

		public void drawTile(Board chessBoard) {
			assignTileColor();
			assignTilePieceIcon(chessBoard);
			highlightLegals(chessBoard);
			validate();
			repaint();
		}
	}

	public enum BoardDirection {
		NORMAL {
			@Override
			public List<TilePanel> traverse(List<TilePanel> boardTiles) {
				return boardTiles;
			}

			@Override
			public BoardDirection opposite() {
				return FLIPPED;
			}
		},
		FLIPPED {
			@Override
			public List<TilePanel> traverse(List<TilePanel> boardTiles) {
				return Lists.reverse(boardTiles);
			}

			@Override
			public BoardDirection opposite() {
				return NORMAL;
			}
		};
		public abstract List<TilePanel> traverse(final  List<TilePanel> boardTiles);
		public abstract BoardDirection opposite();
	}
}

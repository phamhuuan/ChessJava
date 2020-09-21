package chess.gui;

import chess.engine.board.Board;
import chess.engine.board.Move;
import chess.gui.Table.MoveLog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameHistoryPanel extends JPanel {
	private final DataModel model;
	private final JScrollPane scrollPane;

	public GameHistoryPanel() {
		setLayout(new BorderLayout());
		model = new DataModel();
		final JTable table = new JTable(model);
		table.setRowHeight(15);
		scrollPane = new JScrollPane(table);
		scrollPane.setColumnHeaderView(table.getTableHeader());
		scrollPane.setPreferredSize(new Dimension(150, 40));
		add(scrollPane, BorderLayout.CENTER);
		setVisible(true);
	}

	public void redo(final Board board, final MoveLog moveLog) {
		int currentRow = 0;
		model.clear();
		for (final Move move : moveLog.getMoves()) {
			final String moveText = move.toString();
			if (move.getMovedPiece().getPieceAlliance().isWhite()) {
				model.setValueAt(moveText, currentRow, 0);
			} else if (move.getMovedPiece().getPieceAlliance().isBlack()) {
				model.setValueAt(moveText, currentRow, 1);
				currentRow++;
			}
		}
		if (moveLog.getMoves().size() > 0) {
			final Move lastMove = moveLog.getMoves().get(moveLog.size() - 1);
			final String moveText = lastMove.toString();
			if (lastMove.getMovedPiece().getPieceAlliance().isWhite()) {
				model.setValueAt(moveText + calculateCheckAndCheckmateHash(board), currentRow, 0);
			} else if (lastMove.getMovedPiece().getPieceAlliance().isBlack()) {
				model.setValueAt(moveText + calculateCheckAndCheckmateHash(board), currentRow - 1, 1);
			}
		} else {
			model.setValueAt("", 0, 0);
		}
		final JScrollBar vertical = scrollPane.getVerticalScrollBar();
		vertical.setValue(vertical.getMaximum());
	}

	private String calculateCheckAndCheckmateHash(final Board board) {
		if (board.getCurrentPlayer().isInCheckmate()) {
			return "#";
		} else if (board.getCurrentPlayer().isInCheck()) {
			return "+";
		}
		return "";
	}

	private static class DataModel extends DefaultTableModel {
		private final List<Row> values;
		private final static String[] names = {"White", "Black"};

		public DataModel() {
			values = new ArrayList<>();
		}

		public void clear() {
			values.clear();
			setRowCount(0);
		}

		@Override
		public int getRowCount() {
			return values == null ? 0 : values.size();
		}

		@Override
		public int getColumnCount() {
			return names.length;
		}

		@Override
		public Object getValueAt(int row, int column) {
			final Row currentRow = values.get(row);
			return column == 0 ? currentRow.getWhiteMove() : column == 1 ? currentRow.getBlackMove() : null;
		}

		@Override
		public void setValueAt(Object valueAt, int row, int column) {
			final Row currentRow;
			if (values.size() <= row) {
				currentRow = new Row();
				values.add(currentRow);
			} else {
				currentRow = values.get(row);
			}
			if (column == 0) {
				currentRow.setWhiteMove((String) valueAt);
				fireTableRowsInserted(row, row);
			} else if (column == 1) {
				currentRow.setBlackMove((String) valueAt);
				fireTableCellUpdated(row, column);
			}
		}

		@Override
		public Class<?> getColumnClass(final int column) {
			return Move.class;
		}

		@Override
		public String getColumnName(final int column) {
			return names[column];
		}
	}

	private static class Row {
		private String whiteMove, blackMove;

		public Row() {
		}

		public String getWhiteMove() {
			return whiteMove;
		}

		public void setWhiteMove(String whiteMove) {
			this.whiteMove = whiteMove;
		}

		public String getBlackMove() {
			return blackMove;
		}

		public void setBlackMove(String blackMove) {
			this.blackMove = blackMove;
		}
	}
}

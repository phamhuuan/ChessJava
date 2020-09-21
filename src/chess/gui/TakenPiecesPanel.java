package chess.gui;

import chess.engine.board.Move;
import chess.engine.piece.Piece;
import chess.gui.Table.MoveLog;
import com.google.common.primitives.Ints;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TakenPiecesPanel extends JPanel {
	private final JPanel northPanel, southPanel;
	private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);

	public TakenPiecesPanel() {
		super(new BorderLayout());
		setBackground(new Color(0xf0f0f0));
		setBorder(PANEL_BORDER);
		northPanel = new JPanel(new GridLayout(16, 1));
		southPanel = new JPanel(new GridLayout(16, 1));
		northPanel.setBackground(new Color(0xf0f0f0));
		southPanel.setBackground(new Color(0xf0f0f0));
		add(northPanel, BorderLayout.NORTH);
		add(southPanel, BorderLayout.SOUTH);
		setPreferredSize(new Dimension(40, 80));
	}

	public void redo(final MoveLog moveLog) {
		southPanel.removeAll();
		northPanel.removeAll();

		final List<Piece> whiteTakenPieces = new ArrayList<>();
		final List<Piece> blackTakenPieces = new ArrayList<>();

		for(final Move move : moveLog.getMoves()) {
			if(move.isAttack()) {
				final Piece takenPiece = move.getAttackedPiece();
				if(takenPiece.getPieceAlliance().isWhite()) {
					whiteTakenPieces.add(takenPiece);
				} else if(takenPiece.getPieceAlliance().isBlack()) {
					blackTakenPieces.add(takenPiece);
				} else {
					throw new RuntimeException("Should not reach here");
				}
			}
		}
		whiteTakenPieces.sort((piece1, piece2) -> Ints.compare(piece1.getPieceValue(), piece2.getPieceValue()));
		blackTakenPieces.sort((piece1, piece2) -> Ints.compare(piece1.getPieceValue(), piece2.getPieceValue()));

		for(final Piece piece : whiteTakenPieces) {
			try {
				final BufferedImage image = ImageIO.read(new File("art/simple/" + piece.getPieceAlliance().toString().substring(0, 1) + piece.toString() + ".gif"));
				final ImageIcon icon = new ImageIcon(image);
				final JLabel imageLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(icon.getIconWidth() - 15, icon.getIconHeight() - 15, Image.SCALE_SMOOTH)));
				northPanel.add(imageLabel);
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}

		for(final Piece piece : blackTakenPieces) {
			try {
				final BufferedImage image = ImageIO.read(new File("art/simple/" + piece.getPieceAlliance().toString().substring(0, 1) + piece.toString() + ".gif"));
				final ImageIcon icon = new ImageIcon(image);
				final JLabel imageLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(icon.getIconWidth() - 15, icon.getIconHeight() - 15, Image.SCALE_SMOOTH)));
				southPanel.add(imageLabel);
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
		validate();
	}
}

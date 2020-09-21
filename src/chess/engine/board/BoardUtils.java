package chess.engine.board;

public class BoardUtils {
	private BoardUtils() {
		throw new RuntimeException("You cannot instantiate me");
	}

	public static String positionToCoordinate(final int row, final int column) {
		StringBuilder stringBuilder = new StringBuilder();
		switch (column) {
			case 0:
				stringBuilder.append("a");
				break;
			case 1:
				stringBuilder.append("b");
				break;
			case 2:
				stringBuilder.append("c");
				break;
			case 3:
				stringBuilder.append("d");
				break;
			case 4:
				stringBuilder.append("e");
				break;
			case 5:
				stringBuilder.append("f");
				break;
			case 6:
				stringBuilder.append("g");
				break;
			case 7:
				stringBuilder.append("h");
				break;
		}
		stringBuilder.append(":");
		switch (row) {
			case 0:
				stringBuilder.append("8");
				break;
			case 1:
				stringBuilder.append("7");
				break;
			case 2:
				stringBuilder.append("6");
				break;
			case 3:
				stringBuilder.append("5");
				break;
			case 4:
				stringBuilder.append("4");
				break;
			case 5:
				stringBuilder.append("3");
				break;
			case 6:
				stringBuilder.append("2");
				break;
			case 7:
				stringBuilder.append("1");
				break;
		}
		return stringBuilder.toString();
	}

	public static boolean isValidTileCoordinate(int positionX, int positionY) {
		return positionX >= 0 && positionX <= 7 && positionY >= 0 && positionY <= 7;
	}
}

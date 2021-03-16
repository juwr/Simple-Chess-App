package game;

public class Tower extends Piece {

	

	public Tower(Board board, Player owner, Cell position) {
		super(board, owner, position);
	}

	@Override
	public boolean checkMove(Cell cell) {
		
		boolean valid = true;

		if (cell.x == position.x) {
			if (Math.abs(cell.y - position.y) > 1) {
				if (cell.y > position.y) {
					for (int i = position.y + 1; i < cell.y; i++) {
						if (this.board.getCell(position.x, i).occupied) {
							valid = false;
						}
					}
				} else {
					for (int i = position.y - 1; i > cell.y; i--) {
						if (this.board.getCell(position.x, i).occupied) {
							valid = false;
						}
					}
				}
			}
		} else if (cell.y == position.y) {
			if (Math.abs(cell.x - position.x) > 1) {
				if (cell.x > position.x) {
					for (int i = position.x + 1; i < cell.x; i++) {
						if (this.board.getCell(i, position.y).occupied) {
							valid = false;
						}
					}
				} else {
					for (int i = position.x - 1; i > cell.x; i--) {
						if (this.board.getCell(i, position.y).occupied) {
							valid = false;
						}
					}
				}
			}
		} else {
			valid = false;
		}

		if (cell.occupied) {
			if (cell.piece.owner.white == owner.white) {
				valid = false;
			}
		}
		
		if (valid) {
			if (cell.occupied) {
				if (cell.piece instanceof King || cell.piece.owner.white == this.owner.white) {
					return valid;
				}
			}
			valid = this.checkIfCheckAfterMove(cell);
			return valid;
		} else {
			return valid;
		}
	}

}

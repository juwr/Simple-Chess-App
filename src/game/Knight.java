package game;

public class Knight extends Piece {

	

	public Knight(Board board, Player owner, Cell position) {
		super(board, owner, position);
	}

	@Override
	public boolean checkMove(Cell cell) {
		
		boolean valid = false;
		if ((cell.x == position.x+1 || cell.x == position.x-1) && (cell.y == position.y+2 || cell.y == position.y-2)) {
			valid = true;
		} else if ((cell.y == position.y+1 || cell.y == position.y-1) && (cell.x == position.x+2 || cell.x == position.x-2)) {
			valid = true;
		}
		if (valid) {
			if (cell.occupied) {
				if (cell.piece.owner.white == owner.white) {
					valid = false;
				}
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

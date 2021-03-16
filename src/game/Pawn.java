package game;

public class Pawn extends Piece {
	
	

	public Pawn(Board board, Player owner, Cell position) {
		super(board, owner, position);
	}

	@Override
	public boolean checkMove(Cell cell) {
		
		boolean valid = false;
		
		int updown;
		if (owner.white) {
			updown = -1;
		} else {
			updown = 1;
		}
		
		
		if (movecount == 0) {
			if (cell.y == (position.y+(2*updown)) && cell.x == position.x) {
				
				if (this.board.getCell(this.position.x, this.position.y+updown).occupied) {
					return false;
				} else {
					if (cell.occupied) {
						valid = false;
					} else {
						valid = true;
					}
				}
				
				
			}
		}
		
		if (cell.y == (position.y+updown) && cell.x == position.x) {
			if (cell.occupied) {
				valid = false;
			} else {
				valid = true;
			}
		}
		
		if (cell.y == (position.y+updown) && (cell.x == (position.x+1) || cell.x == (position.x-1))) {
			if (cell.occupied) {
				if (cell.piece.owner.white == owner.white) {
					valid = false;
				} else {
					valid = true;
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

package game;

public class King extends Piece {

	

	public King(Board board, Player owner, Cell position) {
		super(board, owner, position);
	}

	@Override
	public boolean checkMove(Cell cell) {

		boolean valid = false;
		if ((cell.x == position.x+1 || cell.x == position.x-1 || cell.x == position.x) && (cell.y == position.y+1 || cell.y == position.y-1 || cell.y == position.y)) {
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
			
			if (!this.board.check) {
				valid = this.checkIfCheckAfterMove(cell);
			} else {
				
				Piece storage = cell.piece;
				Cell storagePosition = this.position;
				
				if (!(cell.piece == null)) {
					cell.piece.position = null;
				}
				cell.remove();
				
				cell.occupy(this);
				
				this.position.remove();
				
				this.position = cell;
				
				boolean check = this.board.check;
				
				this.board.check = false;
				if (this.vulnerable()) {
					valid = false;
				}
				this.board.check = check;
				
				cell.remove();
				
				if (storage != null) {
					cell.occupy(storage);
					storage.position = cell;
				}
				
				this.position = storagePosition;
				
				this.position.occupy(this);
			}
			return valid;
		} else {
			
			if (!this.board.check) {
				if (this.owner.white) {
					if (cell.x == 2 && cell.y == 7 && this.board.getCell(0, 7).occupied) {
						if (movecount == 0 && this.board.getCell(0, 7).piece.movecount == 0) {
							if (!this.board.getCell(1, 7).occupied && !this.board.getCell(2, 7).occupied && !this.board.getCell(3, 7).occupied) {
								if (this.checkIfCheckAfterMove(cell) && this.checkIfCheckAfterMove(this.board.getCell(3, 7))) {
									valid = true;
								}
							}
						}
						
						
					} else if (cell.x == 6 && cell.y == 7 && this.board.getCell(7, 7).occupied) {
						if (movecount == 0 && this.board.getCell(7, 7).piece.movecount == 0) {
							if (!this.board.getCell(5, 7).occupied && !this.board.getCell(6, 7).occupied) {
								if (this.checkIfCheckAfterMove(cell) && this.checkIfCheckAfterMove(this.board.getCell(5, 7))) {
									valid = true;
								}
							}
						}
					}
				} else {
					if (cell.x == 2 && cell.y == 0 && this.board.getCell(0, 0).occupied) {
						if (movecount == 0 && this.board.getCell(0, 0).piece.movecount == 0) {
							if (!this.board.getCell(1, 0).occupied && !this.board.getCell(2, 0).occupied && !this.board.getCell(3, 0).occupied) {
								if (this.checkIfCheckAfterMove(cell) && this.checkIfCheckAfterMove(this.board.getCell(3, 0))) {
									valid = true;
								}
							}
						}
						
						
					} else if (cell.x == 6 && cell.y == 0 && this.board.getCell(7, 0).occupied) {
						if (movecount == 0 && this.board.getCell(7, 0).piece.movecount == 0) {
							if (!this.board.getCell(5, 0).occupied && !this.board.getCell(6, 0).occupied) {
								if (this.checkIfCheckAfterMove(cell) && this.checkIfCheckAfterMove(this.board.getCell(5, 0))) {
									valid = true;
								}
							}
						}
					}
				}
			}
			
			
			return valid;
		}
		
	}
	
	@Override
	public void die() {
		this.position.remove();
		
		this.board.endGame(this.owner);
	}

}

package game;

public class Queen extends Piece {

	

	public Queen(Board board, Player owner, Cell position) {
		super(board, owner, position);
	}

	@Override
	public boolean checkMove(Cell cell) {

		boolean valid = false;
		boolean occleft = false;
		boolean occright = false;

		int counter = 1;
		for (int y = position.y + 1; y < Statics.SIZE; y++) {
			// For every y above this piece, check if the target cell is in a diagonal line
			// to the piece.
			if (cell.x == position.x + counter && cell.y == y) {
				if (occright) {
					valid = false;
				} else {
					valid = true;
				}
				break;
			} else if (cell.x == position.x - counter && cell.y == y) {
				if (occleft) {
					valid = false;
				} else {
					valid = true;
				}
				break;
			} else {
				// If any cell between piece and target cell is occupied, target cannot be
				// valid.
				if (!(position.x + counter > Statics.SIZE-1)) {
					if (board.getCell(position.x + counter, y).occupied) {
						occright = true;
					}
				} if (!(position.x - counter < 0)) {
					 if (board.getCell(position.x - counter, y).occupied) {
						occleft = true;
					}
				}
			}

			counter++;
		}

		occleft = false;
		occright = false;

		counter = 1;
		for (int y = position.y - 1; y >= 0; y--) {
			// For every y above this piece, check if the target cell is in a diagonal line
			// to the piece.
			if (cell.x == position.x + counter && cell.y == y) {
				if (occright) {
					valid = false;
				} else {
					valid = true;
				}
				break;
			} else if (cell.x == position.x - counter && cell.y == y) {
				if (occleft) {
					valid = false;
				} else {
					valid = true;
				}
				break;
			} else {
				// If any cell between piece and target cell is occupied, target cannot be
				// valid.
				if (!(position.x + counter > Statics.SIZE-1)) {
					if (board.getCell(position.x + counter, y).occupied) {
						occright = true;
					}
				} if (!(position.x - counter < 0)) {
					 if (board.getCell(position.x - counter, y).occupied) {
						occleft = true;
					}
				}
			}

			counter++;
		}

		if (valid) {
			if (cell.occupied) {
				if (cell.piece.owner.white == owner.white) {
					valid = false;
				}
			}
		} else {
			// If Bishop move is not valid, try Tower move.

			valid = true;
			
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

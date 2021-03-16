package game;

public abstract class Piece {
	
	public Cell position;
	public Board board;
	public Player owner;
	public int movecount;
	
	public Piece (Board board, Player owner, Cell position) {
		this.board = board;
		this.owner = owner;
		this.movecount = 0;
		this.position = position;
	}
	
	public abstract boolean checkMove(Cell cell);
	
	public void move(Cell cell) {
		if (checkMove(cell)) {
			
			if (this instanceof King) {
				if (this.owner.white) {
					if (cell.x == 2 && cell.y == 7 && movecount == 0) {
						Piece tower = this.board.getCell(0, 7).piece;
						Cell towermove = this.board.getCell(3, 7);
						
						this.position.remove();
						cell.occupy(this);
						this.position = cell;
						this.movecount++;
						
						tower.position.remove();
						towermove.occupy(tower);
						tower.position = towermove;
						tower.movecount++;
						return;
					} else if (cell.x == 6 && cell.y == 7 && movecount == 0) {
						Piece tower = this.board.getCell(7, 7).piece;
						Cell towermove = this.board.getCell(5, 7);
						
						this.position.remove();
						cell.occupy(this);
						this.position = cell;
						this.movecount++;
						
						tower.position.remove();
						towermove.occupy(tower);
						tower.position = towermove;
						tower.movecount++;
						return;
					}
				} else {
					if (cell.x == 2 && cell.y == 0 && movecount == 0) {
						Piece tower = this.board.getCell(0, 0).piece;
						Cell towermove = this.board.getCell(3, 0);
						
						this.position.remove();
						cell.occupy(this);
						this.position = cell;
						this.movecount++;
						
						tower.position.remove();
						towermove.occupy(tower);
						tower.position = towermove;
						tower.movecount++;
						return;
					} else if (cell.x == 6 && cell.y == 0 && movecount == 0) {
						Piece tower = this.board.getCell(7, 0).piece;
						Cell towermove = this.board.getCell(5, 0);
						
						this.position.remove();
						cell.occupy(this);
						this.position = cell;
						this.movecount++;
						
						tower.position.remove();
						towermove.occupy(tower);
						tower.position = towermove;
						tower.movecount++;
						return;
					}
				}
			}
			
			if (cell.occupied) {
				if (cell.piece.owner.white != owner.white) {
					this.position.remove();
					
					if (cell.piece instanceof King) {
						this.board.endGame(cell.piece.owner);
					}
					
					cell.piece.die();
					cell.remove();
					
					cell.occupy(this);
					
					this.position = cell;
					
					this.movecount++;
				}
			} else {
				this.position.remove();
				cell.occupy(this);
				this.position = cell;
				this.movecount++;
			}
		}
	}
	
	public void die() {
		this.position.remove();
		this.position = null;
	}
	
	public boolean noPossibleMoves() {
		boolean result = true;
		
		for (int i = 0; i < Statics.SIZE; i++) {
			for (int j = 0; j < Statics.SIZE; j++) {
				if (this.checkMove(this.board.getCell(j, i))) {
					result = false;
				}

			}
		}
		
		return result;
	}
	
	public boolean vulnerable() {
		
		for (int i = 0; i < Statics.SIZE; i++) {
			for (int j = 0; j < Statics.SIZE; j++) {
				if (this.board.getCell(j, i).occupied) {
					if (this.board.getCell(j, i).piece.owner.white != this.owner.white) {
						if (this.board.getCell(j, i).piece.checkMove(this.position)) {
							return true;
						}
					}
				}
			}
		}
		
		return false;
		
	}
	
	public boolean checkIfCheckAfterMove(Cell cell) {
		boolean valid = true;
		Piece storage = cell.piece;
		Cell storagePosition = this.position;
		
		if (!(cell.piece == null)) {
			cell.piece.position = null;
		}
		cell.remove();
		
		cell.occupy(this);
		
		this.position.remove();
		
		this.position = cell;
		
		Piece king = null;
		for (int i = 0; i < Statics.SIZE; i++) {
			for (int j = 0; j < Statics.SIZE; j++) {
				if (this.board.getCell(j, i).occupied) {
					if (this.board.getCell(j, i).piece.owner.white == this.owner.white && this.board.getCell(j, i).piece instanceof King) {
						king = this.board.getCell(j, i).piece;
					}
				}
			}
		}
		
		boolean check = this.board.check;
		
		this.board.check = false;
		
		
		
		if (king.vulnerable()) {
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
		
		return valid;
	}
}

package game;

import main.SCInterface;

public class Board {

	public Player turn;
	public Player player;
	
	private Cell[][] cells;
	public Player p1;
	public Player p2;
	
	public boolean end;
	public boolean check;
	public Player winner;
	
	public SCInterface socket;
	
	public Board() {
		this.p1 = new Player(true);
		this.p2 = new Player(false);
		
		this.end = false;
		this.check = false;
		
		initialize();
	}
	
	
	/**
	 * Creates the Cells of the board and populates it with pieces.
	 */
	private void initialize() {
		this.cells = new Cell[Statics.SIZE][Statics.SIZE];
		
		for (int i = 0; i < Statics.SIZE; i++) {
			for (int j = 0; j < Statics.SIZE; j++) {
				this.cells[i][j] = new Cell(j, i);
				System.out.print("X: " + j + ", Y: " + i + "\t");
			}
			System.out.println();
		}
		
		Player white;
		Player black;
		
		if (p1.white ) {
			white = p1;
			black = p2;
		} else {
			black = p1;
			white = p2;
		}
		
		this.turn = white;
		
		for (int x = 0; x < Statics.SIZE; x++) {
			addPiece(Statics.Type.PAWN, black, getCell(x, 1));
			addPiece(Statics.Type.PAWN, white, getCell(x, 6));
			
			if (x == 0 || x == 7) {
				addPiece(Statics.Type.TOWER, black, getCell(x, 0));
				addPiece(Statics.Type.TOWER, white, getCell(x, 7));
			} else if (x == 1 || x == 6) {
				addPiece(Statics.Type.KNIGHT, black, getCell(x, 0));
				addPiece(Statics.Type.KNIGHT, white, getCell(x, 7));
			} else if (x == 2 || x == 5) {
				addPiece(Statics.Type.BISHOP, black, getCell(x, 0));
				addPiece(Statics.Type.BISHOP, white, getCell(x, 7));
			} else if (x == 3) {
				addPiece(Statics.Type.QUEEN, black, getCell(x, 0));
				addPiece(Statics.Type.QUEEN, white, getCell(x, 7));
			} else if (x == 4) {
				addPiece(Statics.Type.KING, black, getCell(x, 0));
				addPiece(Statics.Type.KING, white, getCell(x, 7));
			}
		}
		
		
		
	}
	
	public Cell getCell(int x, int y) {
		return this.cells[y][x];
	}
	
	public void movePiece(Cell position, Cell target, boolean received) {
		if (!position.occupied) {
			return;
		} else {
			if (position.piece.checkMove(target)) {
				if (this.turn.white) {
					if (this.p1.white) {
						this.turn = p2;
					} else {
						this.turn = p1;
					}
				} else {
					if (this.p1.white) {
						this.turn = p1;
					} else {
						this.turn = p2;
					}
				}
				
				position.piece.move(target);
				this.checkCheck();
				
				if (!received) {
					int checksum = (position.x + position.y + target.x + target.y) % 8;
					
					String message = "MOVE;" + position.x + "," + position.y + ";" + target.x + "," + target.y + ";" + checksum;
					
					this.socket.send(message);
				}
			}
		}
	}
	
	private void addPiece(Statics.Type type, Player player, Cell cell) {

		if (cell.occupied) {
			return;
		} else {

			Piece piece;
			switch (type) {
			case PAWN:
				piece = new Pawn(this, player, cell);
				break;
			case BISHOP:
				piece = new Bishop(this, player, cell);
				break;
			case KING:
				piece = new King(this, player, cell);
				break;
			case KNIGHT:
				piece = new Knight(this, player, cell);
				break;
			case QUEEN:
				piece = new Queen(this, player, cell);
				break;
			case TOWER:
				piece = new Tower(this, player, cell);
				break;
			default:
				return;
			}

			cell.piece = piece;
			cell.occupied = true;

		}

	}
	
	public void endGame(Player loser) {
		this.end = true;
		if (loser.white == this.p1.white) {
			this.winner = p2;
		} else {
			this.winner = p1;
		}
	}
	
	public void endTurn() {
		if (this.turn.white == p1.white) {
			this.turn = p2;
		} else {
			this.turn = p1;
		}
	}
	
	public void checkCheck() {
		Piece king = null;
		for (int i = 0; i < Statics.SIZE; i++) {
			for (int j = 0; j < Statics.SIZE; j++) {
				if (this.getCell(j, i).occupied) {
					if (this.getCell(j, i).piece.owner.white == this.turn.white && this.getCell(j, i).piece instanceof King) {
						king = this.getCell(j, i).piece;
					}
				}
			}
		}
		
		if (king == null) {
			return;
		}
		
		for (int i = 0; i < Statics.SIZE; i++) {
			for (int j = 0; j < Statics.SIZE; j++) {
				Cell cell = this.getCell(j, i);
				
				if (cell.occupied) {
					if (cell.piece.owner.white != this.turn.white) {
						if (cell.piece.checkMove(king.position)) {
							this.check = true;
							
							this.checkCheckmate(king);
							
							return;
						}
					}
				}
			}
		}
		
		this.check = false;
		
	}
	
	private void checkCheckmate(Piece king) {
		boolean noMoves = false;
		if (king instanceof King) {
			if (this.check) {
				noMoves = true;
				for (int i = 0; i < Statics.SIZE; i++) {
					for (int j = 0; j < Statics.SIZE; j++) {
						Cell cell = this.getCell(j, i);
						if (cell.occupied) {
							if (cell.piece.owner.white == king.owner.white) {
								if (!cell.piece.noPossibleMoves()) {
									noMoves = false;
								}
							}
						}
						
					}
				}
					
				
			}
		}
		
		if (noMoves) {
			this.endGame(king.owner);
		}
	}
	
	public String translate(int x, int y) {
		String result = "";
		
		if (x >= Statics.SIZE || y >= Statics.SIZE || x < 0 || y < 0) {
		} else {
			switch (x) {
			case 0: result = "A";
			break;
			case 1: result = "B";
			break;
			case 2: result = "C";
			break;
			case 3: result = "D";
			break;
			case 4: result = "E";
			break;
			case 5: result = "F";
			break;
			case 6: result = "G";
			break;
			case 7: result = "H";
			break;
			}
			
			result = result + Math.abs(Statics.SIZE - y);
		}
		return result;
	}
	
	public Player getOther() {
		if (this.player.white == this.p1.white) {
			return this.p2;
		} else {
			return this.p1;
		}
	}
	
}

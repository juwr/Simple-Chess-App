package game;

public class Cell {
	
	public int x;
	public int y;
	
	public boolean occupied;
	public Piece piece;
	
	Cell(int x, int y) {
		this.x = x;
		this.y = y;
		
		this.occupied = false;
		this.piece = null;
		}
	
	public void occupy(Piece piece) {
		if (this.occupied || piece == null) {
			return;
		}
		
		this.occupied = true;
		this.piece = piece;
	}
	
	public void remove() {
		this.occupied = false;
		this.piece = null;
	}
}

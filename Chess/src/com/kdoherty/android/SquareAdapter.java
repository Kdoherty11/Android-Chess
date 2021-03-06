package com.kdoherty.android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.kdoherty.androidchess.R;
import com.kdoherty.chess.Board;
import com.kdoherty.chess.Move;
import com.kdoherty.chess.Piece;
import com.kdoherty.chess.Square;

/**
 * This is responsible for binding the Board representation to the view of the
 * Board and each of its Pieces.
 * 
 * @author Kevin Doherty
 * 
 */
final class SquareAdapter extends BaseAdapter {

	/** The context which this adapter is called from */
	private ChessActivity chessContext;

	/** The Board that this will represent */
	private Board board;

	/**
	 * Starts with a Board in which all Pieces are in their default positions.
	 * 
	 * @param context
	 *            The context which this adapter is called from
	 */
	public SquareAdapter(ChessActivity context) {
		this(context, Board.defaultBoard());
	}

	/**
	 * Creates a new instance to represent the input Board.
	 * 
	 * @param context
	 *            The context which this adapter is called from
	 * @param board
	 *            The Board to display
	 */
	public SquareAdapter(ChessActivity context, Board board) {
		this.chessContext = context;
		this.board = board;
	}

	@Override
	public int getCount() {
		// Board size stays constant
		return Board.NUM_ROWS * Board.NUM_COLS;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Gets the Board this adapter is representing
	 * 
	 * @return The Board this adapter is representing
	 */
	Board getBoard() {
		return board;
	}

	/**
	 * Gets the Color of the Square based on the Board.
	 * @param row The row of the Square to get the view of
	 * @param col The col of the Square to get the view of
	 * @return The Color of the Square at the input row and column
	 */
	private int getSquareColor(int row, int col) {
		if (chessContext.getActivePieceSquares().contains(new Square(row, col))) {
			// Highlight possible moves
			return R.color.highlight_moves;
		}

		Move lastMove = board.getLastMove();
		if (!chessContext.isCpuMove() && board.getLastMove() != null
				&& lastMove.getStartingRow() == row
				&& lastMove.getStartingCol() == col) {
			// Highlight from square of last move
			return R.color.from_square;
		}
		
		if (!chessContext.isCpuMove() && board.getLastMove() != null
				&& lastMove.getRow() == row && lastMove.getCol() == col) {
			// Highlight to square of last move
			return R.color.to_square;
		}
		
		// Checker the board
		if (!((row % 2 == 0 && col % 2 == 0) || (row % 2 == 1 && col % 2 == 1))) {	
			return R.color.dark_square;
		}
		
		return R.color.light_square;
	}

	/**
	 * Used to hold PieceImageViews so we don't have to inflate the same View
	 * multiple times
	 */
	static class ViewHolder {

		PieceImageView pieceViewItem;

	}

	/**
	 * Displays each Square in the Boards grid view. This is where the Piece
	 * images are set and the Board is checkered.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

		if (convertView == null) {
			LayoutInflater inflater = chessContext.getLayoutInflater();
			convertView = inflater.inflate(R.layout.square, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.pieceViewItem = (PieceImageView) convertView
					.findViewById(R.id.squareView);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		int row = position / 8;
		int col = position % 8;

		convertView.setBackgroundResource(getSquareColor(row, col));

		PieceImageView pieceView = viewHolder.pieceViewItem;
		pieceView.setRow(row);
		pieceView.setCol(col);

		Piece piece = board.getOccupant(row, col);
		if (piece != null) {
			int id = PieceImages.getId(piece);
			pieceView.setImageResource(id);
			pieceView.setId(id);
			pieceView
					.setOnLongClickListener(new OnPieceLongClick(chessContext));
			pieceView.setOnClickListener(new OnPieceClick(chessContext, board,
					row, col));
		}

		convertView.setOnClickListener(new OnPieceClick(chessContext, board,
				row, col));
		convertView.setOnDragListener(new OnPieceDrag(chessContext, board, row,
				col));

		return convertView;
	}
}

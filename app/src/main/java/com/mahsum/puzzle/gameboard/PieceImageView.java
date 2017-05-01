package com.mahsum.puzzle.gameboard;

import android.content.ClipData;
import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import com.jakewharton.rxbinding2.view.RxView;
import com.mahsum.puzzle.core.GameBoard;
import com.mahsum.puzzle.core.Piece;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class PieceImageView extends AppCompatImageView{
  private Piece piece;

  public PieceImageView(Context context) {
    super(context);
  }

  public void configure(Piece piece, int piecesX, int piecesY, int index){
    this.piece = piece;

    setId(AppCompatImageView.generateViewId());
    setX(piece, piecesX, index);
    setY(piece, piecesY, index);
    setImageBitmap(piece.getBitmap());

    setUpTouchStream();
    setUpDragAndDropStream();
  }

  private void setUpDragAndDropStream() {
    Observable<DragEvent> dragEventStream = RxView.drags(this);
    dragEventStream.subscribe(new Consumer<DragEvent>() {
      @Override
      public void accept(@NonNull DragEvent event) throws Exception {
        final int action = event.getAction();

        // Handles each of the expected events
        if (action == DragEvent.ACTION_DROP) {
          int sourcePieceId = (int) event.getLocalState();
          GameBoard.swapPieces(sourcePieceId, piece.getId());
          // Returns true. DragEvent.getResult() will return true.
        }
      }
    });
  }

  private void setUpTouchStream() {
    Observable<MotionEvent> touchStream = RxView.touches(this);
    Consumer shadowDrawer = new Consumer() {
      @Override
      public void accept(@NonNull Object o) throws Exception {
        startDrag(null, new AppCompatImageView.DragShadowBuilder(PieceImageView.this), piece.getId(), 0);
      }
    };
    touchStream.subscribe(shadowDrawer);
  }

  private void setY(Piece piece, int piecesY, int index){
    int row = index / piecesY;
    int viewY = row * piece.getBitmap().getHeight();
    int yPadding = 2;
    if (row != 0){
      viewY = viewY - (2 * row ) * piece.getAdditionSizeY() + yPadding + 2 * row * yPadding;
    }
    setY(viewY);
  }

  private void setX(Piece piece, int piecesX, int index) {
    int column = index % piecesX;
    int viewX = column * piece.getBitmap().getWidth();
    int xPadding = 2;
    if (column != 0){
      viewX = viewX - (2 * column) * piece.getAdditionSizeX() + 2 * column * xPadding;
    }
    setX(viewX);
  }

  public void scale(double scaleFactor) {
    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
    params.height = (int) (getHeight() * scaleFactor);
    params.width = (int) (getWidth() * scaleFactor);
    setX((float) (getX() * scaleFactor));
    setY((float) (getY() * scaleFactor));
  }

  public void setPiece(Piece piece){
    this.piece = piece;
    setImageBitmap(piece.getBitmap());
  }

  public Piece getPiece() {
    return piece;
  }
}

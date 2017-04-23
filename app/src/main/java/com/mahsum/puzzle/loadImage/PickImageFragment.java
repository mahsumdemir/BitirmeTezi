package com.mahsum.puzzle.loadImage;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import com.mahsum.puzzle.R;
import com.mahsum.puzzle.ui.PuzzlePropertiesDialog;
import com.yalantis.ucrop.UCrop;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import java.io.File;

public class PickImageFragment extends Fragment implements Contract.View{
  private Contract.Presenter presenter;
  private ImageView imageView;
  public PickImageFragment() {
    // Required empty public constructor
  }

  public static PickImageFragment newInstance() {
    PickImageFragment pickImageFragment = new PickImageFragment();
    pickImageFragment.presenter = new MainActivityPresenter(pickImageFragment);
    return pickImageFragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_pick_image, container, false);
    imageView = (ImageView) view.findViewById(R.id.imageView);
    Button button = (Button) view.findViewById(R.id.button);
    button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        presenter.startImageChoosing();
      }
    });
    return view;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (data == null) return;
    final Observable<Intent> imageUri = Observable.just(data);

    imageUri.subscribe(new Consumer<Intent>() {
      @RequiresApi(api = VERSION_CODES.M)
      @Override
      public void accept(@NonNull Intent intent) throws Exception {
        UCrop.of(intent.getData(), Uri.fromFile(new File(getActivity().getCacheDir(), "image")))
            .withAspectRatio(1, 1)
            .start(getActivity());  //handle result at PickImageActivity
      }
    });
  }

  @Override
  public void showImage(Bitmap image) {
    imageView.setImageBitmap(image);
  }

  @Override
  public ContentResolver getContentResolver() {
    return getActivity().getContentResolver();
  }
}

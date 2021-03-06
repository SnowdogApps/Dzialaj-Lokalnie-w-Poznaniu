package pl.snowdog.dzialajlokalnie.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import de.greenrobot.event.EventBus;
import pl.snowdog.dzialajlokalnie.AddUserActivity;
import pl.snowdog.dzialajlokalnie.R;
import pl.snowdog.dzialajlokalnie.api.DlApi;
import pl.snowdog.dzialajlokalnie.events.CreateNewObjectEvent;
import pl.snowdog.dzialajlokalnie.helpers.BitmapHeplers;
import pl.snowdog.dzialajlokalnie.util.FileChooserUtil;

/**
 * Created by chomi3 on 2015-07-06.
 */
@EFragment(R.layout.fragment_add_image)
public class AddImageFragment extends AddBaseFragment {
    public static final int PICK_FROM_FILE = 1231;
    public static final int TAKE_PICTURE = 1234;
    private static final String TAG = "AddImageFr";

    private String mTmpGalleryPicturePath;

    @ViewById(R.id.ivPreview)
    ImageView ivPreview;

    @ViewById(R.id.fab)
    FloatingActionsMenu fab;

    @FragmentArg
    int mMode;

    private Uri mFileUri;

    private int photoFrom = 0;

    @FragmentArg
    CreateNewObjectEvent mEditedObject;

    @Click(R.id.btnNext)
    void onNextButtonClicked() {
        if (validateInput()) {
            String uriToSend = "";
            switch (photoFrom) {
                case PICK_FROM_FILE:
                    if (mFileUri != null) {
                        uriToSend = FileChooserUtil.getPath(getActivity(), mFileUri);
                    }
                    break;
                case TAKE_PICTURE:
                    if (mFileUri != null) {
                        uriToSend = mFileUri.getPath();
                    }
                    break;
            }

            CreateNewObjectEvent.Builder builder = new CreateNewObjectEvent.Builder()
                    .image(uriToSend)
                    .type(CreateNewObjectEvent.Type.image);

            EventBus.getDefault().post(builder.build());
        }
    }

    @Click(R.id.fab_new_photo)
    void onTakePhotoClicked() {
        fab.collapse();
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        //File image = new File(Environment.getExternalStorageDirectory(),  AppHelpers.getRandomBigInt(999999) + ".jpg");
        File photo = new File(Environment.getExternalStorageDirectory(), fname);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        mFileUri = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Click(R.id.fab_gallery)
    void onGalleryButtonClicked() {
        fab.collapse();
        startActivityForResult(
                Intent.createChooser(
                        new Intent(Intent.ACTION_GET_CONTENT)
                                .setType("image/*"), getString(R.string.choose_image_info)),
                PICK_FROM_FILE);

    }

    @OnActivityResult(TAKE_PICTURE)
    void handleTakePictureResult(int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        /* Set bitmap options to scale the image decode target */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = 2;
//        bmOptions.inPurgeable = true;



        Bitmap bitmap = BitmapFactory.decodeFile(mFileUri.getPath(), bmOptions);


        /* Test compress */
        File imageFile = new File(mFileUri.getPath());
        try {
            OutputStream out = null;
            out = new FileOutputStream(imageFile);
            //Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();
            out.close();
            Log.d(TAG, "imgdbg COMPRESSED FILE");
        } catch (Exception e) {
            Log.e(TAG, "imgdbg Error compressing: " + e.toString());
        }
        /* Decode the JPEG file into a Bitmap */

         /* Rotate image by EXIF data */

        BitmapHeplers bmHelpers = new BitmapHeplers();

        Bitmap bitmapRotated = bmHelpers.rotateBitmap(mFileUri, bmOptions);
        File imageFileRotated = new File(mFileUri.getPath());
        try {
            OutputStream out = null;
            out = new FileOutputStream(imageFileRotated);
            //Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

            bitmapRotated.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Log.d(TAG, "imgdbg COMPRESSED FILE");
        } catch (Exception e) {
            Log.e(TAG, "imgdbg Error compressing: " + e.toString());
        }

        photoFrom = TAKE_PICTURE;
        Picasso.with(getActivity()).load(mFileUri).error(
                R.drawable.ic_editor_insert_emoticon).into(ivPreview);
        if (mMode != AddUserActivity.MODE_SIGN_UP) {
            btnNext.setText(R.string.next);
        }
        //handleGalleryResult(data);
    }

    @OnActivityResult(PICK_FROM_FILE)
    void handleGalleryResult(int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        Uri selectedImage = data.getData();
        mTmpGalleryPicturePath = FileChooserUtil.getPath(getActivity(), selectedImage);
        if (mTmpGalleryPicturePath != null) {

            mFileUri = selectedImage;
            Picasso.with(getActivity()).load(selectedImage).error(
                    R.drawable.ic_editor_insert_emoticon).into(ivPreview);
            Log.d(TAG, "imgdbg pick from file A mFileUri: " + mFileUri.toString());

        } else {
            try {
                InputStream is = getActivity().getContentResolver().openInputStream(selectedImage);
                ivPreview.setImageBitmap(BitmapFactory.decodeStream(is));
                mTmpGalleryPicturePath = selectedImage.getPath();
                mFileUri = selectedImage;

                Log.d(TAG, "imgdbg pick from file B mFileUri: " + mFileUri.toString());
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        photoFrom = PICK_FROM_FILE;
        if (mMode != AddUserActivity.MODE_SIGN_UP) {
            btnNext.setText(R.string.next);
        }
    }

    @Override
    boolean validateInput() {
        return true;
    }

    @AfterViews
    void afterViews() {
        if (mFileUri == null) {
            btnNext.setText(R.string.skip);
        }
        //EDIT MODE
        if (mEditedObject != null) {
            if (mMode == AddUserActivity.MODE_SIGN_UP) {
                Picasso.with(getActivity())
                        .load(String.format(DlApi.AVATAR_NORMAL_URL, mEditedObject.getImage()))
                        .error(
                                R.drawable.ic_editor_insert_emoticon).into(ivPreview);
                Log.d(TAG, "edtdbg image: " + mEditedObject.getImage());
            } else {
                Picasso.with(getActivity())
                        .load(String.format(DlApi.PHOTO_NORMAL_URL, mEditedObject.getImage()))
                        .error(
                                R.drawable.ic_editor_insert_emoticon).into(ivPreview);
                Log.d(TAG, "edtdbg image: " + mEditedObject.getImage());
            }

        }

        if (mMode == AddUserActivity.MODE_SIGN_UP) {
            btnNext.setText(mEditedObject == null ? R.string.register : R.string.save_user_edit);
        }
    }
}

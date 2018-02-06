package com.ravibasil.thebigdream.barcon.pdf.fragment;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpicker.ImagePickerActivity;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.ravibasil.thebigdream.barcon.R;
import com.ravibasil.thebigdream.barcon.pdf.adapter.MyAdapter;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;


/**
 * Home fragment to start with creating PDF
 */
public class Home extends Fragment {

    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 1;
    private static int mImageCounter = 0;
    Activity activity;
    ArrayList<String> imagesUri;
    ArrayList<String> tempUris;
    String path, filename;
    Image image;
    Button createPdf;
    Button openPdf;
    Button addImages;
    Button cropImages;
    Button viewFiles;
    TextView textView;
    private int mMorphCounter1 = 1;

    private static ViewPager mPager;
    private static int currentPage = 0;
     private ArrayList<Integer> XMENArray = new ArrayList<Integer>();


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
      //  ButterKnife.bind(this, root);

        //initialising variables
        imagesUri = new ArrayList<>();
        tempUris = new ArrayList<>();
        addImages = (Button) root.findViewById(R.id.addImages);
        cropImages = (Button) root.findViewById(R.id.cropImages);
        createPdf = (Button) root.findViewById(R.id.pdfCreate);
        openPdf = (Button) root.findViewById(R.id.pdfOpen);
        textView = (TextView) root.findViewById(R.id.text);
        viewFiles = (Button) root.findViewById(R.id.btn_view_history);

        mPager = (ViewPager) root.findViewById(R.id.pager);


        //morphToSquare(createPdf, integer(R.integer.mb_animation));
        openPdf.setVisibility(View.GONE);

        addImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddingImages();
            }
        });

        cropImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImages();
            }
        });

        createPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createPdf();
            }
        });

        openPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPdf();
            }
        });

        viewFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startViewFiles();
            }
        });


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Images to Pdf");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_history_button, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_history:
                startViewFiles();
                break;
            default:
                break;
        }
        return true;
    }

    private void init() {
        viewFiles.setVisibility(View.GONE);
        mPager.setVisibility(View.VISIBLE);
        cropImages.setVisibility(View.VISIBLE);
        createPdf.setVisibility(View.VISIBLE);

        mPager.setAdapter(new MyAdapter(getActivity(),tempUris));

    }



    private void startViewFiles() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, new ViewFiles());
        transaction.addToBackStack("Hello");
        transaction.commit();
    }

    // Adding Images to PDFF
    void startAddingImages() {
        // Check if permissions are granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
            } else {
                selectImages();
            }
        } else {
            selectImages();
        }
    }

    void cropImages() {
        if (tempUris.size() == 0) {
            Toast.makeText(activity, R.string.toast_no_images, Toast.LENGTH_SHORT).show();
            return;
        }
        cropImages.setVisibility(View.INVISIBLE);
        next();
    }

    void next() {
        if (mImageCounter != tempUris.size()) {
            CropImage.activity(Uri.fromFile(new File(tempUris.get(mImageCounter))))
                    .setActivityMenuIconColor(color(R.color.colorPrimary))
                    .setInitialCropWindowPaddingRatio(0)
                    .setAllowRotation(true)
                    .setActivityTitle(getString(R.string.cropImage_activityTitle) + (mImageCounter + 1))
                    .start(getContext(), this);
        }
    }

    // Create Pdf of selected images
    void createPdf() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (imagesUri.size() == 0) {
            if (tempUris.size() == 0) {
                Toast.makeText(activity, R.string.toast_no_images, Toast.LENGTH_LONG).show();
                return;
            } else {
                imagesUri = (ArrayList<String>) tempUris.clone();
            }
        }
        //custom dialog
        final View dialogView = View.inflate(getActivity(),R.layout.dialog_create_pdf,null);

        final Dialog dialog = new Dialog(getActivity(),R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);


        final EditText inputName = (EditText) dialog.findViewById(R.id.et_inputName);
        Button ok = (Button) dialog.findViewById(R.id.btn_ok);
        Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        inputName.setSingleLine(true);
        inputName.setImeOptions(EditorInfo.IME_ACTION_DONE);

        //handling actiondone
        inputName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE|| EditorInfo.IME_ACTION_UNSPECIFIED == i
                        ||EditorInfo.IME_ACTION_SEND == i){
                    InputMethodManager inputMethodManager =(InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInput(0,0);

                    handlingButtonClick(inputName, dialogView,dialog);

                    return true;
                }
                return false;
            }
        });


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager =(InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(0,0);

                handlingButtonClick(inputName, dialogView, dialog);
                      }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revealShow(dialogView, false, dialog);
            }
        });

        ImageView imageView = (ImageView)dialog.findViewById(R.id.closeDialogImg);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                revealShow(dialogView, false, dialog);
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                revealShow(dialogView, true, null);
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK){

                    revealShow(dialogView, false, dialog);
                    return true;
                }

                return false;
            }
        });



        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();

    }

    public void handlingButtonClick(EditText inputName, View dialogView, Dialog dialog){
        String input = inputName.getText().toString();
        if (input == null || input.toString().trim().equals("")) {
            Toast.makeText(activity, R.string.toast_name_not_blank, Toast.LENGTH_LONG).show();
        } else {
            revealShow(dialogView, false, dialog);
            filename = input.toString();

            new CreatingPdf().execute();


            if (mMorphCounter1 == 0) {
                mMorphCounter1++;
            }

        }
    }
    private void revealShow(View dialogView, boolean b, final Dialog dialog) {

        final View view = dialogView.findViewById(R.id.dialog);

        int w = view.getWidth();
        int h = view.getHeight();

        int endRadius = (int) Math.hypot(w, h);

        int cx = (int) (createPdf.getX() + (createPdf.getWidth()/2));
        int cy = (int) (createPdf.getY())+ createPdf.getHeight() + 56;


        if(b){
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx,cy, 0, endRadius);

            view.setVisibility(View.VISIBLE);
            revealAnimator.setDuration(700);
            revealAnimator.start();

        } else {

            Animator anim =
                    ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);

                }
            });
            anim.setDuration(700);
            anim.start();
        }

    }

    void openPdf() {
        File file = new File(path);
        Intent target = new Intent(Intent.ACTION_VIEW);
        //target.setDataAndType(Uri.fromFile(file), getString(R.string.pdf_type));
        //target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Uri apkURI = FileProvider.getUriForFile(
                getActivity(),
                getActivity().getApplicationContext()
                        .getPackageName() + ".provider", file);
        target.setDataAndType(apkURI, getActivity().getString(R.string.pdf_type));
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = Intent.createChooser(target, getString(R.string.open_file));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, R.string.toast_no_pdf_app, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Opens ImagePickerActivity to select Images
     */
    public void selectImages() {
        Intent intent = new Intent(activity, ImagePickerActivity.class);

        //add to intent the URIs of the already selected images
        //first they are converted to Uri objects
        ArrayList<Uri> uris = new ArrayList<>(tempUris.size());
        for (String stringUri : tempUris) {
            uris.add(Uri.fromFile(new File(stringUri)));
        }
        // add them to the intent
        intent.putExtra(ImagePickerActivity.EXTRA_IMAGE_URIS, uris);

        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
    }

    /**
     * Called after user is asked to grant permissions
     *
     * @param requestCode  REQUEST Code for opening permissions
     * @param permissions  permissions asked to user
     * @param grantResults bool array indicating if permission is granted
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImages();
                    Toast.makeText(activity, R.string.toast_permissions_given, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(activity, R.string.toast_insufficient_permissions, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Called after ImagePickerActivity is called
     *
     * @param requestCode REQUEST Code for opening ImagePickerActivity
     * @param resultCode  result code of the process
     * @param data        Data of the image selected
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_REQUEST_GET_IMAGES && resultCode == Activity.RESULT_OK) {

            tempUris.clear();

            ArrayList<Uri> imageUris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            for (int i = 0; i < imageUris.size(); i++) {
                tempUris.add(imageUris.get(i).getPath());
            }
            Toast.makeText(activity, R.string.toast_images_added, Toast.LENGTH_LONG).show();
            cropImages.setVisibility(View.VISIBLE);

            createPdf.setVisibility(View.VISIBLE);
            addImages.setText("Add Images");
            openPdf.setVisibility(View.GONE);
            init();
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri resultUri = result.getUri();
                imagesUri.add(resultUri.getPath());
                Toast.makeText(activity, R.string.toast_imagecropped, Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(activity, R.string.toast_error_getCropped, Toast.LENGTH_LONG).show();
                imagesUri.add(tempUris.get(mImageCounter));
                error.printStackTrace();
            } else {
                imagesUri.add(tempUris.get(mImageCounter));
            }
            //morphToSquare(createPdf, integer(R.integer.mb_animation));
            mImageCounter++;
            next();
        }
    }



    public int integer(@IntegerRes int resId) {
        return getResources().getInteger(resId);
    }

    public int dimen(@DimenRes int resId) {
        return (int) getResources().getDimension(resId);
    }

    public int color(@ColorRes int resId) {
        return getResources().getColor(resId);
    }

    /**
     * An async task that converts selected images to Pdf
     */
    public class CreatingPdf extends AsyncTask<String, String, String> {

        // Progress dialog
        ProgressDialog progressDialog = new ProgressDialog(activity);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setCancelable(false);
            progressDialog.setTitle(R.string.please_wait);
            progressDialog.setMessage("Fetching files. This may take a while");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + Home.this.getString(R.string.pdf_dir);

            File folder = new File(path);
            if (!folder.exists()) {
                boolean success = folder.mkdir();
                if (!success) {
                    Toast.makeText(activity, "Error on creating application folder", Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

            path = path + filename + Home.this.getString(R.string.pdf_ext);

            Log.v("stage 1", "store the pdf in sd card");

            Document document = new Document(PageSize.A4, 38, 38, 50, 38);

            Log.v("stage 2", "Document Created");

            Rectangle documentRect = document.getPageSize();

            try {
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));

                Log.v("Stage 3", "Pdf writer");

                document.open();

                Log.v("Stage 4", "Document opened");

                for (int i = 0; i < imagesUri.size(); i++) {

                    Bitmap bmp = MediaStore.Images.Media.getBitmap(
                            activity.getContentResolver(), Uri.fromFile(new File(imagesUri.get(i))));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 70, stream);


                    image = Image.getInstance(imagesUri.get(i));


                    float documentWidth = document.getPageSize().getWidth() - document.leftMargin()- document.rightMargin();
                    float documentHeight = document.getPageSize().getHeight() - document.topMargin() - document.bottomMargin();
                    if (bmp.getWidth() > documentRect.getWidth()
                            || bmp.getHeight() > documentRect.getHeight()) {
                        //bitmap is larger than page,so set bitmap's size similar to the whole page
                      //  image.setRotation(180f);
                        image.scaleToFit(documentWidth, documentHeight);
                    } else {
                        //bitmap is smaller than page, so add bitmap simply.
                        //[note: if you want to fill page by stretching image,
                        // you may set size similar to page as above]
                        image.scaleToFit(bmp.getWidth(), bmp.getHeight());
                    }

                    Log.v("Stage 6", "Image path adding");

                    image.setAbsolutePosition(
                            (documentRect.getWidth() - image.getScaledWidth()) / 2,
                            (documentRect.getHeight() - image.getScaledHeight()) / 2);
                    Log.v("Stage 7", "Image Alignments");

                    //image.setBorder(Image.BOX);

                   // image.setBorderWidth(15);

                    document.add(image);

                    document.newPage();
                }

                Log.v("Stage 8", "Image adding");

                document.close();

                Log.v("Stage 7", "Document Closed" + path);
            } catch (Exception e) {
                e.printStackTrace();
            }

            document.close();
            imagesUri.clear();
            tempUris.clear();
            mImageCounter = 0;

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            openPdf.setVisibility(View.VISIBLE);
            progressDialog.dismiss();

            viewFiles.setVisibility(View.GONE);
            mPager.setVisibility(View.GONE);
            cropImages.setVisibility(View.GONE);
            createPdf.setVisibility(View.GONE);
            addImages.setText("Create new pdf");
            //morphToSuccess(createPdf);
        }
    }

}
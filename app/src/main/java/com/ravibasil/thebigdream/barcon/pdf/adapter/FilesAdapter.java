package com.ravibasil.thebigdream.barcon.pdf.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ravibasil.thebigdream.barcon.R;
import com.ravibasil.thebigdream.barcon.pdf.fragment.ViewFiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<File> mFeedItems;
    private String mFileName;
    private boolean multiSelect = false,isFileOpen=true;
    private ArrayList<Integer> selectedItems = new ArrayList<Integer>();
    int cr=0;

    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            multiSelect = true;
            //actionMode.setTitle(selectedItems.size()+" selected");


            MenuItem shareItem = menu.add(0,1,0,"Share");
            shareItem.setIcon(R.drawable.ic_share_black_24dp);
            shareItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);

            MenuItem deleteItem = menu.add(0,2,0,"Delete");
            deleteItem.setIcon(R.drawable.ic_delete_forever_black_24dp);
            deleteItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);


            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            //menu.set
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case 1:
                    ArrayList<Uri> files = new ArrayList<Uri>();
                    for (Integer integer: selectedItems){
                        Uri uri = FileProvider.getUriForFile(mContext,
                                "com.ravibasil.thebigdream.barcon.provider",
                                new File(mFeedItems.get(integer).getPath()));
                        files.add(uri);
                    }
                    shareFile(files);
                    break;
                case 2:
                    for (Integer integer: selectedItems){
                        Log.d("Removing",integer+"");
                        deleteFile(integer);
                    }
                    break;
            }

            actionMode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            multiSelect = false;
            isFileOpen=true;
            selectedItems.clear();
            notifyDataSetChanged();

        }

    };

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_files_name,tv_files_date;
        public RelativeLayout frameLayout;
        public MyViewHolder(View itemView) {
            super(itemView);
            tv_files_date = (TextView) itemView.findViewById(R.id.tv_date_modified);
            tv_files_name = (TextView) itemView.findViewById(R.id.tv_files_name);
            frameLayout = (RelativeLayout) itemView.findViewById(R.id.frameLayout);
        }
        void update(final Integer value){

            if (selectedItems.contains(value)){
                frameLayout.setBackgroundColor(Color.LTGRAY);
            }else{
                frameLayout.setBackgroundColor(Color.WHITE);
            }

            frameLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ((AppCompatActivity) view.getContext()).startSupportActionMode(actionModeCallbacks);
                    isFileOpen=false;
                    selectItems(value);

                    return true;
                }
            });

            frameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isFileOpen){
                        openFile(value);
                    }else
                    selectItems(value);
                }
            });
        }
        void selectItems(Integer item){
            if(multiSelect){
                if(selectedItems.contains(item)){
                    cr--;
                    selectedItems.remove(item);
                    frameLayout.setBackgroundColor(Color.WHITE);
                }else {
                    cr++;
                    selectedItems.add(item);
                    frameLayout.setBackgroundColor(Color.LTGRAY);
                }
            }
        }


    }

    /**
     * Returns adapter instance
     *
     * @param context   the context calling this adapter
     * @param feedItems array list containing path of files
     */
    public FilesAdapter(Context context, ArrayList<File> feedItems) {
        this.mContext = context;
        this.mFeedItems = feedItems;
    }

    /**
     * Sets pdf files
     *
     * @param pdfFiles array list containing path of files
     */
    public void setData(ArrayList<File> pdfFiles) {
        mFeedItems = pdfFiles;
        notifyDataSetChanged();
    }

    /**
     * Return number of elements in adapter
     *
     * @return count of number of elements*/

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FilesAdapter.MyViewHolder holder, final int position) {
        // Extract file name from path
        final String fileName = mFeedItems.get(position).getPath();
        String[] name = fileName.split("/");
        holder.tv_files_name.setText(name[name.length - 1]);
        File file = new File(fileName);

        Date lastModified = new Date(file.lastModified());
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        holder.tv_files_date.setText(sdf.format(file.lastModified()) + "");
        holder.update(position);
/*
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog(fileName,position);
            }
        });*/
    }

    public void showBottomSheetDialog(final String fileName, final int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_actions_pdf, null);

        BottomSheetDialog dialog = new BottomSheetDialog(mContext);
        dialog.setContentView(view);
        LinearLayout ls_preview = (LinearLayout) dialog.findViewById(R.id.bs_preview);
        LinearLayout ls_share = (LinearLayout) dialog.findViewById(R.id.bs_share);
        LinearLayout ls_print = (LinearLayout) dialog.findViewById(R.id.bs_print_file);
        LinearLayout ls_rename = (LinearLayout) dialog.findViewById(R.id.bs_rename_file);
        LinearLayout ls_delete = (LinearLayout) dialog.findViewById(R.id.bs_delete_file);

        dialog.show();

        ls_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openFile(fileName);

            }
        });
        ls_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //shareFile(fileName);
            }
        });
        ls_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //deleteFile(fileName, position);
            }
        });
        ls_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doPrint(fileName);
            }
        });
        ls_rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                renameFile(position);
                //relativeLayout_rename.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mFeedItems.size();
    }

    public void openFile(int position) {
        String fileName = mFeedItems.get(position).getPath();
        File file = new File(fileName);
        Intent target = new Intent(Intent.ACTION_VIEW);
        //target.setDataAndType(Uri.fromFile(file), mContext.getString(R.string.pdf_type));
        Uri apkURI = FileProvider.getUriForFile(
                mContext,
                mContext.getApplicationContext()
                        .getPackageName() + ".provider", file);
        target.setDataAndType(apkURI, mContext.getString(R.string.pdf_type));
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


       // target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent intent = Intent.createChooser(target, mContext.getString(R.string.open_file));
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mContext, mContext.getString(R.string.toast_no_pdf_app), Toast.LENGTH_LONG).show();
        }
    }

    private void deleteFile(int position) {
        String fileName = mFeedItems.get(position).getPath();
        File fdelete = new File(fileName);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Toast.makeText(mContext, R.string.toast_file_deleted, Toast.LENGTH_SHORT).show();
                mFeedItems.remove(position);
                notifyDataSetChanged();
                if (mFeedItems.size() == 0) {
                    ViewFiles.emptyStatusTextView.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(mContext, R.string.toast_file_not_deleted, Toast.LENGTH_LONG).show();
            }
        }

    }

    private void renameFile(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.creating_pdf);
        builder.setMessage("Enter file name");
        //add button
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              /*  if (input == null) {
                    Toast.makeText(mContext, R.string.toast_name_not_blank, Toast.LENGTH_LONG).show();

                } else {
                    String newname = input.toString();
                    File oldfile = mFeedItems.get(position);
                    String[] x = mFeedItems.get(position).getPath().split("/");
                    String newfilename = "";
                    for (int i = 0; i < x.length - 1; i++)
                        newfilename = newfilename + "/" + x[i];

                    File newfile = new File(newfilename + "/" + newname + mContext.getString(R.string.pdf_ext));

                    Log.e("Old file name", oldfile + " ");
                    Log.e("New file name", newfile + " ");

                    if (oldfile.renameTo(newfile)) {
                        Toast.makeText(mContext, R.string.toast_file_renamed, Toast.LENGTH_LONG).show();
                        mFeedItems.set(position, newfile);
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(mContext, R.string.toast_file_not_renamed, Toast.LENGTH_LONG).show();
                    }

                }*/
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        //create the alertDialog
        AlertDialog dialog = builder.create();

    }

    /**
     * Prints a file
     *
     * @param fileName Path of file to be printed
     */
    private void doPrint(String fileName) {
        PrintManager printManager = (PrintManager) mContext
                .getSystemService(Context.PRINT_SERVICE);

        mFileName = fileName;
        String jobName = mContext.getString(R.string.app_name) + " Document";
        printManager.print(jobName, mPrintDocumentAdapter, null);
    }

    /**
     * Emails the desired PDF using application of choice by user
     *
     * @author RakiRoad
     * @param
     */
    private void shareFile(ArrayList<Uri> fileUri) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_TEXT, "I have attached a PDF to this message");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("application/pdf");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUri);

        mContext.startActivity(Intent.createChooser(intent, "Sharing"));
    }


    private PrintDocumentAdapter mPrintDocumentAdapter = new PrintDocumentAdapter() {

        @Override
        public void onWrite(PageRange[] pages,
                            ParcelFileDescriptor destination,
                            CancellationSignal cancellationSignal,
                            WriteResultCallback callback) {
            InputStream input = null;
            OutputStream output = null;
            try {
                input = new FileInputStream(mFileName);
                output = new FileOutputStream(destination.getFileDescriptor());

                byte[] buf = new byte[1024];
                int bytesRead;

                while ((bytesRead = input.read(buf)) > 0) {
                    output.write(buf, 0, bytesRead);
                }

                callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

            } catch (Exception e) {
                //Catch exception
            } finally {
                try {
                    if (input != null) {
                        input.close();
                    }
                    if (output != null) {
                        output.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        public void onLayout(PrintAttributes oldAttributes,
                             PrintAttributes newAttributes,
                             CancellationSignal cancellationSignal,
                             LayoutResultCallback callback,
                             Bundle extras) {

            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }
            PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("myFile")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .build();

            callback.onLayoutFinished(pdi, true);
        }
    };
}
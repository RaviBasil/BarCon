package com.ravibasil.thebigdream.barcon.pdf.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ravibasil.thebigdream.barcon.R;
import com.ravibasil.thebigdream.barcon.pdf.adapter.FilesAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ViewFiles extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final int NAME_INDEX = 0;
    private static final int DATE_INDEX = 1;

    Activity activity;
    FilesAdapter adapter;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeView;
    public static TextView emptyStatusTextView;

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

        View root = inflater.inflate(R.layout.fragment_view_files, container, false);

        recyclerView = (RecyclerView) root.findViewById(R.id.list);
        swipeView = (SwipeRefreshLayout) root.findViewById(R.id.swipe);
        emptyStatusTextView = (TextView) root.findViewById(R.id.emptyStatusTextView);

        //Create/Open folder
        File folder = getOrCreatePdfDirectory();

        // Initialize variables
        final ArrayList<File> pdfFiles = new ArrayList<>();
        final File[] files = folder.listFiles();

        if (files == null) {
            emptyStatusTextView.setVisibility(View.VISIBLE);
        }

        adapter = new FilesAdapter(activity, pdfFiles);
        RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManger);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        swipeView.setOnRefreshListener(this);

        // Populate data into listView
        populatePdfList();
        getActivity().setTitle("View PDF");


        return root;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_view_files_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_sort:
                displaySortDialog();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onRefresh() {

       // Log.v("refresh", "refreshing dta");
        populatePdfList();
        swipeView.setRefreshing(false);
    }

    private void populatePdfList() {
        new PopulateList().execute();
    }

    /**
     * AsyncTask used to populate the list of elements in the background
     */
    private class PopulateList extends AsyncTask<Void, Void, Void> {
        // Progress dialog
        ProgressDialog progressDialog = new ProgressDialog(activity);

        @Override
        protected Void doInBackground(Void... voids) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    populateListView();
                }
            });
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setCancelable(false);
            progressDialog.setTitle(R.string.please_wait);
            progressDialog.setMessage("Fetching files. This may take a while");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            progressDialog.dismiss();
        }

        /**
         * Populate data into listView
         */
        private void populateListView() {
            ArrayList<File> pdfFiles = new ArrayList<>();
            final File[] files = getOrCreatePdfDirectory().listFiles();
            if (files == null)
                Toast.makeText(activity, R.string.toast_no_pdfs, Toast.LENGTH_LONG).show();
            else {
                pdfFiles = getPdfsFromPdfFolder();
            }
          //  Log.v("done", "adding");
            adapter.setData(pdfFiles);
            recyclerView.setAdapter(adapter);
        }
    }

    private void displaySortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sort by")
                .setItems(R.array.sort_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList<File> pdfsFromFolder = getPdfsFromPdfFolder();
                        switch (which) {
                            case DATE_INDEX:
                                sortFilesByDateNewestToOldest(pdfsFromFolder);
                                adapter.setData(pdfsFromFolder);
                                break;
                            case NAME_INDEX:
                                sortByNameAlphabetical(pdfsFromFolder);
                                adapter.setData(pdfsFromFolder);
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    private void sortByNameAlphabetical(ArrayList<File> pdfsFromFolder) {
        Collections.sort(pdfsFromFolder);
    }

    private void sortFilesByDateNewestToOldest(ArrayList<File> pdfsFromFolder) {
        Collections.sort(pdfsFromFolder, new Comparator<File>() {
            @Override
            public int compare(File file, File file2) {
                return Long.compare(file2.lastModified(), file.lastModified());
            }
        });
    }

    private ArrayList<File> getPdfsFromPdfFolder() {
        return getPdfsFromFolder(getOrCreatePdfDirectory().listFiles());
    }

    private File getOrCreatePdfDirectory() {
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + getString(R.string.pdf_dir));
        if (!folder.exists()) {
            folder.mkdir();
        }
        return folder;
    }

    private ArrayList<File> getPdfsFromFolder(File[] files) {
        final ArrayList<File> pdfFiles = new ArrayList<>();
        for (File file : files) {
            if (!file.isDirectory() && file.getName().endsWith(getString(R.string.pdf_ext))) {
                pdfFiles.add(file);
                //Log.v("adding", file.getName());
            }
        }
        return pdfFiles;
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }


}

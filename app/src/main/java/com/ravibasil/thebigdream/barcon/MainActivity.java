package com.ravibasil.thebigdream.barcon;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ravibasil.thebigdream.barcon.database.DatabaseHandler;
import com.ravibasil.thebigdream.barcon.textables.modal.Texty;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private PrefManager prefManager;
    private int layouts[];
    private Button btnSkip, btnNext;
    private MyViewPagerAdapter myViewPagerAdapter;
    private ViewPager viewPager;
    private TextView[] dots;
    private LinearLayout dotsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences sp = getSharedPreferences(Config.SHARED_TEXTALBLES,MODE_PRIVATE);



        //checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        if(!prefManager.isFirstTimeLaunch()){
            launchHomeScreen();
            finish();
        }

        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);

        if(sp.getBoolean(Config.TEXTABLES,false)) {

        }else{
            BackgroundTask backgroundTask = new BackgroundTask(MainActivity.this);
            backgroundTask.execute();
        }

        //layout of all welcome sliders
        // add few more layout if you want
        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3
        };
        //adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor(getResources().getColor(R.color.bg_screen1));

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });

    }


    private class BackgroundTask extends AsyncTask<Void,Void,Void> {
        private ProgressDialog progressDialog;
        public BackgroundTask(MainActivity activity){
            progressDialog =new ProgressDialog(activity);
            progressDialog.setTitle("Please wait");
            progressDialog.setMessage("App is getting ready for first time");

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                parseJsonFile();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }

    private void parseJsonFile() throws JSONException {
        //Log.d("Insert: ", "Inserting ..");
        DatabaseHandler db = new DatabaseHandler(this);

        JSONArray jsonArray = new JSONArray(loadJSONFromAsset());
        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String category = jsonObject.getString("category");
            String items = jsonObject.getString("items");

            JSONArray jsonArrayItems = new JSONArray(items);
            List<Texty> data = new ArrayList<Texty>();

            for (int j = 0; j < jsonArrayItems.length(); j++) {
                JSONObject jsonObjectItems = jsonArrayItems.getJSONObject(j);

                String name = jsonObjectItems.getString("name");
                String art = jsonObjectItems.getString("art");
                db.addTextables(new Texty(category, name, art));

                //Log.d("Hello",category+"--"+name+"--"+art);
            }
        }
        SharedPreferences.Editor editor = getSharedPreferences(Config.SHARED_TEXTALBLES,MODE_PRIVATE).edit();
        editor.putBoolean(Config.TEXTABLES,true);
        editor.commit();
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("content.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }


    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(MainActivity.this, BarcodeActivity.class));
        finish();
    }


    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);


            if(position==0){
                changeStatusBarColor(getResources().getColor( R.color.bg_screen1));
            }else if ( position == 1){
                changeStatusBarColor(getResources().getColor(R.color.bg_screen4));
            }else{
                changeStatusBarColor(getResources().getColor(R.color.bg_screen3));
            }

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     * @param //bg_screen1
     */
    private void changeStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}

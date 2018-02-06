package com.ravibasil.thebigdream.barcon.pdf.fragment;

import android.support.v4.app.Fragment;

/**
 * Created by ravibasil on 1/25/18.
 */

public class BaseFragment extends Fragment {
    /**
     * Could handle back press.
     * @return true if back press was handled
     */
    public boolean onBackPressed() {
        return false;
    }
}
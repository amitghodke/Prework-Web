package com.sample.preworkassignment.constant;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by admin on 6/22/2019.
 */

public class Utils {
    public static void showToast(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();

    }
}

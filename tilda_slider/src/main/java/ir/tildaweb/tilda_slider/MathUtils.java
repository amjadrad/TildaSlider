package ir.tildaweb.tilda_slider;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.DisplayMetrics;

import java.io.File;


public class MathUtils {


    public static int convertDipToPixels(Context context, float dips) {
        return (int) (dips * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static int convertPxToDp(Context context, float px) {
        return (int) (px / context.getResources().getDisplayMetrics().density - 0.5f);
    }


    public static String getView(int view) {
        String result = view + "";
        if (view > 1000) {
            return String.format("%.1f", (float) view / 1000) + " k";
        } else {
            return result;
        }
    }

    public static String convertNumberToKiloPersian(int number) {
        String result = number + "";
        if (number > 1000) {
            return String.format("%.1f هزار", (float) number / 1000);
        } else {
            return result;
        }
    }

    public static String getPoint(float view) {
        return String.format("%.1f", view);
    }

    public static int calculateDiscount(int withDiscount, int withoutDiscount) {
        return Math.round(((withoutDiscount - withDiscount) / (float) withoutDiscount) * 100);
    }


    public static int[] getImageSize(Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        return new int[]{imageWidth, imageHeight};
    }

    public static int getScreenWidth(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
}

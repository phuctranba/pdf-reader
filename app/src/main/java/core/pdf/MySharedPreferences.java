package core.pdf;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MySharedPreferences {
    private static String PREF_NIGHT_MODE = "pref_night_mode";
    private static String PREF_GRID_VIEW = "pref_grid_view";
    private static String PREF_HORIZONTAL_SCROLL  = "pref_horizontal_scroll";
    private static String PREF_SWIPE = "pref_swipe";

    private static String SORT_ALL_FILE = "pref_sort_all_file";
    private static String VIEW_ALL_FILE = "pref_view_all_file";
    private static String SORT_FAVOURITE = "pref_sort_favourite";
    private static String VIEW_FAVOURITE = "pref_view_favourite";
    private static String SORT_RECENT = "pref_sort_recent";
    private static String VIEW_RECENT = "pref_view_recent";


    public static void setPrefNightMode(Context context,boolean isEnabled){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_NIGHT_MODE,isEnabled);
        editor.apply();

    }

    public static boolean getPrefNightMode(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return  preferences.getBoolean(PREF_NIGHT_MODE,false);
    }

    public static void setPrefGridView(Context context,boolean isEnabled){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_GRID_VIEW,isEnabled);
        editor.apply();

    }

    public static boolean getPrefGridView(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return  preferences.getBoolean(PREF_GRID_VIEW,false);
    }



    public static void setPrefHorizontalScroll(Context context,boolean isEnabled){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_HORIZONTAL_SCROLL,isEnabled);
        editor.apply();

    }

    public static boolean getPrefHorizontalScroll(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return  preferences.getBoolean(PREF_HORIZONTAL_SCROLL,false);
    }

    public static void setPrefSwipe(Context context,boolean isEnabled){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_SWIPE,isEnabled);
        editor.apply();

    }

    public static boolean getPrefSwipe(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return  preferences.getBoolean(PREF_SWIPE,false);
    }

    public static void setPrefSortAllFile(Context context,int value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(SORT_ALL_FILE,value);
        editor.apply();
    }

    public static int getPrefSortAllFile(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return  preferences.getInt(SORT_ALL_FILE,0);
    }

    public static void setPrefViewAllFile(Context context,boolean isEnabled){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(VIEW_ALL_FILE,isEnabled);
        editor.apply();
    }

    public static boolean getPrefViewAllFile(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return  preferences.getBoolean(VIEW_ALL_FILE,true);
    }

    public static void setPrefSortFavourite(Context context,int value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(SORT_FAVOURITE,value);
        editor.apply();
    }

    public static int getPrefSortFavourite(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return  preferences.getInt(SORT_FAVOURITE,0);
    }

    public static void setPrefViewFavourite(Context context,boolean isEnabled){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(VIEW_FAVOURITE,isEnabled);
        editor.apply();
    }

    public static boolean getPrefViewFavourite(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return  preferences.getBoolean(VIEW_FAVOURITE,true);
    }

    public static void setPrefSortRecent(Context context,int value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(SORT_RECENT,value);
        editor.apply();
    }

    public static int getPrefSortRecent(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return  preferences.getInt(SORT_RECENT,0);
    }

    public static void setPrefViewRecent(Context context,boolean isEnabled){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(VIEW_RECENT,isEnabled);
        editor.apply();
    }

    public static boolean getPrefViewRecent(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return  preferences.getBoolean(VIEW_RECENT,true);
    }
}

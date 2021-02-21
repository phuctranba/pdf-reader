package core.pdf;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.core.content.FileProvider;

import com.core.pdf.reader.R;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PdfRecyclerAdapter extends EmptyRecyclerView.Adapter<PdfRecyclerAdapter.PdfViewHolder> implements Filterable {


    public static final int ITEM_TYPE_LIST = 0;
    public static final int ITEM_TYPE_GRID = 1;
    ArrayList<PDFDoc> pdfDocuments;
    ArrayList<PDFDoc> pdfDocumentsAll;
    Context mContext;
    private static final String LOG_TAG = "FileViewerAdapter";
    DatabaseHelper databaseHelper;
    String screenParent = "";
    private int VIEW_TYPE;

    static boolean sortNameIncrease = true, sortTimeIncrease = false, sortSizeIncrease = false;

    public PdfRecyclerAdapter(Context context, ArrayList<PDFDoc> docs, String screen, boolean viewType, int sortType) {
        pdfDocumentsAll = docs;
        pdfDocuments = docs;
        mContext = context;
        databaseHelper = new DatabaseHelper(mContext);
        screenParent = screen;
        VIEW_TYPE = viewType?0:1;

        sortNameIncrease = sortType == 1;
        sortTimeIncrease = sortType == 3;
        sortSizeIncrease = sortType == 5;
    }

    public void setViewType(int viewType) {
        VIEW_TYPE = viewType;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public PdfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = null;
        switch (VIEW_TYPE) {
            case ITEM_TYPE_LIST:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_item, parent, false);
                break;
            case ITEM_TYPE_GRID:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_item_grid, parent, false);
                break;
        }
        return new PdfViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PdfViewHolder holder, final int position) {
        final PDFDoc item = pdfDocuments.get(position);
        holder.fileName.setText(item.getName());
        holder.fileSize.setText(GetFileSize(Double.parseDouble(item.getSize())));
        holder.fileLastModified.setText(convertDateToString(item.getLastModified()));
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdfDocuments.get(position).setOpenedTime(new Date());
                databaseHelper.updatePDFDoc(pdfDocuments.get(position));
                OpenPdf(item);
            }
        });

        holder.btnViewOption.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {

                //displaying the popup
                MenuBuilder menuBuilder = new MenuBuilder(mContext);
                MenuInflater inflater = new MenuInflater(mContext);
                inflater.inflate(R.menu.options_menu, menuBuilder);
                MenuPopupHelper optionsMenu = new MenuPopupHelper(mContext, menuBuilder, holder.btnViewOption);
                optionsMenu.setForceShowIcon(true);

                if (item.isFavourite()) {
                    menuBuilder.getItem(0).setTitle("UnFavourite");
                    menuBuilder.getItem(0).setIcon(R.drawable.ic_star_yellow);
                } else {
                    menuBuilder.getItem(0).setTitle("Add to Favourite");
                    menuBuilder.getItem(0).setIcon(R.drawable.ic_star_white);
                }

                // Set Item Click Listener
                menuBuilder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem itemmenu) {
                        switch (itemmenu.getItemId()) {
                            case R.id.favourite: {

                                switch (screenParent){
                                    case "RECENT":
                                    case "ALLFILE":{
                                        pdfDocuments.get(position).setFavourite(!item.isFavourite());
                                        databaseHelper.updatePDFDoc(pdfDocuments.get(position));
                                        break;
                                    }
                                    case "FAVOURITE":{
                                        pdfDocuments.get(position).setFavourite(!item.isFavourite());
                                        databaseHelper.updatePDFDoc(pdfDocuments.get(position));
                                        pdfDocuments.remove(position);
                                        break;
                                    }
                                }
                                pdfDocumentsAll = new ArrayList<>(pdfDocuments);
                                notifyDataSetChanged();
                                return true;
                            }
                            case R.id.sort: {
                                PopupMenu popup = new PopupMenu(mContext, holder.btnViewOption);
                                popup.inflate(R.menu.sort_menu);

                                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        switch (menuItem.getItemId()) {
                                            case R.id.name: {
                                                Collections.sort(pdfDocuments, sortNameIncrease ? PDFDoc.sortNameDecrease : PDFDoc.sortNameIncrease);
                                                sortNameIncrease = !sortNameIncrease;
                                                pdfDocumentsAll = new ArrayList<>(pdfDocuments);

                                                switch (screenParent){
                                                    case "ALLFILE":{
                                                        final AllFileActivity allFileActivity = (AllFileActivity) mContext;
                                                        allFileActivity.typeSort = sortNameIncrease?1:2;
                                                        MySharedPreferences.setPrefSortAllFile(mContext,allFileActivity.typeSort);
                                                        break;
                                                    }
                                                    case "FAVOURITE":{
                                                        final FavouriteActivity favouriteActivity = (FavouriteActivity) mContext;
                                                        favouriteActivity.typeSort = sortNameIncrease?1:2;
                                                        MySharedPreferences.setPrefSortFavourite(mContext,favouriteActivity.typeSort);
                                                        break;
                                                    }
                                                    case "RECENT":{
                                                        final RecentActivity recentActivity = (RecentActivity) mContext;
                                                        recentActivity.typeSort = sortNameIncrease?1:2;
                                                        MySharedPreferences.setPrefSortRecent(mContext,recentActivity.typeSort);
                                                        break;
                                                    }
                                                }

                                                notifyDataSetChanged();
                                            }
                                            break;
                                            case R.id.time: {
                                                Collections.sort(pdfDocuments, sortTimeIncrease ? PDFDoc.sortTimeDecrease : PDFDoc.sortTimeIncrease);
                                                sortTimeIncrease = !sortTimeIncrease;
                                                pdfDocumentsAll = new ArrayList<>(pdfDocuments);

                                                switch (screenParent){
                                                    case "ALLFILE":{
                                                        final AllFileActivity allFileActivity = (AllFileActivity) mContext;
                                                        allFileActivity.typeSort = sortTimeIncrease?3:4;
                                                        MySharedPreferences.setPrefSortAllFile(mContext,allFileActivity.typeSort);
                                                        break;
                                                    }
                                                    case "FAVOURITE":{
                                                        final FavouriteActivity favouriteActivity = (FavouriteActivity) mContext;
                                                        favouriteActivity.typeSort = sortTimeIncrease?3:4;
                                                        MySharedPreferences.setPrefSortFavourite(mContext,favouriteActivity.typeSort);
                                                        break;
                                                    }
                                                    case "RECENT":{
                                                        final RecentActivity recentActivity = (RecentActivity) mContext;
                                                        recentActivity.typeSort = sortTimeIncrease?3:4;
                                                        MySharedPreferences.setPrefSortRecent(mContext,recentActivity.typeSort);
                                                        break;
                                                    }
                                                }

                                                notifyDataSetChanged();
                                            }
                                            break;
                                            case R.id.size: {
                                                Collections.sort(pdfDocuments, sortSizeIncrease ? PDFDoc.sortSizeDecrease : PDFDoc.sortSizeIncrease);
                                                sortSizeIncrease = !sortSizeIncrease;
                                                pdfDocumentsAll = new ArrayList<>(pdfDocuments);

                                                switch (screenParent){
                                                    case "ALLFILE":{
                                                        final AllFileActivity allFileActivity = (AllFileActivity) mContext;
                                                        allFileActivity.typeSort = sortSizeIncrease?5:6;
                                                        MySharedPreferences.setPrefSortAllFile(mContext,allFileActivity.typeSort);
                                                        break;
                                                    }
                                                    case "FAVOURITE":{
                                                        final FavouriteActivity favouriteActivity = (FavouriteActivity) mContext;
                                                        favouriteActivity.typeSort = sortSizeIncrease?5:6;
                                                        MySharedPreferences.setPrefSortFavourite(mContext,favouriteActivity.typeSort);
                                                        break;
                                                    }
                                                    case "RECENT":{
                                                        final RecentActivity recentActivity = (RecentActivity) mContext;
                                                        recentActivity.typeSort = sortSizeIncrease?5:6;
                                                        MySharedPreferences.setPrefSortRecent(mContext,recentActivity.typeSort);
                                                        break;
                                                    }
                                                }

                                                notifyDataSetChanged();
                                            }
                                            break;
                                        }
                                        return false;
                                    }
                                });

                                popup.show();
                                return true;
                            }

                            case R.id.binoculars: {

                                PopupMenu popup = new PopupMenu(mContext, holder.btnViewOption);
                                popup.inflate(R.menu.viewtype_menu);

                                switch (screenParent){
                                    case "ALLFILE":{
                                        final AllFileActivity allFileActivity = (AllFileActivity) mContext;
                                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                            @Override
                                            public boolean onMenuItemClick(MenuItem menuItem) {
                                                switch (menuItem.getItemId()) {
                                                    case R.id.list: {
                                                        allFileActivity.changeViewType(0);
                                                    }
                                                    break;
                                                    case R.id.grid: {
                                                        allFileActivity.changeViewType(1);
                                                    }
                                                    break;
                                                }
                                                return false;
                                            }
                                        });
                                        break;
                                    }
                                    case "FAVOURITE":{
                                        final FavouriteActivity favouriteActivity = (FavouriteActivity) mContext;
                                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                            @Override
                                            public boolean onMenuItemClick(MenuItem menuItem) {
                                                switch (menuItem.getItemId()) {
                                                    case R.id.list: {
                                                        favouriteActivity.changeViewType(0);
                                                    }
                                                    break;
                                                    case R.id.grid: {
                                                        favouriteActivity.changeViewType(1);
                                                    }
                                                    break;
                                                }
                                                return false;
                                            }
                                        });
                                        break;
                                    }
                                    case "RECENT":{
                                        final RecentActivity recentActivity = (RecentActivity) mContext;
                                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                            @Override
                                            public boolean onMenuItemClick(MenuItem menuItem) {
                                                switch (menuItem.getItemId()) {
                                                    case R.id.list: {
                                                        recentActivity.changeViewType(0);
                                                    }
                                                    break;
                                                    case R.id.grid: {
                                                        recentActivity.changeViewType(1);
                                                    }
                                                    break;
                                                }
                                                return false;
                                            }
                                        });
                                        break;
                                    }
                                }

                                popup.show();
                                return true;
                            }
                            case R.id.share:
                                shareFileDialog(holder.getPosition());
                                return true;
                            case R.id.remove:
                                switch (screenParent){
                                    case "ALLFILE":{
                                        deleteFileDialog(position);
                                        break;
                                    }
                                    case "FAVOURITE":{
                                        removeFavouriteFileDialog(position);
                                        break;
                                    }
                                    case "RECENT":{
                                        removeRecentFileDialog(position);
                                        break;
                                    }
                                }
                                return true;
                            default:
                                return false;
                        }
                    }

                    @Override
                    public void onMenuModeChange(MenuBuilder menu) {
                    }
                });

                optionsMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return pdfDocuments.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<PDFDoc> filteredDocs = new ArrayList<>();

            if (constraint.toString().isEmpty()) {
                filteredDocs=pdfDocumentsAll;
            } else {

                for (PDFDoc pdfDoc : pdfDocumentsAll) {
                    if (pdfDoc.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredDocs.add(pdfDoc);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredDocs;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
//            pdfDocuments.clear();
            pdfDocuments= (ArrayList<PDFDoc>) results.values;
            notifyDataSetChanged();
        }
    };

    public static class PdfViewHolder extends EmptyRecyclerView.ViewHolder {

        protected TextView fileName;
        protected TextView fileSize;
        protected TextView fileLastModified;
        protected View card_view;
        public ImageView btnViewOption;

        public PdfViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_name);
            fileSize = itemView.findViewById(R.id.file_size);
            fileLastModified = itemView.findViewById(R.id.file_lastmodified);
            card_view = itemView.findViewById(R.id.card_view);
            btnViewOption = itemView.findViewById(R.id.textViewOptions);
        }
    }

    private void OpenPdf(PDFDoc pdfDoc) {
        Intent i = new Intent(mContext, PDFActivity.class);
        i.putExtra("PDF", pdfDoc);
        mContext.startActivity(i);
    }

    private void shareFileDialog(int position) {
        Intent sharedIntent = new Intent();
        sharedIntent.setAction(Intent.ACTION_SEND);
        sharedIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", new File(getItem(position).getPath())));
        sharedIntent.setType("application/pdf");
        mContext.startActivity(Intent.createChooser(sharedIntent, "Send to"));
    }

    private void renameFileDialog(final int position) {
        AlertDialog.Builder renameFileBuilder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_rename_file, null);

        final EditText input = view.findViewById(R.id.new_name);

        renameFileBuilder.setTitle("Rename File");
        renameFileBuilder.setCancelable(true);
        renameFileBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    String value = input.getText().toString().trim() + ".pdf";
                    rename(position, value);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "exception", e);
                }

                dialog.cancel();
            }
        });
        renameFileBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        renameFileBuilder.setView(view);
        AlertDialog alert = renameFileBuilder.create();
        alert.show();
    }

    private void rename(int position, String name) {

        String currentFilePath = getItem(position).getPath();
        String dirSub = currentFilePath.substring(currentFilePath.lastIndexOf("/"), currentFilePath.length());

        //String dirPath = dirSub.substring(1);

        File file = new File(currentFilePath);

        try {

            String currentName = dirSub.substring(1);
            File directory = file.getParentFile();

            File from = new File(directory, currentName);
            File to = new File(directory, name);
            boolean success = from.renameTo(to);

            //File oldFilePath = new File(getItem(position).getmFilePath());
            //oldFilePath.renameTo(file);
            //mDatabase.renameItem(getItem(position),name,mFilePath);
            notifyItemChanged(position);
        } catch (Exception e) {

        }


        //String mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //mFilePath += name;
        //String curentdir = "/" + name;

        //File file = new File(curentdir);

        //if (file.exists() && !file.isDirectory()){
        //Toast.makeText(mContext,String.format("The file %1$s already exists. Please choose a different...",name),Toast.LENGTH_SHORT).show();
        //}
        //else {


        //}
    }

    private void deleteFileDialog(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        View alert = LayoutInflater.from(mContext).inflate(R.layout.dialog_yesno,null);
        builder.setView(alert);

        TextView textViewAlert = alert.findViewById(R.id.content);
        TextView btnNo = alert.findViewById(R.id.btnNo);
        TextView btnYes = alert.findViewById(R.id.btnYes);

        textViewAlert.setText("Are you sure\nyou want to remove this File?");

        final AlertDialog dialog = builder.create();

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                try {
                    remove(position);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "exception", e);
                }
            }
        });

        dialog.show();
    }

    private void removeFavouriteFileDialog(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        View alert = LayoutInflater.from(mContext).inflate(R.layout.dialog_yesno,null);
        builder.setView(alert);

        TextView textViewAlert = alert.findViewById(R.id.content);
        TextView btnNo = alert.findViewById(R.id.btnNo);
        TextView btnYes = alert.findViewById(R.id.btnYes);

        textViewAlert.setText("Are you sure\nyou want to remove this File?");

        final AlertDialog dialog = builder.create();

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                pdfDocuments.get(position).setFavourite(!pdfDocuments.get(position).isFavourite());
                databaseHelper.updatePDFDoc(pdfDocuments.get(position));
                pdfDocuments.remove(position);
                pdfDocumentsAll = new ArrayList<>(pdfDocuments);
                notifyDataSetChanged();
            }
        });

        dialog.show();
    }

    private void removeRecentFileDialog(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        View alert = LayoutInflater.from(mContext).inflate(R.layout.dialog_yesno,null);
        builder.setView(alert);

        TextView textViewAlert = alert.findViewById(R.id.content);
        TextView btnNo = alert.findViewById(R.id.btnNo);
        TextView btnYes = alert.findViewById(R.id.btnYes);

        textViewAlert.setText("Are you sure\nyou want to remove this File?");

        final AlertDialog dialog = builder.create();

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                pdfDocuments.get(position).setOpenedTime(null);
                databaseHelper.updatePDFDoc(pdfDocuments.get(position));
                pdfDocuments.remove(position);
                pdfDocumentsAll = new ArrayList<>(pdfDocuments);
                notifyDataSetChanged();
            }
        });

        dialog.show();
    }

    private void remove(int position) {
        File fdelete = new File(pdfDocuments.get(position).getPath());
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Toast.makeText(mContext, "File Deleted : " + pdfDocuments.get(position).getName(), Toast.LENGTH_SHORT).show();
                databaseHelper.deletePDFDoc(pdfDocuments.get(position));
                pdfDocuments.remove(position);
                pdfDocumentsAll = new ArrayList<>(pdfDocuments);
                notifyDataSetChanged();
            } else {
                Toast.makeText(mContext, "File not Deleted : " + pdfDocuments.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public PDFDoc getItem(int position) {
        return pdfDocuments.get(position);
    }

    public String GetFileSize(double length) {

        DecimalFormat format = new DecimalFormat("#.##");
        long MiB = 1024 * 1024;
        long KiB = 1024;

        String fileSize = "";

        if (length > MiB) {
            fileSize = format.format(length / MiB) + " Mb";
        } else if (length > KiB) {
            fileSize = format.format(length / KiB) + " Kb";
        } else {
            fileSize = format.format(length / KiB) + " B";
        }

        return fileSize;
    }

    public String convertDateToString(Date date){
        String pattern = "HH:mm:ss dd/MM/yyyy";
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }
}

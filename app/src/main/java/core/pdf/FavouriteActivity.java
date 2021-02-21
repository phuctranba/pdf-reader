package core.pdf;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.core.pdf.reader.R;

import java.util.ArrayList;
import java.util.Collections;

public class FavouriteActivity extends AppCompatActivity {

    private PdfRecyclerAdapter mPdfViewAdapter;
    ArrayList<PDFDoc> pdfDocmnts = new ArrayList<>();
    EmptyRecyclerView mRecyclerView;
    DatabaseHelper databaseHelper;
    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;
    public int typeSort;
    public boolean typeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        typeSort = MySharedPreferences.getPrefSortFavourite(this);
        typeView = MySharedPreferences.getPrefViewFavourite(this);

        init();

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle("Favourite");
    }

    @Override
    protected void onStart() {
        pdfDocmnts.clear();
        pdfDocmnts.addAll(databaseHelper.getAllPDFDocFavourite());
        setupGUI();
        mPdfViewAdapter.notifyDataSetChanged();
        super.onStart();
    }

    private void init() {
        mRecyclerView = findViewById(R.id.all_files_recycler);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        gridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(typeView?linearLayoutManager:gridLayoutManager);

        databaseHelper = new DatabaseHelper(this);
        pdfDocmnts = databaseHelper.getAllPDFDocFavourite();

        setupGUI();

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        mPdfViewAdapter = new PdfRecyclerAdapter(this, pdfDocmnts,"FAVOURITE", typeView, typeSort);
        mRecyclerView.setAdapter(mPdfViewAdapter);
        mRecyclerView.setEmptyView(findViewById(R.id.emptyView));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_clear, menu);

        MenuItem itemSearch = menu.findItem(R.id.action_search);
        MenuItem itemClear = menu.findItem(R.id.action_clear);

        SearchView searchView = (SearchView) itemSearch.getActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mPdfViewAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mPdfViewAdapter.getFilter().filter(newText);
                return false;
            }
        });


        itemClear.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(FavouriteActivity.this);

                View alert = LayoutInflater.from(FavouriteActivity.this).inflate(R.layout.dialog_yesno,null);
                builder.setView(alert);

                TextView textViewAlert = alert.findViewById(R.id.content);
                TextView btnNo = alert.findViewById(R.id.btnNo);
                TextView btnYes = alert.findViewById(R.id.btnYes);

                textViewAlert.setText("Are you sure\nyou want to clear the Favourite?");

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
                        databaseHelper.clearPDFDocFavourite();
                        pdfDocmnts.clear();
                        mPdfViewAdapter.notifyDataSetChanged();
                    }
                });

                dialog.show();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeViewType(int type){
        mPdfViewAdapter.setViewType(type);
        mRecyclerView.setLayoutManager(type==0?linearLayoutManager:gridLayoutManager);
        mRecyclerView.setAdapter(mPdfViewAdapter);
        MySharedPreferences.setPrefViewFavourite(this,type==0);
    }

    void setupGUI(){
        switch (typeSort){
            case 0:
            case 1:
                Collections.sort(pdfDocmnts,PDFDoc.sortNameIncrease);
                break;
            case 2:
                Collections.sort(pdfDocmnts,PDFDoc.sortNameDecrease);
                break;
            case 3:
                Collections.sort(pdfDocmnts,PDFDoc.sortTimeIncrease);
                break;
            case 4:
                Collections.sort(pdfDocmnts,PDFDoc.sortTimeDecrease);
                break;
            case 5:
                Collections.sort(pdfDocmnts,PDFDoc.sortSizeIncrease);
                break;
            case 6:
                Collections.sort(pdfDocmnts,PDFDoc.sortSizeDecrease);
                break;
        }
    }
}
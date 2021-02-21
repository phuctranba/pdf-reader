package core.pdf;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.core.pdf.reader.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.io.File;

//import com.github.barteksc.pdfviewer.ScrollBar;

public class PDFActivity extends AppCompatActivity {
    boolean isNightMode;
    boolean isHorizontalScroll;
    PDFDoc pdfDoc;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pdf);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        databaseHelper = new DatabaseHelper(this);

        final PDFView pdfView = findViewById(R.id.pdfView);

        pdfView.useBestQuality(true);
        pdfView.setPadding(0, 0, 0, 0);

        isNightMode = MySharedPreferences.getPrefNightMode(PDFActivity.this);
        isHorizontalScroll = MySharedPreferences.getPrefHorizontalScroll(PDFActivity.this);

        Intent intent = this.getIntent();

        Bundle bundle = intent.getExtras();

        if (intent != null && bundle != null) {
            pdfDoc = (PDFDoc) intent.getSerializableExtra("PDF");
            setTitle(pdfDoc.getName());
            File file = new File(pdfDoc.getPath());

            if (file.canRead()) {
                pdfView.fromFile(file).defaultPage(0).spacing(0).nightMode(isNightMode).enableSwipe(true).swipeHorizontal(isHorizontalScroll).pageSnap(true).enableAnnotationRendering(true).autoSpacing(false).pageFling(true)
                        .enableDoubletap(true).autoSpacing(true).pageFitPolicy(FitPolicy.BOTH).scrollHandle(new DefaultScrollHandle(this)).load();
            }
        } else {

            String action = intent.getAction();

            if (Intent.ACTION_VIEW.equals(action)) {

                final Uri uri = intent.getData();

                if (uri != null) {
                    OpenPdf(pdfView, uri);
                }

            }
        }
    }
    private void OpenPdf(PDFView pdfView, Uri uri) {
        pdfView.fromUri(uri).defaultPage(0).spacing(0).nightMode(isNightMode).enableSwipe(true).swipeHorizontal(isHorizontalScroll).pageSnap(true).enableAnnotationRendering(true).autoSpacing(false).pageFling(true)
                .enableDoubletap(true).autoSpacing(true).pageFitPolicy(FitPolicy.BOTH).scrollHandle(new DefaultScrollHandle(this)).load();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu_viewpdf, menu);

        MenuItem itemFavourite = menu.findItem(R.id.favourite);

        itemFavourite.setTitle(pdfDoc.isFavourite()?"UnFavourite":"Add to Favourite");
        itemFavourite.setIcon(pdfDoc.isFavourite()?R.drawable.ic_star_yellow:R.drawable.ic_star_white);

        return super.onCreateOptionsMenu(menu);
    }

    private void shareFileDialog() {
        Intent sharedIntent = new Intent();
        sharedIntent.setAction(Intent.ACTION_SEND);
        sharedIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", new File(pdfDoc.getPath())));
        sharedIntent.setType("application/pdf");
        startActivity(Intent.createChooser(sharedIntent, "Send to"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.favourite:{
                pdfDoc.setFavourite(!pdfDoc.isFavourite());
                databaseHelper.updatePDFDoc(pdfDoc);
                item.setTitle(pdfDoc.isFavourite()?"UnFavourite":" Add to Favourite");
                item.setIcon(pdfDoc.isFavourite()?R.drawable.ic_star_yellow:R.drawable.ic_star_white);
                return true;
            }
            case R.id.share:{
                shareFileDialog();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}


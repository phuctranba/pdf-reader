package core.pdf;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.core.pdf.reader.R;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static core.pdf.PermissionUtil.PERMISSION_ALL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout btnAllfile, btnRecent, btnFavourite, btnSettings;
    ProgressDialog progressDialog;
    DatabaseHelper databaseHelper;
    List<PDFDoc> pdfDocList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        init();

        checkPermissions();

//        Search_Dir(Environment.getExternalStorageDirectory());

    }

    void init (){
        btnAllfile = findViewById(R.id.btnAllFile);
        btnAllfile.setOnClickListener(this);
        btnFavourite = findViewById(R.id.btnFavourite);
        btnFavourite.setOnClickListener(this);
        btnRecent = findViewById(R.id.btnRecent);
        btnRecent.setOnClickListener(this);
        btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(this);

        databaseHelper = new DatabaseHelper(this);

        pdfDocList = new ArrayList<>();
    }


    public void Search_Dir(File dir) {
        String pdfPattern = ".pdf";
        Log.e("Search_Dir", dir.getAbsolutePath());

        File FileList[] = dir.listFiles();

        if (FileList != null) {
            for (int i = 0; i < FileList.length; i++) {

                if (FileList[i].isDirectory()) {
                    Search_Dir(FileList[i]);
                } else {
                    Log.e("Search_Dir", FileList[i].getName());

                    if (FileList[i].getName().endsWith(pdfPattern)) {
                        //here you have that file.


                    }
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAllFile:
                startActivity(new Intent(this,AllFileActivity.class));
                break;
            case R.id.btnFavourite:
                startActivity(new Intent(this,FavouriteActivity.class));
                break;
            case R.id.btnRecent:
                startActivity(new Intent(this,RecentActivity.class));
                break;
            case R.id.btnSettings:
                startActivity(new Intent(this,SettingsActivity.class));
                break;
        }
    }

    private void checkPermissions() {
        try {

            String[] permissions = PermissionUtil.getPermissions(this);
            if (!PermissionUtil.checkPermissions(this, permissions)) {
                PermissionUtil.askPermissions(this);
                Log.e("checkPermissions", "tren");
            } else {
                Log.e("checkPermissions", "duoi");
                getDatas();
            }
        } catch (Exception e) {

        }
    }

    public String GetFileName(File file) {
        String filename = "";

        try {
            if (file != null && file.exists()) {
                String name = file.getName();
                filename = name.replaceFirst("[.][^.]+$", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            filename = file.getName();
        }

        return filename;
    }

    public String GetFileSize(File file) {

        DecimalFormat format = new DecimalFormat("#.##");
        long MiB = 1024 * 1024;
        long KiB = 1024;

        if (!file.isFile()) {
            throw new IllegalArgumentException("Expected a file");
        }

        String fileSize = "";

        //long fileSizeInBytes = file.length();
        //long fileSizeInKB = fileSizeInBytes / 1024;
        //long fileSizeInMB = fileSizeInKB / 1024;
        //long fileSizeInGB = fileSizeInMB / 1024;
        //long fileSizeInTB= fileSizeInGB / 1024;

        final double length = file.length();

        if (length > MiB) {
            fileSize = format.format(length / MiB) + " Mb";
        } else if (length > KiB) {
            fileSize = format.format(length / KiB) + " Kb";
        } else {
            fileSize = format.format(length / KiB) + " B";
        }
        return String.valueOf(length);

    }

    public void walkdir(  File dir) {

        Log.e("walkdir", dir.getAbsolutePath());
        try {
            String pdfPattern = ".pdf";
            File listFile[] = dir.listFiles();
            PDFDoc pdfDoc;
            if (listFile != null) {
                for (int i = 0; i < listFile.length; i++) {
                    if (listFile[i].isDirectory()) {
                        walkdir(listFile[i]);
                    } else {
                        File file = listFile[i];


                        if (file.getName().endsWith(pdfPattern)) {

                            //BasicFileAttributes fileAttributes = Files.readAttributes(file,)

                            pdfDoc = new PDFDoc();
                            pdfDoc.setId(UUID.randomUUID().toString());
                            pdfDoc.setName(GetFileName(file));
                            pdfDoc.setPath(file.getAbsolutePath());
                            pdfDoc.setSize(GetFileSize(file));
                            pdfDoc.setFavourite(false);
                            pdfDoc.setLastModified(new Date(file.lastModified()));
                            pdfDocList.add(pdfDoc);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("walkdir", "loi roi");
            Log.e("walkdir", e.getMessage());

        }
    }

    private void getDatas() {
        progressDialog = new ProgressDialog(this);

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                pdfDocList.clear();
                walkdir(Environment.getExternalStorageDirectory());
                databaseHelper.loadAllPDFDoc(pdfDocList);
                return null;
            }

            @Override
            protected void onPreExecute() {
                progressDialog.setMessage("Loadding, please wait.");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
//                initList();
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        }.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL : {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermissions();
                } else {
                    Toast.makeText(getApplicationContext(), "Permissions are required for the app", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}

package com.android.devthien.dailyselfie;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.devthien.dailyselfie.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private AdapterListView adapterListView;
    private ActivityMainBinding binding;

    private List<ImageModel> imageModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_baseline_photo_library_24);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.button_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btnOpenCamera) {
            launchCamera.launch(Manifest.permission.CAMERA);
        }
        return super.onOptionsItemSelected(item);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        try {
                            saveToGallery(photo);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });

    ActivityResultLauncher<String> launchCamera =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(), isGranted -> {
                        if (isGranted) {
                            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            activityResultLauncher.launch(camera);
                        } else {

                        }
                    });

    private void init() {
        imageModelList = new ArrayList<>();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                2 * 60 * 1000,
                pendingIntent);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//        }
        loadData();
        listView = findViewById(R.id.list_item_image);
        adapterListView = new AdapterListView(imageModelList, MainActivity.this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showDetailPhoto(i);
            }
        });
        listView.setAdapter(adapterListView);
    }

    private void saveToGallery(Bitmap photo) throws IOException {
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        File dir = new File(path + "/DailySelfie");
        dir.mkdir();
        File file = new File(path + "/DailySelfie", System.currentTimeMillis() + ".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        fOut = new FileOutputStream(file);

        Bitmap pictureBitmap = photo; // obtaining the Bitmap
        pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
        fOut.flush(); // Not really required
        fOut.close(); // do not forget to close the stream

        MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        ImageModel imageModel = new ImageModel(photo, System.currentTimeMillis() + ".jpg", getDate(System.currentTimeMillis(), "dd/MM/yyyy hh:mm"));
        imageModelList.add(imageModel);
        adapterListView.notifyDataSetChanged();
    }

    private void loadData() {
        File folder = new File(Environment.getExternalStorageDirectory().toString() + "/DailySelfie/");
        File[] allFiles = {};
        if (folder.exists()) {
            allFiles = folder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return (name.endsWith(".jpeg") || name.endsWith(".jpg"));
                }
            });
        }
        for (File file : allFiles) {
            Log.d(TAG, "loadData: ");
            ImageModel imageModel = new ImageModel(BitmapFactory.decodeFile(file.getPath()), file.getName(), getDate(file.lastModified(), "dd/MM/yyyy hh:mm"));
            imageModelList.add(imageModel);
        }
    }

    private void showDetailPhoto(int i) {
        Intent intent = new Intent(MainActivity.this, ImageViewActivity.class);
        intent.putExtra("bitmap", imageModelList.get(i).photo);
        startActivity(intent);
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

}
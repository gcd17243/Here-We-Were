package com.example.herewewere.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.herewewere.R;
import com.example.herewewere.databases.MyNoteDbManager;
import com.example.herewewere.models.MyNote;
import com.example.herewewere.preferences.MyPreferences;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class CreateOrShowNoteActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private MyNoteDbManager myNoteDbManager;
    private MyPreferences myPreferences;

    private EditText editTitle, editNote,editLatid,editLongid;
    private BottomSheetBehavior bottomSheetBehavior;
    private TextView editedDateText;
    private ImageView noteImage;
    private LinearLayout linearLayoutET, bottomSheetLinearLayout, deleteNoteLL, makeACopyLL, sendNoteLL, takePhotoLL, chooseImageLL,getlocation;
    private GoogleMap mMap;

    private final static int REQUEST_CAMERA_CODE = 121;
    private final static int REQUEST_CHOOSE_IMAGE_CODE = 122;

    private boolean isUndoClicked, isImageChanged;
    private String showNoteKey, title, note, imagePath = null,latid,longid;
    private int id;
    FusedLocationProviderClient client;


    private final static int PLACE_PICKER_REQUEST = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_show_note);

        //default status bar and action bar changed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorCreateStatusBar));
        }
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorCreateActionBar)));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_back_black_24dp);

        editTitle = findViewById(R.id.editTitle);
        editNote = findViewById(R.id.editNote);
        editLatid = findViewById(R.id.editLatid);
        editLongid = findViewById(R.id.editLongid);
        editedDateText = findViewById(R.id.editedDate);
        noteImage = findViewById(R.id.noteImage);
        linearLayoutET = findViewById(R.id.linearLayoutEt);


        deleteNoteLL = findViewById(R.id.deleteNoteLL);
        makeACopyLL = findViewById(R.id.makeACopyLL);
        sendNoteLL = findViewById(R.id.sendNoteLL);
        takePhotoLL = findViewById(R.id.takePhotoLL);
        chooseImageLL = findViewById(R.id.chooseImageLL);
        getlocation = findViewById(R.id.getlocation);

        myNoteDbManager = new MyNoteDbManager(this);
        myPreferences = MyPreferences.getMyPreferences(this);
        setEditTextFontSize(myPreferences.getFontSize());

        deleteNoteLL.setOnClickListener(this);
        makeACopyLL.setOnClickListener(this);
        sendNoteLL.setOnClickListener(this);
        takePhotoLL.setOnClickListener(this);
        chooseImageLL.setOnClickListener(this);
        getlocation.setOnClickListener(this);

        client = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapadd);
        mapFragment.getMapAsync(this);

        createOrDisplaySavedData();

        if (savedInstanceState != null) {
            String imageSavedInstance = savedInstanceState.getString("imageSavedInstance");

            if (imageSavedInstance != null) {
                noteImage.setVisibility(View.VISIBLE);
                //Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                Glide.with(this)
                        .load(imageSavedInstance)
                        .into(noteImage);

                imagePath = imageSavedInstance;
                isImageChanged = true;
            }
        }
    }

    private void createOrDisplaySavedData() {
        LinearLayout bottomSheetMoreLayout = findViewById(R.id.bottom_sheet_more_layout);
        LinearLayout bottomSheetPlusLayout = findViewById(R.id.bottom_sheet_plus_layout);



        showNoteKey = getIntent().getStringExtra("our_note_key");
        String myDate;
        if (showNoteKey.equals("show_note_here")) {
            deleteNoteLL.setVisibility(View.VISIBLE);

            MyNote myNote = myNoteDbManager.getSingleNote(getIntent().getIntExtra("id", 0));
            id = myNote.getId();
            title = myNote.getTitle();
            note = myNote.getNote();
            myDate = myNote.getDate();
            imagePath = myNote.getImagePath();
            latid = myNote.getLatid();
            longid = myNote.getLongid();


            editTitle.setText(title);
            editNote.setText(note);
            editLatid.setText(latid);
            editLongid.setText(longid);
            editedDateText.setText(myDate);

            if (imagePath != null) {
                noteImage.setVisibility(View.VISIBLE);
                //Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                Glide.with(this)
                        .load(imagePath)
                        .into(noteImage);
            }

            editTitle.setSelection(editTitle.getText().length());
            editNote.setSelection(editNote.getText().length());

            editTitle.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            editTitle.setRawInputType(InputType.TYPE_CLASS_TEXT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                editTitle.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
                editNote.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
            }

            linearLayoutET.setFocusable(true);
            linearLayoutET.setFocusableInTouchMode(true);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        } else if (showNoteKey.equals("create_note_here")) {
            makeACopyLL.setVisibility(View.GONE);
            editTitle.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            editTitle.setRawInputType(InputType.TYPE_CLASS_TEXT);

            myDate = "Edited: " + getCurrentDateAndTime();
            editedDateText.setText(myDate);
            editTitle.requestFocus();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        editTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    closeMoreBottomSheetAndKeyboard();
                    closePlusBottomSheetAndKeyboard();
                }
            }
        });

        editNote.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    closeMoreBottomSheetAndKeyboard();
                    closePlusBottomSheetAndKeyboard();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        savedOrUpdateNote();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        savedOrUpdateNote();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        super.onBackPressed();
    }

    private void savedOrUpdateNote() {
        String currentTitle = editTitle.getText().toString();
        String currentNote = editNote.getText().toString();
        String currentLatid = editLatid.getText().toString();
        String currentLongid = editLongid.getText().toString();

        if (id > 0) {
            if (!title.equals(currentTitle) || !note.equals(currentNote) || isImageChanged || !latid.equals(currentLatid) || !longid.equals(currentLongid)) {
                MyNote myNote = new MyNote(id, currentTitle, currentNote, getCurrentDateAndTime(), imagePath,currentLatid,currentLongid);
                long isUpdate = myNoteDbManager.updateNote(myNote);

                if (isUpdate <= 0) {
                    Toast.makeText(this, "Note not updated", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            if (!currentTitle.trim().isEmpty() || !currentNote.trim().isEmpty() || isImageChanged || !currentLatid.trim().isEmpty() || !currentLongid.trim().isEmpty()  ) {
                MyNote myNote = new MyNote(currentTitle, currentNote, getCurrentDateAndTime(), imagePath,currentLatid,currentLongid);
                long isInsert = myNoteDbManager.addNote(myNote);

                if (isInsert <= 0) {
                    Toast.makeText(this, "Note not created", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pin_archive_menu, menu);
        if (showNoteKey.equals("show_note_here")) {
            menu.findItem(R.id.archiveMenu).setVisible(true);
            menu.findItem(R.id.pinMenu).setVisible(true);
            menu.findItem(R.id.pinMenu).setVisible(true);
            menu.findItem(R.id.archiveMenu).setVisible(true);
            menu.findItem(R.id.resetMenu).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.resetMenu:
                resetNote();
                break;
            case R.id.pinMenu:
                break;

            case R.id.archiveMenu:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetNote() {
        editTitle.setText("");
        editNote.setText("");
        editLatid.setText("");
        editLongid.setText("");
    }

    private void deleteNote() {
        title = editTitle.getText().toString();
        note = editNote.getText().toString();
        latid = editLatid.getText().toString();
        longid = editLongid.getText().toString();

        editTitle.setText("");
        editNote.setText("");
        editLatid.setText("");
        editLongid.setText("");
        noteImage.setVisibility(View.GONE);
        if (id > 0) {
            myNoteDbManager.deleteNote(id);
            Toast.makeText(CreateOrShowNoteActivity.this, "Note delete.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(CreateOrShowNoteActivity.this, "Note not delete.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCurrentDateAndTime() {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    public void moreHorizontalView(View view) {
        //close previous bottom sheet and soft key board
        closePlusBottomSheetAndKeyboard();

        //start the current/desire bottom sheet
        bottomSheetLinearLayout = findViewById(R.id.bottom_sheet_more_layout);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLinearLayout);

        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED || bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void plusHorizontalView(View view) {
        //close previous bottom sheet and soft key board
        closeMoreBottomSheetAndKeyboard();

        //start the current/desire bottom sheet
        bottomSheetLinearLayout = findViewById(R.id.bottom_sheet_plus_layout);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLinearLayout);

        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED || bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void setEditTextFontSize(int fontSize) {
        if (fontSize == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                editTitle.setTextAppearance(android.R.style.TextAppearance_Small);
                editNote.setTextAppearance(android.R.style.TextAppearance_Small);
            } else {
                editTitle.setTextAppearance(this, android.R.style.TextAppearance_Small);
                editNote.setTextAppearance(this, android.R.style.TextAppearance_Small);
            }
        } else if (fontSize == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                editTitle.setTextAppearance(android.R.style.TextAppearance_Medium);
                editNote.setTextAppearance(android.R.style.TextAppearance_Medium);
            } else {
                editTitle.setTextAppearance(this, android.R.style.TextAppearance_Medium);
                editNote.setTextAppearance(this, android.R.style.TextAppearance_Medium);
            }
        } else if (fontSize == 2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                editTitle.setTextAppearance(android.R.style.TextAppearance_Large);
                editNote.setTextAppearance(android.R.style.TextAppearance_Large);
            } else {
                editTitle.setTextAppearance(this, android.R.style.TextAppearance_Large);
                editNote.setTextAppearance(this, android.R.style.TextAppearance_Large);
            }
        }
        editTitle.setTypeface(editTitle.getTypeface(), Typeface.BOLD);
    }

    private void closeMoreBottomSheetAndKeyboard() {
        bottomSheetLinearLayout = findViewById(R.id.bottom_sheet_more_layout);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLinearLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

    }

    private void closePlusBottomSheetAndKeyboard() {
        bottomSheetLinearLayout = findViewById(R.id.bottom_sheet_plus_layout);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLinearLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.deleteNoteLL:
                deleteNote();
                closeMoreBottomSheetAndKeyboard();
                break;

            case R.id.makeACopyLL:
                makeACopyOfNote();
                closeMoreBottomSheetAndKeyboard();
                break;

            case R.id.sendNoteLL:
                shareNoteToOtherClientApp();
                closeMoreBottomSheetAndKeyboard();
                break;

            case R.id.chooseImageLL:
                chooseImagePermission();
                closePlusBottomSheetAndKeyboard();
                break;

            case R.id.takePhotoLL:
                takePhotoPermission();
                closePlusBottomSheetAndKeyboard();
                break;

            case R.id.getlocation:
                getCurrentLocation();
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(CreateOrShowNoteActivity.this),PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;

        }
    }




    private void shareNoteToOtherClientApp() {
        if (myPreferences.getDisableShareNote().equals("disable")) {
            Toast.makeText(this, "Please, enable your note sharing option from settings", Toast.LENGTH_LONG).show();

        } else {
            if (!editTitle.getText().toString().trim().isEmpty() || !editNote.getText().toString().trim().isEmpty()|| !editLatid.getText().toString().trim().isEmpty()|| !editLongid.getText().toString().trim().isEmpty()) {
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, editTitle.getText().toString());
                intent.putExtra(android.content.Intent.EXTRA_TEXT, editNote.getText().toString());
                startActivity(android.content.Intent.createChooser(intent, "Send note"));
            } else {
                Toast.makeText(this, "Empty notes can not be shared. Insert title or body", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void makeACopyOfNote() {
        if (!editTitle.getText().toString().trim().isEmpty() || !editNote.getText().toString().trim().isEmpty() || imagePath != null|| !editLatid.getText().toString().trim().isEmpty()|| !editLongid.getText().toString().trim().isEmpty()) {
            MyNote myNote = new MyNote(editTitle.getText().toString(), editNote.getText().toString(), getCurrentDateAndTime(), imagePath,editLatid.getText().toString(),editLongid.getText().toString());
            long insert = myNoteDbManager.addNote(myNote);

            if (insert > 0) {
                savedOrUpdateNote();

                Intent intent = new Intent(this, CreateOrShowNoteActivity.class);
                intent.putExtra("our_note_key", "show_note_here");
                intent.putExtra("id", id);
                myNote.setDate(getCurrentDateAndTime());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        } else {
            Toast.makeText(this, "Copy of empty note can not be made", Toast.LENGTH_SHORT).show();
        }
    }

    private void chooseImagePermission() {
        isImageChanged = false;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CHOOSE_IMAGE_CODE);
        } else {
            chooseImageForNote();
        }
    }

    private void takePhotoPermission() {
        isImageChanged = false;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);
        } else {
            takePhotoForNote();
        }
    }

    private void chooseImageForNote() {
        Intent chooseImageIntent = new Intent(Intent.ACTION_PICK);
        chooseImageIntent.setType("image/*");
        startActivityForResult(chooseImageIntent, REQUEST_CHOOSE_IMAGE_CODE);
    }

    private void takePhotoForNote() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePhotoIntent, REQUEST_CAMERA_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CHOOSE_IMAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseImageForNote();
            } else {
                Toast.makeText(this, "Need read permission of external storage to select an image.", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == REQUEST_CAMERA_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhotoForNote();
            } else {
                Toast.makeText(this, "Need camera permission to take a photo.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CHOOSE_IMAGE_CODE && data != null) {
                Uri uri = data.getData();

                if (uri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        isImageChanged = true;
                        imagePath = saveToInternalStorage(bitmap);

                            noteImage.setVisibility(View.VISIBLE);
                            Glide.with(CreateOrShowNoteActivity.this)
                                    .asBitmap()
                                    .load(bitmap)
                                    .into(noteImage);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                } else if (requestCode == REQUEST_CAMERA_CODE && data != null) {
                    Bundle bundleData = data.getExtras();

                    if (bundleData != null) {
                        Bitmap bitmapData = (Bitmap) bundleData.get("data");

                        if (bitmapData != null) {
                            isImageChanged = true;
                            imagePath = saveToInternalStorage(bitmapData);

                            noteImage.setVisibility(View.VISIBLE);
                            Glide.with(this)
                                    .asBitmap()
                                    .load(bitmapData)
                                    .into(noteImage);
                        }
                    }
                }
            }

        closePlusBottomSheetAndKeyboard();
    }

    private String saveToInternalStorage(Bitmap bitmapData) {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());

        if (id > 0) {
            if (imagePath != null) {
                File currentFilePath = new File(imagePath);

                if (currentFilePath.exists()) {
                    boolean isDeleted = currentFilePath.delete();
                    if (!isDeleted) {
                        Toast.makeText(this, "Previous image can't delete", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        File directory = contextWrapper.getDir("note_image", Context.MODE_PRIVATE);
        File filePath = new File(directory, getImageName());
        String fullImagePath = filePath.toString();

        OutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(fullImagePath);
            bitmapData.compress(Bitmap.CompressFormat.JPEG, 60, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(contextWrapper, e.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fullImagePath;
    }

    private String getImageName() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
        String imageTimeStrap = simpleDateFormat.format(new Date());
        return "image_" + imageTimeStrap + ".jpg";
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (imagePath != null) {
            outState.putString("imageSavedInstance", imagePath);
        }
    }

    private void getCurrentLocation() {
        //Initialize task Location
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            //When success
            public void onSuccess(final Location location) {
                if (location !=null){
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.mapadd);

                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            //Initialize lat Lng
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                            //Create Maker Options
                            String latitude = String.valueOf(location.getLatitude());
                            String longitude = String.valueOf(location.getLongitude());
                            editLatid.setText(latitude);
                            editLongid.setText(longitude);
                            //Zoom Map
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                        }
                    });

                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        double valuelat;
        double valuelong;

        valuelat=0;
        valuelong=0;
        if(latid != null||longid != null){
    try
    {   valuelat = Double.parseDouble(latid);
        valuelong = Double.parseDouble(longid);
        // it means it is double
    } catch (Exception e1) {
        e1.printStackTrace();
    }

}

        LatLng latLng = new LatLng(valuelat, valuelong);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));

        // Add a marker in Sydney and move the camera
        if (imagePath==null){
            mMap.addMarker(new MarkerOptions().position(latLng).title(title));

        }else{
            mMap.addMarker(new MarkerOptions().position(latLng).title(title).icon(BitmapDescriptorFactory.fromPath(imagePath)));
        }

    }
}
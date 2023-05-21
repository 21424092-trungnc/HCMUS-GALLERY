package com.hcmus.project_21424074_21424092_21424094.mainFragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.project_21424074_21424092_21424094.R;
import com.hcmus.project_21424074_21424092_21424094.activities.mainActivities.ItemAlbumActivity;
import com.hcmus.project_21424074_21424092_21424094.activities.subActivities.MultiSelectImage;
import com.hcmus.project_21424074_21424092_21424094.adapters.CategoryAdapter;
import com.hcmus.project_21424074_21424092_21424094.models.Category;
import com.hcmus.project_21424074_21424092_21424094.models.Image;
import com.hcmus.project_21424074_21424092_21424094.utility.GetAllPhotoFromGallery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PhotoFragment extends Fragment {
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private Toolbar toolbar_photo;
    private Boolean flag = false;
    private List<Category> listImg;
    private List<Image> imageList;
    private List<String> listLabel;
    private ArrayList<String> list_searchA;
    private static int REQUEST_CODE_MULTI = 40;

    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        context = view.getContext();
        setUpListLabel(view.getContext());
        recyclerView = view.findViewById(R.id.rcv_category);
        toolbar_photo = view.findViewById(R.id.toolbar_photo);
        toolBarEvents();
        setRyc();
        return view;
    }

    private void setUpListLabel(Context context) {
        list_searchA = new ArrayList<>();
        try {
            listLabel = new ArrayList<>();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("label.txt")));
            String line = "";
            while ((line = reader.readLine()) != null) {
                listLabel.add(line.toUpperCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setRyc() {
        categoryAdapter = new CategoryAdapter(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        categoryAdapter.setData(getListCategory());
        recyclerView.setAdapter(categoryAdapter);

    }

    private void toolBarEvents() {
        toolbar_photo.inflateMenu(R.menu.menu_top);
        toolbar_photo.setTitle(getContext().getResources().getString(R.string.photo));
        toolbar_photo.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.menuSearch:
                        eventSearch(item);
                        break;
                    case R.id.menuCamera:
                        takenImg();
                        break;
                    case R.id.menuSearch_Advanced:
                        actionSearchAdvanced();
                        break;
                    case R.id.duplicateImages:
                        actionDuplicateImage();
                        break;
                    case R.id.menuChoose:
                        Intent intent_mul = new Intent(getContext(), MultiSelectImage.class);
                        startActivityForResult(intent_mul, REQUEST_CODE_MULTI);
                        break;
                }
                return true;
            }
        });
    }

    private void actionDuplicateImage(){
        DupAsyncTask dupAsyncTask = new DupAsyncTask();
        dupAsyncTask.execute();
    }

    public class DupAsyncTask extends AsyncTask<Void, Integer, Void> {
        private ProgressDialog mProgressDialog ;
        List<String> list;
        @Override
        protected Void doInBackground(Void... voids) {
            list = getListImg();
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Intent intent_duplicate = new Intent(getContext(), ItemAlbumActivity.class);
            intent_duplicate.putStringArrayListExtra("data", (ArrayList<String>) list);
            intent_duplicate.putExtra("name", "Duplicate Image");
            intent_duplicate.putExtra("duplicateImg", 2);
            intent_duplicate.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent_duplicate);
            mProgressDialog.cancel();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("Loading, please wait...");
            mProgressDialog.show();
        }

    }

    public ArrayList<String> getListImg(){
        List<Image> imageList = GetAllPhotoFromGallery.getAllImageFromGallery(getContext());
        long hash = 0;
        Map<Long,ArrayList<String>> map = new HashMap<Long,ArrayList<String>>();
        for (Image img: imageList) {
            Bitmap bitmap = BitmapFactory.decodeFile(img.getPath());
            hash = hashBitmap(bitmap);
            if(map.containsKey(hash)){
                map.get(hash).add(img.getPath());
            }else{
                ArrayList<String> list = new ArrayList<>();
                list.add(img.getPath());
                map.put(hash,list);
            }
        }
        ArrayList<String> result = new ArrayList<>();
        Set set = map.keySet();
        for (Object key: set) {
            if(map.get(key).size() >=2){
                result.addAll(map.get(key));
            }
        }
        return result;
    }

    public long hashBitmap(Bitmap bmp){
        long hash = 31;
        for(int x = 1; x <  bmp.getWidth(); x=x*2){
            for (int y = 1; y < bmp.getHeight(); y=y*2){
                hash *= (bmp.getPixel(x,y) + 31);
                hash = hash%1111122233;
            }
        }
        return hash;
    }

    private void eventSearch(@NonNull MenuItem item) {
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(i, i1, i2);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String date = simpleDateFormat.format(calendar.getTime());
                showImageByDate(date);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void showImageByDate(String date) {
        Toast.makeText(getContext(), date, Toast.LENGTH_LONG).show();
        List<Image> imageList = GetAllPhotoFromGallery.getAllImageFromGallery(getContext());
        List<Image> listImageSearch = new ArrayList<>();

        for (Image image : imageList) {
            if (image.getDateTaken().contains(date)) {
                listImageSearch.add(image);
            }
        }

        if (listImageSearch.size() == 0) {
            Toast.makeText(getContext(), "Không tìm thấy hình ảnh", Toast.LENGTH_LONG).show();
        } else {
            ArrayList<String> listStringImage = new ArrayList<>();
            for (Image image : listImageSearch) {
                listStringImage.add(image.getPath());
            }
            Intent intent = new Intent(context, ItemAlbumActivity.class);
            intent.putStringArrayListExtra("data", listStringImage);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
    private void actionSearchAdvanced() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        View view = getLayoutInflater().inflate(R.layout.layout_dialog_search_advanced, null);

        dialog.setView(view);
        dialog.setTitle("Tìm kiếm nâng cao");
        dialog.setPositiveButton("Tìm kiếm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText edt_search_view = view.findViewById(R.id.edt_search_view);
            }
        });
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
    }

    @Override
    public void onStop() {
        super.onStop();
        flag = true;
    }

    //Camera
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int PICTURE_RESULT = 1;
    private Uri imageUri;
    private String imageurl;
    private Bitmap thumbnail;

    private void takenImg() {
        int permissionCheckStorage = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA);
        if (permissionCheckStorage != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        } else {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Ảnh mới");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Từ máy ảnh");
            imageUri = getActivity().getApplicationContext().getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, PICTURE_RESULT);
            // TODO Simply append one image to the allImages list. No need to loop through it.
            GetAllPhotoFromGallery.updateNewImages();
            GetAllPhotoFromGallery.refreshAllImages();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Máy ảnh không được cấp quyền", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(getActivity(), "Máy ảnh bị chặn truy cập quyền", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case PICTURE_RESULT:
                if (requestCode == PICTURE_RESULT) {
                    if (resultCode == Activity.RESULT_OK) {
                        try {
                            thumbnail = MediaStore.Images.Media.getBitmap(
                                    getActivity().getApplicationContext().getContentResolver(), imageUri);

                            imageurl = getRealPathFromURI(imageUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_MULTI) {
            MyAsyncTask myAsyncTask = new MyAsyncTask();
            myAsyncTask.execute();
            Toast.makeText(context, "Hình ảnh của bạn đã được ẩn", Toast.LENGTH_SHORT).show();
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @NonNull
    private List<Category> getListCategory() {
        List<Category> categoryList = new ArrayList<>();
        int categoryCount = 0;
        imageList = GetAllPhotoFromGallery.getAllImageFromGallery(getContext());

        try {
            categoryList.add(new Category(imageList.get(0).getDateTaken(), new ArrayList<>()));
            categoryList.get(categoryCount).addListGirl(imageList.get(0));
            for (int i = 1; i < imageList.size(); i++) {
                if (!imageList.get(i).getDateTaken().equals(imageList.get(i - 1).getDateTaken())) {
                    categoryList.add(new Category(imageList.get(i).getDateTaken(), new ArrayList<>()));
                    categoryCount++;
                }
                categoryList.get(categoryCount).addListGirl(imageList.get(i));
            }
            return categoryList;
        } catch (Exception e) {
            return null;
        }

    }

    public class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            listImg = getListCategory();
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            categoryAdapter.setData(listImg);
        }
    }
}
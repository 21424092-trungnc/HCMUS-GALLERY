package com.hcmus.project_21424074_21424092_21424094.activities.subActivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.project_21424074_21424092_21424094.R;
import com.hcmus.project_21424074_21424092_21424094.activities.mainActivities.SlideShowActivity;
import com.hcmus.project_21424074_21424092_21424094.adapters.AlbumSheetAdapter;
import com.hcmus.project_21424074_21424092_21424094.adapters.CategoryMultiAdapter;
import com.hcmus.project_21424074_21424092_21424094.models.Album;
import com.hcmus.project_21424074_21424092_21424094.models.Category;
import com.hcmus.project_21424074_21424092_21424094.models.Image;
import com.hcmus.project_21424074_21424092_21424094.utility.FileUtility;
import com.hcmus.project_21424074_21424092_21424094.utility.GetAllPhotoFromGallery;
import com.hcmus.project_21424074_21424092_21424094.utility.ListTransInterface;
import com.hcmus.project_21424074_21424092_21424094.utility.SubInterface;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MultiSelectImage extends AppCompatActivity implements ListTransInterface, SubInterface {
    private RecyclerView ryc_list_album;
    private Toolbar toolbar_item_album;
    private List<Category> listImg;
    private List<Image> imageList;
    private CategoryMultiAdapter categoryMultiAdapter;
    private ArrayList<Image> listImageSelected;
    private BottomSheetDialog bottomSheetDialog;
    private RecyclerView ryc_album;
    EditText edittext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_select_image);
        setUpData();
        mappingControls();
        addEvents();
    }

    private void setUpData() {
        listImageSelected = new ArrayList<>();
    }

    private void addEvents() {
        setRyc();
        // Toolbar events
        eventToolBar();
    }

    private void setRyc() {
        categoryMultiAdapter = new CategoryMultiAdapter(MultiSelectImage.this);
        categoryMultiAdapter.setListTransInterface(MultiSelectImage.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MultiSelectImage.this, RecyclerView.VERTICAL, false);
        ryc_list_album.setLayoutManager(linearLayoutManager);

        //Set adapter
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
    }

    private void eventToolBar() {
        toolbar_item_album.inflateMenu(R.menu.menu_multi_select);
        toolbar_item_album.setTitle("Multi Select");

        toolbar_item_album.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.menuCreateAlbum:
                        AlertDialog.Builder alert = new AlertDialog.Builder(MultiSelectImage.this);
                        edittext = new EditText(MultiSelectImage.this);
                        alert.setMessage("Enter name album");
                        alert.setView(edittext);
                        alert.setPositiveButton("Yes Option", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if(!TextUtils.isEmpty(edittext.getText())) {
                                    CreateAlbumAsyncTask createAlbumAsyncTask = new CreateAlbumAsyncTask();
                                    createAlbumAsyncTask.execute();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "Tiêu đề rỗng", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        alert.show();

                        break;
                    case R.id.menuMultiDelete:
                        deleteEvents();
                        break;
                    case R.id.menuSlideshow:
                        slideShowEvents();
                        break;
                    case R.id.menuAddAlbum:
                        openBottomDialog();
                        break;
                }
                return true;
            }
        });
    }

    private void hideEvents() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MultiSelectImage.this);

        builder.setTitle("Confirm");
        builder.setMessage("Do you want to hide/show this image?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                AddSecretAsync addSecretAsync = new AddSecretAsync();
                addSecretAsync.execute();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();


    }

    private void mappingControls() {
        ryc_list_album = findViewById(R.id.ryc_list_album);
        toolbar_item_album = findViewById(R.id.toolbar_item_album);
    }

    private List<Category> getListCategory() {
        List<Category> categoryList = new ArrayList<>();
        int categoryCount = 0;
        imageList = GetAllPhotoFromGallery.getAllImageFromGallery(MultiSelectImage.this);

        try {
            categoryList.add(new Category(imageList.get(0).getDateTaken(),new ArrayList<>()));
            categoryList.get(categoryCount).addListGirl(imageList.get(0));
            for(int i=1;i<imageList.size();i++){
                if(!imageList.get(i).getDateTaken().equals(imageList.get(i-1).getDateTaken())){
                    categoryList.add(new Category(imageList.get(i).getDateTaken(),new ArrayList<>()));
                    categoryCount++;
                }
                categoryList.get(categoryCount).addListGirl(imageList.get(i));
            }
            return categoryList;
        } catch (Exception e){
            return null;
        }

    }
    private void deleteEvents() {
        for(int i=0;i<listImageSelected.size();i++) {
            Uri targetUri = Uri.parse("file://" + listImageSelected.get(i).getPath());
            File file = new File(targetUri.getPath());
            if (file.exists()){
                GetAllPhotoFromGallery.removeImageFromAllImages(targetUri.getPath());
                if(file.delete()) { // TODO Fails if photos were made in "Burst" mode (Asus Zenfone 6)
                    GetAllPhotoFromGallery.removeImageFromAllImages(targetUri.getPath());
                }else {
                    Log.d("deleteEvents","Failed to remove file "+targetUri.getPath());
                }
            }
            if(i==listImageSelected.size()-1) {
                finish();
            };
        }
    }
    private void slideShowEvents() {
        Intent intent = new Intent(MultiSelectImage.this, SlideShowActivity.class);
        ArrayList<String> list = new ArrayList<>();
        for(int i=0;i<listImageSelected.size();i++) {
            list.add(listImageSelected.get(i).getThumb());
        }
        intent.putStringArrayListExtra("data_slide", list);
        intent.putExtra("name", "Slide Show");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void openBottomDialog() {
        View viewDialog = LayoutInflater.from(MultiSelectImage.this).inflate(R.layout.layout_bottom_sheet_add_to_album, null);
        ryc_album = viewDialog.findViewById(R.id.ryc_album);
        ryc_album.setLayoutManager(new GridLayoutManager(this, 2));

        bottomSheetDialog = new BottomSheetDialog(MultiSelectImage.this);
        bottomSheetDialog.setContentView(viewDialog);
        AddSyncTask addSyncTask = new AddSyncTask();
        addSyncTask.execute();

    }

    @Override
    public void addList(Image img) {
        listImageSelected.add(img);
    }
    public void removeList(Image img) {
        listImageSelected.remove(img);
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
            categoryMultiAdapter.setData(listImg);
            ryc_list_album.setAdapter(categoryMultiAdapter);
        }
    }
    public class CreateAlbumAsyncTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String albumName = edittext.getText().toString();
            String albumPath = Environment.getExternalStorageDirectory()+ File.separator+"Pictures" + File.separator +albumName;
            File directtory = new File(albumPath);
            if(!directtory.exists()){
                directtory.mkdirs();
                Log.e("File-no-exist",directtory.getPath());
            }
            String[] paths = new String[listImageSelected.size()];
            int i =0;
            for (Image img :listImageSelected){
                File imgFile = new File(img.getPath());
                File desImgFile = new File(albumPath,albumName+"_"+imgFile.getName());
                imgFile.renameTo(desImgFile);
                imgFile.deleteOnExit();
                paths[i] = desImgFile.getPath();
                i++;
            }
            MediaScannerConnection.scanFile(getApplicationContext(),paths, null, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            finish();
        }
    }
    @Override
    public void add(Album album) {
        AddAlbumAsync addAlbumAsync = new AddAlbumAsync();
        addAlbumAsync.setAlbum(album);
        addAlbumAsync.execute();
    }

    public class AddSyncTask extends AsyncTask<Void, Integer, Void> {
        private AlbumSheetAdapter albumSheetAdapter;
        private List<Album> listAlbum;
        @Override
        protected Void doInBackground(Void... voids) {
            List<Image> listImage = GetAllPhotoFromGallery.getAllImageFromGallery(MultiSelectImage.this);
            listAlbum = getListAlbum(listImage);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            albumSheetAdapter = new AlbumSheetAdapter(listAlbum, MultiSelectImage.this);
            albumSheetAdapter.setSubInterface(MultiSelectImage.this);
            ryc_album.setAdapter(albumSheetAdapter);
            bottomSheetDialog.show();
        }
        @NonNull
        private List<Album> getListAlbum(List<Image> listImage) {
            List<String> ref = new ArrayList<>();
            List<Album> listAlbum = new ArrayList<>();

            for (int i = 0; i < listImage.size(); i++) {
                String[] _array = listImage.get(i).getThumb().split("/");
                String _pathFolder = listImage.get(i).getThumb().substring(0, listImage.get(i).getThumb().lastIndexOf("/"));
                String _name = _array[_array.length - 2];
                if (!ref.contains(_pathFolder)) {
                    ref.add(_pathFolder);
                    Album token = new Album(listImage.get(i), _name);
                    token.setPathFolder(_pathFolder);
                    token.addItem(listImage.get(i));
                    listAlbum.add(token);
                } else {
                    listAlbum.get(ref.indexOf(_pathFolder)).addItem(listImage.get(i));
                }
            }

            return listAlbum;
        }
    }
    public class AddAlbumAsync extends AsyncTask<Void, Integer, Void> {
        Album album;
        public void setAlbum(Album album) {
            this.album = album;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String[] paths = new String[listImageSelected.size()];
            int i =0;
            for (Image img :listImageSelected){
                File imgFile = new File(img.getPath());
                File desImgFile = new File(album.getPathFolder(),album.getName()+"_"+imgFile.getName());
                imgFile.renameTo(desImgFile);
                imgFile.deleteOnExit();
                paths[i] = desImgFile.getPath();
                i++;
            }
            MediaScannerConnection.scanFile(getApplicationContext(),paths, null, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            bottomSheetDialog.cancel();
            finish();
        }
    }
    public class AddSecretAsync extends AsyncTask<Void, Integer, Void> {
        private ArrayList<String> list;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String scrPath = Environment.getExternalStorageDirectory()+File.separator+".secret";
            File scrDir = new File(scrPath);
            if(!scrDir.exists()){
                Toast.makeText(MultiSelectImage.this, "You haven't created secret album", Toast.LENGTH_SHORT).show();
            }
            else{
                for(int i=0;i<listImageSelected.size();i++) {
                    Image img = listImageSelected.get(i);
                    FileUtility fu = new FileUtility();
                    File imgFile = new File(img.getPath());
                    list.add(img.getPath());
                    fu.moveFile(img.getPath(), imgFile.getName(), scrPath);
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Intent intentResult = new Intent();
            intentResult.putStringArrayListExtra("list_hide",list);
            setResult(RESULT_OK, intentResult);
            finish();
        }
    }
}
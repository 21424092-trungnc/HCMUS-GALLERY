package com.hcmus.project_21424074_21424092_21424094.activities.mainActivities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.hcmus.project_21424074_21424092_21424094.R;
import com.hcmus.project_21424074_21424092_21424094.activities.mainActivities.data_favor.DataLocalManager;
import com.hcmus.project_21424074_21424092_21424094.adapters.AlbumSheetAdapter;
import com.hcmus.project_21424074_21424092_21424094.adapters.SlideImageAdapter;
import com.hcmus.project_21424074_21424092_21424094.mainFragments.BottomSheetFragment;
import com.hcmus.project_21424074_21424092_21424094.models.Album;
import com.hcmus.project_21424074_21424092_21424094.models.Image;
import com.hcmus.project_21424074_21424092_21424094.utility.FileUtility;
import com.hcmus.project_21424074_21424092_21424094.utility.GetAllPhotoFromGallery;
import com.hcmus.project_21424074_21424092_21424094.utility.PictureInterface;
import com.hcmus.project_21424074_21424092_21424094.utility.SubInterface;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PictureActivity extends AppCompatActivity implements PictureInterface, SubInterface {
    private ViewPager viewPager_picture;
    private Toolbar toolbar_picture;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frame_viewPager;
    private ArrayList<String> imageListThumb;
    private ArrayList<String> imageListPath;
    private Intent intent;
    private int pos;
    private SlideImageAdapter slideImageAdapter;
    private PictureInterface activityPicture;
    private String imgPath;
    private String imageName;
    private String thumb;
    private Bitmap imageBitmap;
    private String title, link, displayedLink, snippet;
    private RecyclerView resultsRV;
    private BottomSheetDialog bottomSheetDialog;
    private RecyclerView ryc_album;
    public static Set<String> imageListFavor = DataLocalManager.getListSet();

    @Override
    protected void onResume() {
        super.onResume();
        imageListFavor = DataLocalManager.getListSet();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        // Fix Uri file SDK link:
        // https://stackoverflow.com/questions/48117511/exposed-beyond-app-through-clipdata-item-geturi?answertab=oldest#tab-top
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        mappingControls();
        events();
    }

    private void events() {
        setDataIntent();
        setUpToolBar();
        setUpSilder();
        bottomNavigationViewEvents();
    }

    private void bottomNavigationViewEvents() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Uri targetUri = Uri.parse("file://" + thumb);
                switch (item.getItemId()) {
                    case R.id.sharePic:
                        Drawable mDrawable = Drawable.createFromPath(imgPath);
                        Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();
                        String path = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap,
                                "Mô tả hình ảnh", null);
                        thumb = thumb.replaceAll(" ", "");
                        Uri uri = Uri.parse(path);
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("image/*");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        startActivity(Intent.createChooser(shareIntent, "Chia sẻ hình ảnh"));
                        break;

                    case R.id.editPic:
                        Intent editIntent = new Intent(PictureActivity.this, DsPhotoEditorActivity.class);
                        // Set data
                        editIntent.setData(Uri.fromFile(new File(imgPath)));
                        // Set output directory
                        editIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "HCMUS - GALLERY");
                        // Set toolbar color
                        editIntent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR,
                                Color.parseColor("#FF000000"));
                        // Set background color
                        editIntent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR,
                                Color.parseColor("#FF000000"));
                        // Start activity
                        startActivity(editIntent);
                        break;

                    case R.id.starPic:
                        if (!imageListFavor.add(imgPath)) {
                            imageListFavor.remove(imgPath);
                        }
                        DataLocalManager.setListImg(imageListFavor);
                        Toast.makeText(PictureActivity.this, imageListFavor.size() + "", Toast.LENGTH_SHORT).show();
                        if (!check(imgPath)) {
                            bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.ic_star);
                        } else {
                            bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.ic_star_red);
                        }
                        break;

                    case R.id.deletePic:
                        AlertDialog.Builder builder = new AlertDialog.Builder(PictureActivity.this);
                        builder.setTitle("Xác nhận");
                        builder.setMessage("Bạn có muốn xóa hình ảnh?");
                        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                File file = new File(targetUri.getPath());
                                if (file.exists()) {
                                    if (file.delete()) {
                                        GetAllPhotoFromGallery.removeImageFromAllImages(targetUri.getPath());
                                        Toast.makeText(PictureActivity.this,
                                                "Xóa thành không: " + targetUri.getPath(), Toast.LENGTH_SHORT)
                                                .show();
                                    } else
                                        Toast.makeText(PictureActivity.this,
                                                "Xóa không thành công: " + targetUri.getPath(),
                                                Toast.LENGTH_SHORT).show();
                                }
                                finish();
                                dialog.dismiss();
                            }
                        });

                        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                        break;
                }
                return true;
            }
        });
    }

    private void showNavigation(boolean flag) {
        if (!flag) {
            bottomNavigationView.setVisibility(View.INVISIBLE);
            toolbar_picture.setVisibility(View.INVISIBLE);
        } else {
            bottomNavigationView.setVisibility(View.VISIBLE);
            toolbar_picture.setVisibility(View.VISIBLE);
        }
    }

    private void setUpToolBar() {
        // Toolbar events
        toolbar_picture.inflateMenu(R.menu.menu_top_picture);
        setTitleToolbar("abc");
        // Show back button
        // toolbar_picture.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar_picture.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Show info
        toolbar_picture.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.menuInfo:
                        Uri targetUri = Uri.parse("file://" + thumb);
                        if (targetUri != null) {
                            showExif(targetUri);
                        }
                        break;
                    case R.id.menuAddAlbum:
                        openBottomDialog();
                        break;
                    case R.id.setWallpaper:
                        Uri uri_wallpaper = Uri.parse("file://" + thumb);
                        Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setDataAndType(uri_wallpaper, "image/*");
                        intent.putExtra("mimeType", "image/*");
                        startActivity(Intent.createChooser(intent, "Set as:"));
                }
                return true;
            }
        });
    }

    private void showExif(Uri photoUri) {
        if (photoUri != null) {

            ParcelFileDescriptor parcelFileDescriptor = null;

            try {
                parcelFileDescriptor = getContentResolver().openFileDescriptor(photoUri, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

                ExifInterface exifInterface = new ExifInterface(fileDescriptor);

                BottomSheetDialog infoDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
                View infoDialogView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.layout_info,
                                (LinearLayout) findViewById(R.id.infoContainer),
                                false);
                TextView txtInfoProducer = (TextView) infoDialogView.findViewById(R.id.txtInfoProducer);
                TextView txtInfoSize = (TextView) infoDialogView.findViewById(R.id.txtInfoSize);
                TextView txtInfoModel = (TextView) infoDialogView.findViewById(R.id.txtInfoModel);
                TextView txtInfoFlash = (TextView) infoDialogView.findViewById(R.id.txtInfoFlash);
                TextView txtInfoFocalLength = (TextView) infoDialogView.findViewById(R.id.txtInfoFocalLength);
                TextView txtInfoAuthor = (TextView) infoDialogView.findViewById(R.id.txtInfoAuthor);
                TextView txtInfoTime = (TextView) infoDialogView.findViewById(R.id.txtInfoTime);
                TextView txtInfoName = (TextView) infoDialogView.findViewById(R.id.txtInfoName);

                txtInfoName.setText(imageName);
                txtInfoProducer.setText(exifInterface.getAttribute(ExifInterface.TAG_MAKE));
                txtInfoSize.setText(exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH) + "x"
                        + exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH));
                txtInfoModel.setText(exifInterface.getAttribute(ExifInterface.TAG_MODEL));
                txtInfoFlash.setText(exifInterface.getAttribute(ExifInterface.TAG_FLASH));
                txtInfoFocalLength.setText(exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH));
                txtInfoAuthor.setText(exifInterface.getAttribute(ExifInterface.TAG_ARTIST));
                txtInfoTime.setText(exifInterface.getAttribute(ExifInterface.TAG_DATETIME));

                infoDialog.setContentView(infoDialogView);
                infoDialog.show();
                parcelFileDescriptor.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Lỗi:\n" + e.toString(),
                        Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Lỗi:\n" + e.toString(),
                        Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(getApplicationContext(),
                    "photoUri == null",
                    Toast.LENGTH_LONG).show();
        }
    };

    private void setUpSilder() {
        slideImageAdapter = new SlideImageAdapter();
        slideImageAdapter.setData(imageListThumb, imageListPath);
        slideImageAdapter.setContext(getApplicationContext());
        slideImageAdapter.setPictureInterface(activityPicture);
        viewPager_picture.setAdapter(slideImageAdapter);
        viewPager_picture.setCurrentItem(pos);
        viewPager_picture.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                thumb = imageListThumb.get(position);
                imgPath = imageListPath.get(position);
                setTitleToolbar(thumb.substring(thumb.lastIndexOf('/') + 1));
                if (!check(imgPath)) {
                    bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.ic_star);
                } else {
                    bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.ic_star_red);
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setDataIntent() {
        intent = getIntent();
        imageListPath = intent.getStringArrayListExtra("data_list_path");
        imageListThumb = intent.getStringArrayListExtra("data_list_thumb");
        pos = intent.getIntExtra("pos", 0);
        activityPicture = this;
    }

    private void mappingControls() {
        viewPager_picture = findViewById(R.id.viewPager_picture);
        bottomNavigationView = findViewById(R.id.bottom_picture);
        toolbar_picture = findViewById(R.id.toolbar_picture);
        frame_viewPager = findViewById(R.id.frame_viewPager);
    }

    public Boolean check(String Path) {
        for (String img : imageListFavor) {
            if (img.equals(Path)) {
                return true;
            }
        }
        return false;
    }

    public void setTitleToolbar(String imageName) {
        this.imageName = imageName;
        toolbar_picture.setTitle(imageName);
    }

    public void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    // bottomDialog Add to album
    private void openBottomDialog() {
        View viewDialog = LayoutInflater.from(PictureActivity.this).inflate(R.layout.layout_bottom_sheet_add_to_album,
                null);
        ryc_album = viewDialog.findViewById(R.id.ryc_album);
        ryc_album.setLayoutManager(new GridLayoutManager(this, 2));
        bottomSheetDialog = new BottomSheetDialog(PictureActivity.this);
        bottomSheetDialog.setContentView(viewDialog);
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
    }

    @Override
    public void actionShow(boolean flag) {
        showNavigation(flag);
    }

    @Override
    public void add(Album album) {
        AddAlbumAsync addAlbumAsync = new AddAlbumAsync();
        addAlbumAsync.setAlbum(album);
        addAlbumAsync.execute();
    }

    public class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
        private AlbumSheetAdapter albumSheetAdapter;
        private List<Album> listAlbum;

        @Override
        protected Void doInBackground(Void... voids) {
            List<Image> listImage = GetAllPhotoFromGallery.getAllImageFromGallery(PictureActivity.this);
            listAlbum = getListAlbum(listImage);
            String path_folder = imgPath.substring(0, imgPath.lastIndexOf("/"));
            for (int i = 0; i < listAlbum.size(); i++) {
                if (path_folder.equals(listAlbum.get(i).getPathFolder())) {
                    listAlbum.remove(i);
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            albumSheetAdapter = new AlbumSheetAdapter(listAlbum, PictureActivity.this);
            albumSheetAdapter.setSubInterface(PictureActivity.this);
            ryc_album.setAdapter(albumSheetAdapter);
            bottomSheetDialog.show();
        }

        @NonNull
        private List<Album> getListAlbum(List<Image> listImage) {
            List<String> ref = new ArrayList<>();
            List<Album> listAlbum = new ArrayList<>();

            for (int i = 0; i < listImage.size(); i++) {
                String[] _array = listImage.get(i).getThumb().split("/");
                String _pathFolder = listImage.get(i).getThumb().substring(0,
                        listImage.get(i).getThumb().lastIndexOf("/"));
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
            File directtory = new File(album.getPathFolder());
            if (!directtory.exists()) {
                directtory.mkdirs();
                Log.e("File-no-exist", directtory.getPath());
            }
            String[] paths = new String[1];
            File imgFile = new File(imgPath);
            File desImgFile = new File(album.getPathFolder(), album.getName() + "_" + imgFile.getName());
            imgFile.renameTo(desImgFile);
            imgFile.deleteOnExit();
            paths[0] = desImgFile.getPath();
            for (String imgFavor : imageListFavor) {
                if (imgFavor.equals(imgFile.getPath())) {
                    imageListFavor.remove(imgFile.getPath());
                    imageListFavor.add(desImgFile.getPath());
                    break;
                }
            }
            DataLocalManager.setListImg(imageListFavor);
            MediaScannerConnection.scanFile(getApplicationContext(), paths, null, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            bottomSheetDialog.cancel();
        }
    }
}
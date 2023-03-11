package com.hcmus.project_21424074_21424092_21424094;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class FragmentBlue extends Fragment implements FragmentCallbacks  {
    // this fragment shows a ListView
    MainActivity main;
    Context context = null;
    TextView txtMSSV;
    ListView listView;
    // data to fill-up the ListView
    private String items[] = {"21424074-Nguyễn Phi Hùng-21HCB-9.0", "21424092-Nguyễn Chí Trung-21HCB-8.5", "21424094-Nguyễn Xuân Trường-21HCB-8.0"};

    private String mssv[] = {"21424074", "21424092", "21424094"};
    private Integer[] thumbnails = { R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3};
    // convenient constructor(accept arguments, copy them to a bundle, binds bundle to fragment)
    public static FragmentBlue newInstance(String strArg) {
        FragmentBlue fragment = new FragmentBlue();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity(); // use this reference to invoke main callbacks
            main = (MainActivity) getActivity();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout_blue = (LinearLayout) inflater.inflate(R.layout.layout_blue, null);
        txtMSSV = (TextView) layout_blue.findViewById(R.id.textView1Blue);
        listView = (ListView) layout_blue.findViewById(R.id.listView1Blue);
        listView.setBackgroundColor(Color.parseColor("#20ccddff"));
        CustomIconLabelAdapter adapter = new CustomIconLabelAdapter(context, R.layout.custom_row_icon_label, items, thumbnails);
        listView.setAdapter(adapter);
        listView.setSelection(0);
        txtMSSV.setText("MSSV:");
        listView.smoothScrollToPosition(0);
        main.onMsgFromFragToMain("BLUE-FRAG", items[0] + '-' + 0);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                main.onMsgFromFragToMain("BLUE-FRAG", items[position] + '-' + position);
                String[] rs = items[position].split("-");
                txtMSSV.setText("MSSV: " + mssv[position]);
                changeColorItemSelected(position, listView);
            }
        });
        return layout_blue;
    }

    @Override
    public void onMsgFromMainToFragment(String strValue) {
        int pos = Integer.parseInt(strValue);
        txtMSSV.setText("MSSV: " + mssv[pos]);
        main.onMsgFromFragToMain("BLUE-FRAG", items[pos] + '-' + pos);
        listView.getChildAt(pos).setBackgroundColor(Color.parseColor("#808080"));
        changeColorItemSelected(pos, listView);
    }

    @Override
    public void onResume() {
        super.onResume();
        main.onMsgFromFragToMain("BLUE-FRAG", "");

    }

    private void changeColorItemSelected(int pos, ListView lv){
        for (int i = 0; i < listView.getChildCount(); i++) {
            if(pos == i ){
                lv.getChildAt(i).setBackgroundColor(Color.parseColor("#40009688"));
            }else{
                lv.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }
}
package com.hcmus.project_21424074_21424092_21424094;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Date;

public class FragmentRed extends Fragment implements FragmentCallbacks {
    MainActivity main;
    TextView txtMSSV;
    TextView txtContent;
    Button btnFirst;
    Button btnPre;
    Button btnNext;
    Button btnLast;
    int currentPos = 0;

    public static FragmentRed newInstance(String strArg1) {
        FragmentRed fragment = new FragmentRed();
        Bundle bundle = new Bundle();
        bundle.putString("arg1", strArg1);
        fragment.setArguments(bundle);
        return fragment;
    }// newInstance

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!(getActivity() instanceof MainCallbacks)) {
            throw new IllegalStateException("Activity must implement MainCallbacks");
        }
        main = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view_layout_red = (LinearLayout) inflater.inflate(R.layout.layout_red, null);
        txtMSSV = (TextView) view_layout_red.findViewById(R.id.MSSV);
        txtContent = (TextView) view_layout_red.findViewById(R.id.content);
        try {
            Bundle arguments = getArguments();
            txtMSSV.setText(arguments.getString("arg1", ""));
        } catch (Exception e) {
            Log.e("RED BUNDLE ERROR – ", "" + e.getMessage());
        }

        btnFirst = (Button) view_layout_red.findViewById(R.id.buttonFirst);
        btnPre = (Button) view_layout_red.findViewById(R.id.buttonPre);
        btnNext = (Button) view_layout_red.findViewById(R.id.buttonNext);
        btnLast = (Button) view_layout_red.findViewById(R.id.buttonLast);

        btnFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.onMsgFromFragToMain("RED-FRAG", "0");
                btnFirst.setClickable(false);
                btnFirst.getBackground().setColorFilter(null);
                btnPre.setClickable(false);
                btnFirst.getBackground().setColorFilter(null);
                btnNext.setClickable(true);
                btnNext.getBackground().setColorFilter(Color.blue(100), PorterDuff.Mode.MULTIPLY);
                btnLast.setClickable(true);
                btnLast.getBackground().setColorFilter(Color.blue(100), PorterDuff.Mode.MULTIPLY);
            }
        });
        btnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPos >= 1) {
                    currentPos = currentPos - 1;
                    main.onMsgFromFragToMain("RED-FRAG", currentPos + "");
                }
                if (currentPos == 0) {
                    btnFirst.setClickable(false);
                    btnFirst.getBackground().setColorFilter(null);
                    btnPre.setClickable(false);
                    btnPre.getBackground().setColorFilter(null);
                } else {
                    btnFirst.setClickable(true);
                    btnFirst.getBackground().setColorFilter(Color.blue(100), PorterDuff.Mode.MULTIPLY);
                    btnPre.setClickable(true);
                    btnPre.getBackground().setColorFilter(Color.blue(100), PorterDuff.Mode.MULTIPLY);

                }
                btnNext.setClickable(true);
                btnNext.getBackground().setColorFilter(Color.blue(100), PorterDuff.Mode.MULTIPLY);
                btnLast.setClickable(true);
                btnLast.getBackground().setColorFilter(Color.blue(100), PorterDuff.Mode.MULTIPLY);
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPos <= 3) {
                    currentPos = currentPos + 1;
                    main.onMsgFromFragToMain("RED-FRAG", currentPos + "");
                }

                btnFirst.setClickable(true);
                btnFirst.getBackground().setColorFilter(Color.blue(100), PorterDuff.Mode.MULTIPLY);
                btnPre.setClickable(true);
                btnPre.getBackground().setColorFilter(Color.blue(100), PorterDuff.Mode.MULTIPLY);
                if (currentPos == 2) {
                    btnNext.setClickable(false);
                    btnNext.getBackground().setColorFilter(null);
                    btnLast.setClickable(false);
                    btnLast.getBackground().setColorFilter(null);
                } else {
                    btnNext.setClickable(true);
                    btnNext.getBackground().setColorFilter(Color.blue(100), PorterDuff.Mode.MULTIPLY);
                    btnLast.setClickable(true);
                    btnLast.getBackground().setColorFilter(Color.blue(100), PorterDuff.Mode.MULTIPLY);
                }

            }
        });
        btnLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.onMsgFromFragToMain("RED-FRAG", "2");
                btnFirst.setClickable(true);
                btnFirst.getBackground().setColorFilter(Color.blue(100), PorterDuff.Mode.MULTIPLY);
                btnPre.setClickable(true);
                btnPre.getBackground().setColorFilter(Color.blue(100), PorterDuff.Mode.MULTIPLY);
                btnNext.setClickable(false);
                btnNext.getBackground().setColorFilter(null);
                btnLast.setClickable(false);
                btnLast.getBackground().setColorFilter(null);

            }
        });
        return view_layout_red;
    }

    @Override
    public void onMsgFromMainToFragment(String strValue) {
        if (strValue != "" && strValue != null) {
            String[] rs = strValue.split("-");
            txtMSSV.setText(rs[0]);
            txtContent.setText("Họ tên: " + rs[1] + "\n" + "Lớp: " + rs[2] + "\n" + "Điểm trung bình: " + rs[3]);
            currentPos = Integer.parseInt(rs[4]);
            if (currentPos == 2) {
                btnNext.setClickable(false);
                btnNext.getBackground().setColorFilter(null);
                btnLast.setClickable(false);
                btnLast.getBackground().setColorFilter(null);
            } else {
                btnNext.setClickable(true);
                btnNext.getBackground().setColorFilter(Color.blue(100), PorterDuff.Mode.MULTIPLY);
                btnLast.setClickable(true);
                btnLast.getBackground().setColorFilter(Color.blue(100), PorterDuff.Mode.MULTIPLY);
            }
            if (currentPos == 0) {
                btnFirst.setClickable(false);
                btnFirst.getBackground().setColorFilter(null);
                btnPre.setClickable(false);
                btnPre.getBackground().setColorFilter(null);
            } else {
                btnFirst.setClickable(true);
                btnFirst.getBackground().setColorFilter(Color.blue(100), PorterDuff.Mode.MULTIPLY);
                btnPre.setClickable(true);
                btnPre.getBackground().setColorFilter(Color.blue(100), PorterDuff.Mode.MULTIPLY);
            }
        } else {
            txtMSSV.setText("Vui lòng chọn sinh viên");
            btnFirst.setClickable(false);
            btnFirst.getBackground().setColorFilter(null);
            btnPre.setClickable(false);
            btnPre.getBackground().setColorFilter(null);
            btnNext.setClickable(false);
            btnNext.getBackground().setColorFilter(null);
            btnLast.setClickable(false);
            btnLast.getBackground().setColorFilter(null);
        }
    }
}

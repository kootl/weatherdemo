package com.sunstar.weatherdemo;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sunstar.weatherdemo.db.bean.Area;
import com.sunstar.weatherdemo.db.bean.City;
import com.sunstar.weatherdemo.db.bean.Province;
import com.sunstar.weatherdemo.helper.JsonHelper;
import com.sunstar.weatherdemo.helper.SimpleOkHttpHelper;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChoseAreaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChoseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_AREA = 2;

    private List<Province> mProvinceList = new ArrayList<>();
    private List<City> mCityList = new ArrayList<>();
    private List<Area> mAreaList = new ArrayList<>();
    private Province mProvinceSelected;
    private City mCitySelected;
    private int mNowLevel;


    private ProgressDialog mProgressDialog;
    private TextView mTextViewTitle;
    private Button mButtonBack;
    private ListView mListView;
    private ArrayAdapter<String> mStringArrayAdapter;
    private List<String> mDataList = new ArrayList<>();


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public ChoseAreaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChoseAreaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChoseAreaFragment newInstance(String param1, String param2) {
        ChoseAreaFragment fragment = new ChoseAreaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chose_area, container, false);
        mButtonBack = (Button) view.findViewById(R.id.id_btn_back);
        mTextViewTitle = (TextView) view.findViewById(R.id.id_tv_title);
        mListView = (ListView) view.findViewById(R.id.id_listview);
        mStringArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, mDataList);
        mListView.setAdapter(mStringArrayAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (mNowLevel) {
                    case LEVEL_PROVINCE:
                        mProvinceSelected = mProvinceList.get(position);
                        findCity();
                        break;
                    case LEVEL_CITY:
                        mCitySelected = mCityList.get(position);
                        findArea();
                        break;
                    case LEVEL_AREA:
                      if (getActivity() instanceof MainActivity){
                          String weatherid=mAreaList.get(position).getWeather_id();
                          WeatherActivity.stareMe(getActivity(),weatherid);
                      }else if (getActivity() instanceof WeatherActivity){
                          String weatherid=mAreaList.get(position).getWeather_id();
                          WeatherActivity weatherActivity= (WeatherActivity) getActivity();
                          weatherActivity.changeArea(weatherid);
                      }
                        break;
                }
            }
        });
        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mNowLevel) {
                    case LEVEL_PROVINCE:
                      /* do nothing */
                        break;
                    case LEVEL_CITY:
                        findProvince();
                        break;
                    case LEVEL_AREA:
                        findCity();
                        break;
                }
            }
        });

        //
        findProvince();//初始化
    }

    private void findProvince() {
        mTextViewTitle.setText("中国");
        mButtonBack.setVisibility(View.GONE);
        mProvinceList = DataSupport.findAll(Province.class);
        if (mProvinceList.size() > 0) {
            mDataList.clear();
            for (Province p : mProvinceList
                    ) {
                mDataList.add(p.getName());
            }
            mStringArrayAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mNowLevel = LEVEL_PROVINCE;
        } else {
            String url = "http://guolin.tech/api/china";
            findOnline(url, "province");
        }
    }

    private void findCity() {

        mTextViewTitle.setText(mProvinceSelected.getName());
        mButtonBack.setVisibility(View.VISIBLE);
        mCityList = DataSupport.where("provinceId=?", String.valueOf(mProvinceSelected.getId())).find(City.class);
        if (mCityList.size() > 0) {
            mDataList.clear();
            for (City p : mCityList
                    ) {
                mDataList.add(p.getName());
            }
            mStringArrayAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mNowLevel = LEVEL_CITY;
        } else {
            int provinceCode = mProvinceSelected.getCode();

            String url = "http://guolin.tech/api/china/" + provinceCode;
            findOnline(url, "city");
        }
    }


    private void findArea() {

        mTextViewTitle.setText(mCitySelected.getName());
        mButtonBack.setVisibility(View.VISIBLE);
        mAreaList = DataSupport.where("cityId=?", String.valueOf(mCitySelected.getId())).find(Area.class);
        if (mAreaList.size() > 0) {
            mDataList.clear();
            for (Area p : mAreaList
                    ) {
                mDataList.add(p.getName());
            }
            mStringArrayAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mNowLevel = LEVEL_AREA;
        } else {
            int provinceCode = mProvinceSelected.getCode();
            int cityCode = mCitySelected.getCode();
            String url = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            findOnline(url, "area");
        }
    }

    private void findOnline(String url, final String type) {
        showLoading();
        SimpleOkHttpHelper.reqHttp(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();
                        Toast.makeText(getActivity(), "加载失败...", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result=response.body().string();
                boolean isSuccess=false;
                switch (type){
                    case "province":
                        isSuccess= JsonHelper.parseProvince(result);
                        break;
                    case "city":
                        isSuccess= JsonHelper.parseCity(result,mProvinceSelected.getId());
                        break;
                    case "area":
                        isSuccess= JsonHelper.parseArea(result,mCitySelected.getId());
                        break;
                }
                //
                if (isSuccess){
                    //
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            hideLoading();
                            //
                            switch (type){
                                case "province":
                                   findProvince();
                                    break;
                                case "city":
                                    findCity();
                                    break;
                                case "area":
                                    findArea();
                                    break;
                            }



                        }
                    });

                }
            }
        });

    }

    private void showLoading() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.show();
        }
    }

    private void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    protected  void  callAtAtyOnBackPressed(){
        //
        switch (mNowLevel) {
            case LEVEL_PROVINCE:
               getActivity().finish();//结束aty
                break;
            case LEVEL_CITY:
                findProvince();
                break;
            case LEVEL_AREA:
                findCity();
                break;
        }
    }

}

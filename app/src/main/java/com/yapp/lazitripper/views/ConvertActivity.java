package com.yapp.lazitripper.views;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yapp.lazitripper.R;
import com.yapp.lazitripper.dto.PlaceInfoDto;
import com.yapp.lazitripper.dto.RegionCodeDto;
import com.yapp.lazitripper.dto.common.CommonResponse;
import com.yapp.lazitripper.network.LaziTripperKoreanTourClient;
import com.yapp.lazitripper.service.LaziTripperKoreanTourService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConvertActivity extends AppCompatActivity {

    Button btnConvert;
    LaziTripperKoreanTourClient laziTripperKoreanTourClient;
    LaziTripperKoreanTourService laziTripperKoreanTourService;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("lazitripper");

    // paging 처리를 위한
    Integer page = 1;
    Integer pageNum = 3300;

    //1: 서울, 2: 인천, 3: 대전, 4: 대구, 5: 광주, 6: 부산, 7: 울산, 8: 세종특별자치시,
    //31: 경기도, 32: 강원도, 33: 충청북도, 34: 충청남도, 35: 경상북도, 36: 경상남도, 37: 전라북도, 38: 전라남도, 39: 제주도
    //31 - 1 :
    //Integer[] codeList = {1, 2, 3, 4, 5, 6, 7, 8, 31, 32, 33, 34, 35, 36, 37, 38, 39};
    //Integer[] codeList = {};
    Integer[] codeList = { 39};
    private List<PlaceInfoDto> array;
    private List<RegionCodeDto> regionCodeDtoList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert);

        btnConvert = (Button) findViewById(R.id.btn_convert);

        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer interval = 1000;


                for(int i = 0; i < codeList.length; i++){
                    final Integer cityCode = codeList[i];
                    getData(cityCode);
//                for (Integer code : codeList){
                    // 2초간 멈추게 하고싶다면
//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        public void run() {
//                            getData(cityCode);
//                        }
//                    }, interval*i);  // 2000은 2초를 의미합니다.
                }
            }
        });
    }

    public void getData(final Integer cityCode){
         laziTripperKoreanTourClient = new LaziTripperKoreanTourClient(getApplicationContext());
         laziTripperKoreanTourService = laziTripperKoreanTourClient.getLiziTripperService();

        Call<CommonResponse<RegionCodeDto>> callRelionInfo = laziTripperKoreanTourService.getRelionInfo(100,1,"AND","LaziTripper");

        callRelionInfo.enqueue(new Callback<CommonResponse<RegionCodeDto>>() {
            @Override
            public void onResponse(Call<CommonResponse<RegionCodeDto>> call, Response<CommonResponse<RegionCodeDto>> response) {
                regionCodeDtoList = response.body().getResponse().getBody().getItems().getItems();
                int index = regionCodeDtoList.size();
                ArrayList<String> list = new ArrayList<String>();
                for ( int i = 0 ; i < index ; i++){
                    list.add(regionCodeDtoList.get(i).getName());
                }
//
//                loadingDialog.dismiss();
//
//                cityDropDown.setWheelData(list);
//                cityDropDown.deferNotifyDataSetChanged();
//                isData = true;
            }

            @Override
            public void onFailure(Call<CommonResponse<RegionCodeDto>> call, Throwable t) {
                Log.i("ohdoking",t.getMessage());
            }
        });

         Call<CommonResponse<PlaceInfoDto>> callCityInfo = laziTripperKoreanTourService.getPlaceInfoByCity(pageNum,page,"B","Y","AND","LaziTripper",cityCode, 12);
        callCityInfo.enqueue(new Callback<CommonResponse<PlaceInfoDto>>() {
            @Override
            public void onResponse(Call<CommonResponse<PlaceInfoDto>> call, Response<CommonResponse<PlaceInfoDto>> response) {
                Log.e("ohdoking",response.body().getResponse().getBody().getItems().getItems().get(0).getTitle());

                Integer sigunguCode = response.body().getResponse().getBody().getItems().getItems().get(0).getSigungucode();
                array = response.body().getResponse().getBody().getItems().getItems();

                myRef.child("placeInfo").child(String.valueOf(cityCode)).setValue(array);




            }

            @Override
            public void onFailure(Call<CommonResponse<PlaceInfoDto>> call, Throwable t) {
                Log.i("ohdoking",t.getMessage());
            }
        });


    }
}

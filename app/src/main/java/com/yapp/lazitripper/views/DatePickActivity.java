package com.yapp.lazitripper.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.timessquare.CalendarPickerView;
import com.yapp.lazitripper.R;
import com.yapp.lazitripper.common.ConstantIntent;
import com.yapp.lazitripper.dto.PickDate;
import com.yapp.lazitripper.store.ConstantStore;
import com.yapp.lazitripper.store.SharedPreferenceStore;
import com.yapp.lazitripper.views.bases.BaseAppCompatActivity;

import java.util.Calendar;
import java.util.Date;

//여행 시작 -> 날짜 선택
public class DatePickActivity extends BaseAppCompatActivity {

    private int FLAG = 0;
    private PickDate pickDate;
    private CalendarPickerView calendar;
    private SharedPreferenceStore<PickDate> sharedPreferenceStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_pick);
        setHeader();

        sharedPreferenceStore = new SharedPreferenceStore<PickDate>(getApplicationContext(), ConstantStore.STORE);

        //뒤로가기, 액티비티 종료
        //TODO 버튼 이미지 변경
        ImageView leftImage = getLeftImageView();
        leftImage.setImageResource(R.drawable.arrow);
        leftImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //다음단계 버튼 -> 도시선택 액티비티
        //TODO '다음' 버튼 위치 하단으로 이동 (모든 액티비티 통일?)
        ImageView rightImage = getRightImageView();
        rightImage.setImageResource(R.drawable.next_icon);
        rightImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FLAG == 1){
                    Log.i("ohdoking",FLAG+"");
                    pickDate.setStartDate(new Date());
                    pickDate.setFinishDate(new Date());
                    pickDate.setPeriod(1l);
                }
                sharedPreferenceStore.savePreferences(ConstantStore.DATEKEY, pickDate);

                Intent i = new Intent(DatePickActivity.this, ChooseCityActivity.class);
                i.putExtra(ConstantIntent.PICKDATE,pickDate);
                startActivity(i);
            }
        });

        rightImage.setImageResource(R.drawable.arrow);

        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        Date today = new Date();
        calendar.init(today, nextYear.getTime())
                .inMode(CalendarPickerView.SelectionMode.RANGE);

        pickDate = new PickDate();
        
        //날짜 선택 Listener
        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                if (FLAG == 0){
                    pickDate.setStartDate(date);
                    FLAG = 1;
                }
                else{
                    pickDate.setFinishDate(date);
                    long diff = Math.abs(pickDate.getStartDate().getTime() - date.getTime());
                    long diffDays = (diff / (24 * 60 * 60 * 1000)) + 1;

                    pickDate.setPeriod(diffDays);
                    Log.i("ohdoking",diffDays+"");
                    FLAG = 0;
                }
            }

            @Override
            public void onDateUnselected(Date date) {
                Log.i("ohdoking","onDateUnselected");
                pickDate = new PickDate();
            }
        });

        /*
        * 이전날짜 선택시 시작 날짜가 이전날짜가 되도록 수정
        * */
        calendar.setCellClickInterceptor(new CalendarPickerView.CellClickInterceptor() {
            @Override
            public boolean onCellClicked(Date date) {
                if (FLAG == 1){
                    long diff = pickDate.getStartDate().getTime() - date.getTime();
                    if(diff > 0){
                        calendar.clearHighlightedDates();
                        FLAG = 0;
                        return false;
                    }
                    else{
                        return false;
                    }
                }
                return false;
            }
        });
    }
}
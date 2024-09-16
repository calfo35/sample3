package com.example.alarmclock.listcomponent;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmclock.R;

public class ListViewHolder extends RecyclerView.ViewHolder {
    TextView alarmName;
    TextView time;
    TextView activityType;  // 朝活の種類
    TextView studyTime; // 勉強時間

    ListViewHolder(View itemView) {
        super(itemView);
        this.alarmName = itemView.findViewById(R.id.alarmName);
        this.time = itemView.findViewById(R.id.time);
        this.activityType = itemView.findViewById(R.id.activityType); // 新しく追加
        this.studyTime = itemView.findViewById(R.id.studyTime); // 新しく追加
    }

    // 朝活の種類と勉強時間の設定メソッド
    public void bind(ListItem item) {
        alarmName.setText(item.getAlarmName());
        time.setText(item.getTime());
        activityType.setText(item.getActivityType());
        studyTime.setText(item.getStudyTime() + " 分");
    }
}

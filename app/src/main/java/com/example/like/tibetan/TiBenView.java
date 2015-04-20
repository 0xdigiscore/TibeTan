package com.example.like.tibetan;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Created by like on 15-4-13.
 */
public class TiBenView extends RelativeLayout implements ViewGroup.OnClickListener{
    private Button btn;
    public TiBenView(Context context) {
        super(context);
    }
    public TiBenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(
          Context.LAYOUT_INFLATER_SERVICE
        );
        inflater.inflate(R.layout.tiben_view,this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_top1:
              break;
        }
    }
}

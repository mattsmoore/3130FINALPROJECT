package com.swg7.dalpolls.ui.main;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.swg7.dalpolls.R;

public class QuestionViewHolder extends RecyclerView.ViewHolder {

    public TextView name;
    public TextView info;
    public QuestionViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.question_name);
        info = itemView.findViewById(R.id.question_info);
    }

}

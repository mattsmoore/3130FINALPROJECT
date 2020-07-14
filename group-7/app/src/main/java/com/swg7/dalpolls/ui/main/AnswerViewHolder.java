package com.swg7.dalpolls.ui.main;

import android.view.View;
import android.widget.TextView;

import com.swg7.dalpolls.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AnswerViewHolder extends RecyclerView.ViewHolder{
    public TextView name;
    public TextView info;

    public AnswerViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.answer_name);
        info = itemView.findViewById(R.id.answer_info);
    }
}

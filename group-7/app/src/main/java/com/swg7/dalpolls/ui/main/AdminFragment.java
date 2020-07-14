package com.swg7.dalpolls.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.swg7.dalpolls.Admin_page;
import com.swg7.dalpolls.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class AdminFragment extends Fragment {

    private BlankViewModel mViewModel;

    public static AdminFragment newInstance() {
        return new AdminFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(BlankViewModel.class);
        // TODO: Use the ViewModel

        //display userID on admin page.
        TextView user_welcome = getActivity().findViewById(R.id.welcome_uid);

        Admin_page activity = (Admin_page) getActivity();
        String uID = activity.getuID();

        String userId = "User: " + uID;
        user_welcome.setText(userId);
    }

}

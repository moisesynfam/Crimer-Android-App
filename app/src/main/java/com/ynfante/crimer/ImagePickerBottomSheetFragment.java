package com.ynfante.crimer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ImagePickerBottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener{

    ArrayList<ButtonClickListener> buttonClickListeners;


    public static ImagePickerBottomSheetFragment newInstance() {
        return new ImagePickerBottomSheetFragment();
    }

    public ImagePickerBottomSheetFragment() {
        super();
        buttonClickListeners = new ArrayList<>();
    }

    public void  addButtonClickListener(ButtonClickListener listener) {
        buttonClickListeners.add(listener);
    }

    public void  removeButtonClickListener(ButtonClickListener listener) {
        buttonClickListeners.remove(listener);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_picker_botton_sheet, container, false);
        TextView camera, gallery, delete;
        camera = view.findViewById(R.id.img_bttm_sheet_camera_btn);
        gallery = view.findViewById(R.id.img_bttm_sheet_gallery_btn);
        delete = view.findViewById(R.id.img_bttm_sheet_delete_btn);

        camera.setOnClickListener(this);
        gallery.setOnClickListener(this);
        delete.setOnClickListener(this);


        return view;

    }

    @Override
    public void onClick(View view) {
        for ( ButtonClickListener listener :
                buttonClickListeners) {
            listener.onClick(view);
        }
    }

    public interface ButtonClickListener {

        void onClick(View button);
    }
}

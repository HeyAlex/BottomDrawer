package com.github.heyalex.bottomdrawerexample;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.github.heyalex.bottomdrawer.BottomDrawerDialog;
import com.github.heyalex.bottomdrawer.BottomDrawerFragment;
import com.github.heyalex.handle.PlainHandleView;
import com.github.heyalex.utils.BottomDrawerDelegate;
import com.github.heyalex.utils.DialogFragmentExtensionKt;
import com.rtugeek.android.colorseekbar.ColorSeekBar;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSeekBar;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class GoogleTaskJavaSampleDialog extends BottomDrawerFragment {

    float alphaCancelButton = 0f;
    ImageView cancelButton;
    AppCompatSeekBar cornerRadiusSeekBar;
    AppCompatCheckBox navigation;
    AppCompatCheckBox statusBar;
    ColorSeekBar colorSeekBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.google_task_example_layout, container, false);
        cancelButton = view.findViewById(R.id.cancel);
        final float percent = 0.65f;

        addBottomSheetCallback(new Function1<BottomDrawerDelegate.BottomSheetCallback, Unit>() {
            @Override
            public Unit invoke(BottomDrawerDelegate.BottomSheetCallback bottomSheetCallback) {
                bottomSheetCallback.onSlide(new Function2<View, Float, Unit>() {
                    @Override
                    public Unit invoke(View view, Float slideOffset) {
                        float alphaTemp = (slideOffset - percent) * (1.0F / (1.0F - percent));
                        GoogleTaskJavaSampleDialog.this.alphaCancelButton = alphaTemp >= (float) 0 ? alphaTemp : 0.0F;
                        GoogleTaskJavaSampleDialog.this.cancelButton.setAlpha(GoogleTaskJavaSampleDialog.this.alphaCancelButton);
                        GoogleTaskJavaSampleDialog.this.cancelButton.setEnabled(GoogleTaskJavaSampleDialog.this.alphaCancelButton > 0);
                        return Unit.INSTANCE;
                    }
                });
                return Unit.INSTANCE;
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissWithBehavior();
            }
        });

        cornerRadiusSeekBar = view.findViewById(R.id.corner_radius_seek_bar);
        cornerRadiusSeekBar.setMax(200);
        cornerRadiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                changeCornerRadius(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        navigation = view.findViewById(R.id.navigation_bar_accent);
        navigation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DialogFragmentExtensionKt.changeNavigationIconColor(GoogleTaskJavaSampleDialog.this, isChecked);
            }
        });
        statusBar = view.findViewById(R.id.status_bar_accent);
        statusBar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DialogFragmentExtensionKt.changeStatusBarIconColor(GoogleTaskJavaSampleDialog.this, isChecked);
            }
        });

        colorSeekBar = view.findViewById(R.id.colorSlider);
        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition,
                                              int alphaBarPosition,
                                              int color) {
                if (!colorSeekBar.isFirstDraw()) {
                    changeBackgroundColor(color);
                }
            }
        });

        return view;
    }

    @NotNull
    @Override
    public BottomDrawerDialog configureBottomDrawer() {
        BottomDrawerDialog.Builder builder = new BottomDrawerDialog.Builder(requireContext());

        View handleView = new PlainHandleView(requireContext());
        int widthHandle = getResources().getDimensionPixelSize(R.dimen.bottom_sheet_handle_width);
        int heightHandle =
                getResources().getDimensionPixelSize(R.dimen.bottom_sheet_handle_height);
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(widthHandle, heightHandle, Gravity.CENTER_HORIZONTAL);

        params.topMargin =
                getResources().getDimensionPixelSize(R.dimen.bottom_sheet_handle_top_margin);
        handleView.setLayoutParams(params);

        builder.setHandleView(handleView);

        return builder.build();
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat("alphaCancelButton", alphaCancelButton);
    }

    @Override
    public void onViewStateRestored(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        this.alphaCancelButton = savedInstanceState != null ? savedInstanceState.getFloat("alphaCancelButton") : 0.0F;
        cancelButton.setAlpha(this.alphaCancelButton);
        cancelButton.setEnabled(this.alphaCancelButton > 0);
    }
}

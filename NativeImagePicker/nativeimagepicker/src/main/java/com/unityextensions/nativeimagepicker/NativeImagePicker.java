package com.unityextensions.nativeimagepicker;

import android.app.Activity;
import android.content.Intent;

import com.unity3d.player.UnityPlayer;

@SuppressWarnings("unused")
public class NativeImagePicker {
    @SuppressWarnings("unused")
    public static void FromLibrary() {
        Activity a = UnityPlayer.currentActivity;

        Intent intent = new Intent(a, NativeImagePickerActivity.class);
        intent.putExtra("fromCamera", false);
        a.startActivity(intent);
    }

    @SuppressWarnings("unused")
    public static void FromCamera() {
        Activity a = UnityPlayer.currentActivity;

        Intent intent = new Intent(a, NativeImagePickerActivity.class);
        intent.putExtra("fromCamera", true);
        a.startActivity(intent);
    }
}

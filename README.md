# UnityNativeImagePicker
Unity plugin for getting Images from Camera roll on Android or Photo Library on iOS. Supports taking photos with the camera.

## Known issues

- App needs AndroidManifest changes if it will be running on Android 7.0 (N)+.


        <provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="REPLACE_ME_WITH_ANDROID_APP_ID.provider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/provider_paths"/>
        </provider>

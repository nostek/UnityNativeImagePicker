# UnityNativeImagePicker
Unity plugin for getting Images from Camera roll on Android or Photo Library on iOS. Supports taking photos with the camera.

## Information
Updated to AndroidX

## Install
- App needs AndroidManifest changes.

        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="REPLACE_ME_WITH_ANDROID_APP_ID.provider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/provider_paths"/>
        </provider>

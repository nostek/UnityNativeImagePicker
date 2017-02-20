using System.Collections;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using UnityEngine;

public static class NativeImagePicker
{
	const string GameObjectName = "NIP_599349_GO";

	public delegate void CallbackImagePicked(string filePath);

	public class NativeImagePickerBehaviour : MonoBehaviour
	{
		public NativeImagePicker.CallbackImagePicked Callback = null;

		public void CallbackSelectedImage(string url)
		{
			url = (string.IsNullOrEmpty(url)) ? null : url;

			Callback(url);

			GameObject.Destroy(this.gameObject);
		}
	}

	#if UNITY_IOS
	[DllImport("__Internal")]
	static extern void _CNativeImagePickerFromLibrary(bool allowEditing);

	[DllImport("__Internal")]
	static extern void _CNativeImagePickerFromCamera(bool allowEditing);
	#endif

	public static void FromLibrary(CallbackImagePicked onPicked, bool allowedEditing = false)
	{
		#if UNITY_EDITOR
		onPicked(null);
		#elif UNITY_IOS
		GameObject go = new GameObject(GameObjectName);
		go.AddComponent<NativeImagePickerBehaviour>().Callback = onPicked;

		_CNativeImagePickerFromLibrary(allowedEditing);
		#endif
	}

	public static void FromCamera(CallbackImagePicked onPicked, bool allowedEditing = false)
	{
		#if UNITY_EDITOR
		onPicked(null);
		#elif UNITY_IOS
		GameObject go = new GameObject(GameObjectName);
		go.AddComponent<NativeImagePickerBehaviour>().Callback = onPicked;

		_CNativeImagePickerFromCamera(allowedEditing);
		#endif
	}
}

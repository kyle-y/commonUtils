package com.example.administrator.common.commonUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;

import static com.example.administrator.common.commonUtils.FileUtils.getDataColumn;
import static com.example.administrator.common.commonUtils.FileUtils.isDownloadsDocument;
import static com.example.administrator.common.commonUtils.FileUtils.isExternalStorageDocument;
import static com.example.administrator.common.commonUtils.FileUtils.isMediaDocument;

/**
 * Created by Administrator on 2017/1/17.
 */

public class PhotoUtils {
    /**
     * 从相册获取图片
     * 4.4以上版本和4.4以下版本调用的action不同
     * @see "http://blog.csdn.net/tempersitu/article/details/20557383"
     */
    public static void choicePicFromAlbum(Activity activity, int requestCode) {
        Intent albumIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            albumIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        } else {
            albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        }
        albumIntent.addCategory(Intent.CATEGORY_OPENABLE);
        albumIntent.setType("image/*");
        activity.startActivityForResult(albumIntent, requestCode);
    }

    /**
     * 拍照后获取图片
     * @param activity
     * @param cameraPhotoFile 照片的文件的储存路径，只要回调成功，可以直接获得
     * @param requestCode
     */
    public static void choicePicFromCamera(Activity activity, File cameraPhotoFile, int requestCode) {
        // 来自相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 下面这句指定调用相机拍照后的照片存储的路径，这样通过这个uri就可以得到这个照片了
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraPhotoFile));
        activity.startActivityForResult(cameraIntent, requestCode);// CAMERA_OK是用作判断返回结果的标识
    }

    /**
     * 通过相册选取返回的数据得到uri
     *
     * @param context
     * @param data
     * @return
     */
    public static Uri getUriFromAlbum(Context context, Intent data){
        if (data == null) return null;
        Uri uri = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            uri = Uri.parse(getPath(context, data.getData()));
        } else {
            uri = data.getData();
        }
        return uri;
    }

    /**
     * 通过uri得到对应的照片
     * @param uri
     * @return
     */
    public static Bitmap getPhotoFromUri(Uri uri){
        Bitmap bitmap = BitmapFactory.decodeFile(uri.toString());
        if (bitmap != null) {
            bitmap = ImageUtils.rotateBitmap(bitmap, getPhotoDegreeByUri(uri));
        }
        return bitmap;
    }

    /**
     * 4.4得到的uri,需要以下方法来获取文件的路径
     * @param context
     * @param uri
     * @return
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    /**
     * 通过photo的uri来得到图片的角度，从而判断是否需要进行旋转操作
     * @param uri
     * @return
     */
    public static int getPhotoDegreeByUri(Uri uri) {
        int degree = 0;
        int orientation = getOrientation(uri);
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            degree = 90;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            degree = 180;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            degree = 270;
        }
        return degree;
    }

    /**
     * 得到图片的方向
     * @param photoUri
     * @return
     */
    public static int getOrientation(final Uri photoUri) {
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(photoUri.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}

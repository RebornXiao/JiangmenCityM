package com.liqi.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

import com.liqi.R;
import com.liqi.view.slidingmenu.SlidingMenu;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by baby on 2016/4/7.
 */
public class ViewShowUtil {
    public static boolean show(List<View> list, int[] args) {
        if (list != null && list.size() > 0 && args != null & args.length > 0) {
            int size = args.length;
            for (int position = 0; position < size; position++) {
                int arg = args[position];
                if (arg == 1) {
                    list.get(position).setVisibility(View.VISIBLE);
                } else if (arg == 0) {
                    list.get(position).setVisibility(View.GONE);
                } else if (arg == 2) {
                    list.get(position).setVisibility(View.INVISIBLE);
                }
            }
            return true;
        }
        return false;
    }


    //view 转bitmap
    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    public static void setSlidingMenu(SlidingMenu menu) {
        // configure the SlidingMenu
        menu.setMode(SlidingMenu.LEFT);
        // 设置触摸屏幕的模式
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        // 设置滑动菜单视图的宽度
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 设置渐入渐出效果的值
        menu.setFadeDegree(0.35f);
        /**
         * SLIDING_WINDOW will include the Title/ActionBar in the content
         * section of the SlidingMenu, while SLIDING_CONTENT does not.
         */

        //为侧滑菜单设置布局
        menu.setMenu(R.layout.all);
        menu.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {

            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                //原图从3/4增加到1倍大小
                float scale = (float) (0.75 + 0.25 * percentOpen);
                //x坐标先向屏幕左边移动1/4的视图宽度，然后再慢慢移动到0，这样来实现从屏幕左边移动进屏幕的效果
                canvas.translate(-canvas.getWidth() / 4 + percentOpen * canvas.getWidth() / 4, 0);
                //x，y方向同时放大，动画的相对中心定在“右中”
                canvas.scale(scale, scale, canvas.getWidth(), canvas.getHeight() / 2);
            }
        });

    }

    /**
     * @param context
     * @param cr
     * @param Imagepath
     * @return
     */
    public static Bitmap getImageThumbnail(Context context, ContentResolver cr, String Imagepath) {
        ContentResolver testcr = context.getContentResolver();
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID,};
        String whereClause = MediaStore.Images.Media.DATA + " = '" + Imagepath + "'";
        Cursor cursor = testcr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, whereClause,
                null, null);
        int _id = 0;
        String imagePath = "";
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        if (cursor.moveToFirst()) {
            int _idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            int _dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

            do {
                _id = cursor.getInt(_idColumn);
                imagePath = cursor.getString(_dataColumn);
            } while (cursor.moveToNext());
        }
        cursor.close();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, _id, MediaStore.Images.Thumbnails.MINI_KIND,
                options);
        return bitmap;
    }

    public static Bitmap getThumbnail(Context mContext, Uri uri, int size) throws FileNotFoundException, IOException {
        InputStream input = mContext.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_4444;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;
        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;
        double ratio = (originalSize > size) ? (originalSize / size) : 1.0;
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true;//optional
        bitmapOptions.outHeight = (int) (onlyBoundsOptions.outHeight / ratio);
        bitmapOptions.outWidth = (int) (onlyBoundsOptions.outWidth / ratio);
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;//optional
        input = mContext.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio)) + 1;
        if (k == 0) {
            return 1;
        } else {
            return k;
        }
    }

}

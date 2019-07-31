package com.localgenie.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * This is CircleTransform class
 * <p>
 *     This class is used to transform image into required circle form.
 *     this class has been called fro Join as a doctor.
 * </p>
 * @see Bitmap
 * @see Canvas
 * @see Paint
 * @see BitmapShader
 * @author 3embed
 * @version 1.0
 * @since 8/6/15
 */

public class CircleTransform extends BitmapTransformation {
	public CircleTransform(Context context) {
		super(context);
	}

	@Override protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
		return circleCrop(pool, toTransform);
	}

	private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
		if (source == null) return null;

		int size = Math.min(source.getWidth(), source.getHeight());
		int x = (source.getWidth() - size) / 2;
		int y = (source.getHeight() - size) / 2;

		// TODO this could be acquired from the pool too
		Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

		Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
		if (result == null) {
			result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
		}

		Canvas canvas = new Canvas(result);
		Paint paint = new Paint();
		paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
		paint.setAntiAlias(true);
		float r = size / 2f;
		canvas.drawCircle(r, r, r, paint);
		return result;
	}


	public String getId() {
		return getClass().getName();
	}

	@Override
	public void updateDiskCacheKey(@NonNull MessageDigest messageDigest)
	{
		getId().getClass().getName();
		messageDigest.getProvider().getName();
	}
}
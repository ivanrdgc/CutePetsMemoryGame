package com.wimjetgames.framework;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

public class WimjetGraphics {
	private AssetManager assets;
	private Bitmap frameBuffer;
	private Canvas canvas;
	private Paint paint;
	private Rect srcRect;
	private Rect dstRect;
	private HashMap<String, Bitmap> bitmaps;

	public static enum Align {
		TOP_LEFT, TOP_CENTER, TOP_RIGHT, MIDDLE_LEFT,
		MIDDLE_CENTER, MIDDLE_RIGHT, BOTTOM_LEFT,
		BOTTOM_CENTER, BOTTOM_RIGHT
	}

	public WimjetGraphics(AssetManager assets, Bitmap frameBuffer) {
		this.assets = assets;
		this.frameBuffer = frameBuffer;
		this.canvas = new Canvas(frameBuffer);
		this.paint = new Paint();
		this.srcRect = new Rect();
		this.dstRect = new Rect();
		this.bitmaps = new HashMap<String, Bitmap>();
	}

	private Bitmap getBitmap(String fileName) {
		if (this.bitmaps.containsKey(fileName)) {
			return this.bitmaps.get(fileName);
		}

		Options options = new Options();
		options.inPreferredConfig = Config.ARGB_8888;

		InputStream in = null;
		Bitmap bitmap = null;
		try {
			in = this.assets.open(fileName);
			bitmap = BitmapFactory.decodeStream(in, null, options);
			if (bitmap == null)
				throw new RuntimeException("Couldn't load bitmap from asset '" + fileName + "'");

		} catch (IOException e) {
			throw new RuntimeException("Couldn't load bitmap from asset '" + fileName + "'");

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {}
			}
		}

		this.bitmaps.put(fileName, bitmap);
		
		return bitmap;
	}

	public void clearScreen(int color) {
		this.canvas.drawRGB((color & 0xff0000) >> 16, (color & 0xff00) >> 8, (color & 0xff));
	}

	public void drawLine(float x, float y, float x2, float y2, int color) {
		x = this.frameBuffer.getWidth() * x / 100;
		y = this.frameBuffer.getHeight() * y / 100;
		x2 = this.frameBuffer.getWidth() * x2 / 100;
		y2 = this.frameBuffer.getHeight() * y2 / 100;

		this.paint.setColor(color);
		this.canvas.drawLine(x, y, x2, y2, this.paint);
	}

	public void drawRect(float x, float y, float width, float height, int color) {
		x = getWidth() * x / 100;
		y = getHeight() * y / 100;

		width = getWidth() * width / 100;
		height = getHeight() * height / 100;

		this.paint.setColor(color);
		this.paint.setStyle(Style.FILL);
		this.canvas.drawRect(x, y, x + width - 1, y + height - 1, this.paint);
	}

	public void drawARGB(int a, int r, int g, int b) {
		this.paint.setStyle(Style.FILL);
		this.canvas.drawARGB(Math.round((100f-a)*255f/100f), Math.round((100f-r)*255f/100f), Math.round((100f-g)*255f/100f), Math.round((100f-b)*255f/100f));
	}

	public void drawString(String text, float x, float y, int size, int color, Align align) {
		this.paint.setTextSize(size);
		this.paint.setColor(color);

		x = getWidth() * x / 100;
		y = getHeight() * y / 100;

		if (align == Align.TOP_LEFT || align == Align.MIDDLE_LEFT || align == Align.BOTTOM_LEFT) {
			this.paint.setTextAlign(Paint.Align.LEFT);

		} else if (align == Align.TOP_CENTER || align == Align.MIDDLE_CENTER || align == Align.BOTTOM_CENTER) {
			this.paint.setTextAlign(Paint.Align.CENTER);

		} else if (align == Align.TOP_RIGHT || align == Align.MIDDLE_RIGHT || align == Align.BOTTOM_RIGHT) {
			this.paint.setTextAlign(Paint.Align.RIGHT);
		}

		if (align == Align.TOP_LEFT || align == Align.TOP_CENTER || align == Align.TOP_RIGHT) {
			this.canvas.drawText(text, x, y, this.paint);

		} else {
			this.paint.getTextBounds(text, 0, text.length(), this.dstRect);

			if (align == Align.MIDDLE_LEFT || align == Align.MIDDLE_CENTER || align == Align.MIDDLE_RIGHT) {
				this.canvas.drawText(text, x, y + this.dstRect.height() * 0.5f, this.paint);

			} else {
				this.canvas.drawText(text, x, y + this.dstRect.height(), this.paint);
			}
		}
	}

	public void drawImage(String image, float x, float y, float srcX, float srcY, float srcWidth, float srcHeight) {
		drawImage(image, x, y, -100, -100, srcX, srcY, srcWidth, srcHeight, 0, Align.TOP_LEFT);
	}

	public void drawImage(String image, float x, float y) {
		drawImage(image, x, y, -100, -100, 0, 0, 100, 100, 0, Align.TOP_LEFT);
	}

	public void drawImage(String image, float x, float y, Align align) {
		drawImage(image, x, y, -100, -100, 0, 0, 100, 100, 0, align);
	}

	public void drawScaledImage(String image, float x, float y, float width, float height, float srcX, float srcY, float srcWidth, float srcHeight) {
		drawImage(image, x, y, width, height, srcX, srcY, srcWidth, srcHeight, 0, Align.TOP_LEFT);
	}

	public void drawImage(String image, float x, float y, float width, float height, float srcX, float srcY, float srcWidth, float srcHeight, float alpha, Align align) {
		Bitmap bitmap = getBitmap(image);

		x = this.getWidth() * x / 100;
		y = this.getHeight() * y / 100;

		if (width >= 0) {
			width = this.frameBuffer.getWidth() * width / 100;

		} else {
			width = bitmap.getWidth() * (-width) / 100;
		}

		if (height >= 0) {
			height = this.frameBuffer.getHeight() * height / 100;

		} else {
			height = bitmap.getHeight() * (-height) / 100;
		}

		srcX = bitmap.getWidth() * srcX / 100;
		srcY = bitmap.getHeight() * srcY / 100;

		srcWidth = bitmap.getWidth() * srcWidth / 100;
		srcHeight = bitmap.getHeight() * srcHeight / 100;

		if (align == Align.TOP_CENTER || align == Align.MIDDLE_CENTER || align == Align.BOTTOM_CENTER) {
			x -= width / 2;

		} else if (align == Align.TOP_RIGHT || align == Align.MIDDLE_RIGHT || align == Align.BOTTOM_RIGHT) {
			x -= width;
		}

		if (align == Align.MIDDLE_LEFT || align == Align.MIDDLE_CENTER || align == Align.MIDDLE_RIGHT) {
			y -= height / 2;

		} else if (align == Align.BOTTOM_LEFT || align == Align.BOTTOM_CENTER || align == Align.BOTTOM_RIGHT) {
			y -= height;
		}

		this.srcRect.left = Math.round(srcX);
		this.srcRect.top = Math.round(srcY);
		this.srcRect.right = Math.round(srcX + srcWidth);
		this.srcRect.bottom = Math.round(srcY + srcHeight);

		this.dstRect.left = Math.round(x);
		this.dstRect.top = Math.round(y);
		this.dstRect.right = Math.round(x + width);
		this.dstRect.bottom = Math.round(y + height);

		this.paint.setAlpha(Math.round((100f-alpha)*255f/100f));

		this.canvas.drawBitmap(bitmap, this.srcRect, this.dstRect, this.paint);
	}

	public int getWidth() {
		return this.frameBuffer.getWidth();
	}

	public int getHeight() {
		return this.frameBuffer.getHeight();
	}
}
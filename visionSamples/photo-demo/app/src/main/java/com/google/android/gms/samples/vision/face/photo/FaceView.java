/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.samples.vision.face.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

/**
 * View which displays a bitmap containing a face along with overlay graphics that identify the
 * locations of detected facial landmarks.
 */
public class FaceView extends View {
    private Bitmap mBitmap;
    private SparseArray<Face> mFaces;

    public FaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Sets the bitmap background and the associated face detections.
     */
    public void setContent(Bitmap bitmap, SparseArray<Face> faces) {
        mBitmap = bitmap;
        mFaces = faces;
        invalidate();
    }

    /**
     * Draws the bitmap background and the associated face landmarks.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if ((mBitmap != null) && (mFaces != null)) {
            double scale = drawBitmap(canvas);
            drawFaceAnnotations(canvas, scale);
        }
    }

    /**
     * Draws the bitmap background, scaled to the device size.  Returns the scale for future use in
     * positioning the facial landmark graphics.
     */
    private double drawBitmap(Canvas canvas) {
        double viewWidth = canvas.getWidth();
        double viewHeight = canvas.getHeight();
        double imageWidth = mBitmap.getWidth();
        double imageHeight = mBitmap.getHeight();
        double scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight);

        Rect destBounds = new Rect(0, 0, (int)(imageWidth * scale), (int)(imageHeight * scale));
        canvas.drawBitmap(mBitmap, null, destBounds, null);
        return scale;
    }

    /**
     * Draws a small circle for each detected landmark, centered at the detected landmark position.
     * <p>
     *
     * Note that eye landmarks are defined to be the midpoint between the detected eye corner
     * positions, which tends to place the eye landmarks at the lower eyelid rather than at the
     * pupil position.
     */
    private void drawFaceAnnotations(Canvas canvas, double scale) {
        Paint paint = new Paint();

        for (int i = 0; i < mFaces.size(); ++i) {
            Face face = mFaces.valueAt(i);

            int fx = (int) (face.getPosition().x * scale);
            int fy = (int) (face.getPosition().y * scale);
            int fw = (int) (face.getWidth() * scale);
            int fh = (int) (face.getHeight() * scale);

            paint.setColor(Color.MAGENTA);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            canvas.drawRect(fx, fy, fx + fw, fy + fh, paint);

            for (Landmark landmark : face.getLandmarks()) {

                paint.setColor(Color.GREEN);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(5);

                int cx = (int) (landmark.getPosition().x * scale);
                int cy = (int) (landmark.getPosition().y * scale);

                // Left eye
                if (landmark.getType() == Landmark.LEFT_EYE) {
                    // Closed
                    if (face.getIsLeftEyeOpenProbability() < .4) {
                        paint.setColor(Color.RED);
                        canvas.drawCircle(cx, cy, 10, paint);
                    }

                    // Open
                    else {
                        paint.setColor(Color.WHITE);
                        paint.setStyle(Paint.Style.FILL);
                        canvas.drawCircle(cx, cy, 60, paint);

                        paint.setColor(Color.BLACK);
                        canvas.drawCircle(cx, cy, 10, paint);
                    }
                }

                // Right eye
                else if (landmark.getType() == Landmark.RIGHT_EYE) {
                    // Closed
                    if (face.getIsRightEyeOpenProbability() < .4) {
                        paint.setColor(Color.RED);
                        canvas.drawCircle(cx, cy, 10, paint);
                    }
                    // Open
                    else {
                        paint.setColor(Color.WHITE);
                        paint.setStyle(Paint.Style.FILL);
                        canvas.drawCircle(cx, cy, 60, paint);

                        paint.setColor(Color.BLACK);
                        canvas.drawCircle(cx, cy, 10, paint);
                    }
                }

                // Everything else in green
                else {
                    paint.setColor(Color.GREEN);
                    canvas.drawCircle(cx, cy, 10, paint);
                }
            }
        }
    }
}

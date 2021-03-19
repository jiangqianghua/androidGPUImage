package com.jqh.gpuimagelib.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.FaceDetector;
import android.util.Log;
import android.widget.ImageView;

public class FaceUtils {

    public static RectF[] detecotrFace(Bitmap bitmap) {
        //设定最大可查的人脸数量
        int MAX_FACES = 1;
        FaceDetector faceDet = new FaceDetector(bitmap.getWidth(), bitmap.getHeight(), MAX_FACES);
        //将人脸数据存储到facelist中
        FaceDetector.Face[] faceList = new FaceDetector.Face[MAX_FACES];
        faceDet.findFaces(bitmap, faceList);

        //  FaceDetector API文档我们发现，它查找人脸的原理是：找眼睛。
        //  它返回的人脸数据face，通过调用public float eyesDistance ()，public void getMidPoint (PointF point)，
        //  我们可以得到探测到的两眼间距，以及两眼中心点位置（MidPoint）。
        //  public float confidence () 可以返回该人脸数据的可信度(0~1)，这个值越大，该人脸数据的准确度也就越高。
        RectF[] faceRects = new RectF[faceList.length];
        for (int i = 0; i < faceList.length; i++) {
            FaceDetector.Face face = faceList[i];
            Log.d("FaceDet", "Face [" + face + "]");
            if (face != null) {
                //confidence标识一个匹配度，在0~1区间，越接近1，表示匹配越高。如果需要可以加上这个判断条件
                //这里不做判断
                Log.d("FaceDet", "Face [" + i + "] - Confidence [" + face.confidence() + "]");
                //获取两眼中心点的坐标位置
                PointF pf = new PointF();
                face.getMidPoint(pf);
                //这里的框，参数分别是：左上角的X,Y  右下角的X,Y
                //也就是左上角（r.left,r.top），右下角( r.right,r.bottom)。作为定位，确定这个框的格局。
                RectF r = new RectF();
                r.left = pf.x - face.eyesDistance() / 2;
                r.right = pf.x + face.eyesDistance() / 2;
                r.top = pf.y - face.eyesDistance() / 2;
                r.bottom = pf.y + face.eyesDistance() / 2;
                faceRects[i] = r;
            }
        }
        return faceRects;
    }

    public static void showFace(Bitmap bitmap, ImageView imageView)
    {
        bitmap = ImageUtils.compress(bitmap);
        bitmap = bitmap.copy(Bitmap.Config.RGB_565, true);
        //设定最大可查的人脸数量
        int MAX_FACES=1;
        FaceDetector faceDet = new FaceDetector(bitmap.getWidth(), bitmap.getHeight(), MAX_FACES);
        //将人脸数据存储到facelist中
        FaceDetector.Face[] faceList = new FaceDetector.Face[MAX_FACES];
        faceDet.findFaces(bitmap, faceList);

        //  FaceDetector API文档我们发现，它查找人脸的原理是：找眼睛。
        //  它返回的人脸数据face，通过调用public float eyesDistance ()，public void getMidPoint (PointF point)，
        //  我们可以得到探测到的两眼间距，以及两眼中心点位置（MidPoint）。
        //  public float confidence () 可以返回该人脸数据的可信度(0~1)，这个值越大，该人脸数据的准确度也就越高。
        RectF[] faceRects=new RectF[faceList.length];
        for (int i=0; i < faceList.length; i++) {
            FaceDetector.Face face = faceList[i];
            Log.d("FaceDet", "Face ["+face+"]");
            if (face != null) {
                //confidence标识一个匹配度，在0~1区间，越接近1，表示匹配越高。如果需要可以加上这个判断条件
                //这里不做判断
                Log.d("FaceDet", "Face ["+i+"] - Confidence ["+face.confidence()+"]");
                //获取两眼中心点的坐标位置
                PointF pf = new PointF();
                face.getMidPoint(pf);
                //这里的框，参数分别是：左上角的X,Y  右下角的X,Y
                //也就是左上角（r.left,r.top），右下角( r.right,r.bottom)。作为定位，确定这个框的格局。
                RectF r = new RectF();
                r.left = pf.x - face.eyesDistance() / 2;
                r.right = pf.x + face.eyesDistance() / 2;
                r.top = pf.y - face.eyesDistance() / 2;
                r.bottom = pf.y + face.eyesDistance() / 2;
                faceRects[i] = r;
                LogUtils.logd("开始检测人脸 left=" + r.left + " top=" + r.top + " r=" + r.right + " bottom=" + r.bottom);
                //画框:对原图进行处理，并在图上显示人脸框。
                Canvas canvas = new Canvas(bitmap);
                Paint p = new Paint();
                p.setAntiAlias(true);
                p.setStrokeWidth(2);
                p.setStyle(Paint.Style.STROKE);
                p.setColor(Color.BLUE);
                //画一个圈圈
                canvas.drawCircle(r.left, pf.y, 10, p);
                canvas.drawCircle(r.right, pf.y, 10, p);
                //画框
                canvas.drawRect(r, p);
                //图片显示
                imageView.setImageBitmap(bitmap);
            }
        }


    }

}

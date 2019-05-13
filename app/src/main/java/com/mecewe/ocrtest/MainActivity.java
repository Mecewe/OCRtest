package com.mecewe.ocrtest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

//import cn.hugeterry.updatefun.UpdateFunGO;
//import cn.hugeterry.updatefun.config.UpdateKey;
import cn.hugeterry.updatefun.UpdateFunGO;
import cn.hugeterry.updatefun.config.UpdateKey;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    String localTempImgFileName;
    String localTempImgDir="TestCamera";
    private ImageView  img;
    private ImageButton photoButton;
    private TextView imgdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UpdateKey.API_TOKEN = "3bf4511d24a7f70bd0131c6797a42b4e";
        UpdateKey.APP_ID = "5cb6a890959d693a0b7aa475";
        UpdateKey.DialogOrNotification=UpdateKey.WITH_DIALOG;
        UpdateFunGO.init(this);
//        UpdateKey.WITH_DIALOG=true;
//        UpdateKey.WITH_NOTIFITION=true;
//        UpdateApk.init(this);
//下载方式:
//UpdateKey.DialogOrNotification=UpdateKey.WITH_DIALOG;通过Dialog来进行下载
//UpdateKey.DialogOrNotification=UpdateKey.WITH_NOTIFITION;通过通知栏来进行下载(默认)
//        UpdateFunGO.init(this);

        Button jump =(Button)findViewById(R.id.jump);
        imgdata = (TextView)findViewById(R.id.text_data);
        imgdata.setMovementMethod(ScrollingMovementMethod.getInstance());
        img = (ImageView)findViewById(R.id.img);
        photoButton = (ImageButton)findViewById(R.id.scan);
//        camera = new Camera.Builder()
//                .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
//                .setTakePhotoRequestCode(1)
//                .setDirectory("pics")
//                .setName("singpools_" + System.currentTimeMillis())
//                .setImageFormat(Camera.IMAGE_JPEG)
//                .setCompression(75)
//                .setImageHeight(300)
//                .build(this);

        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Base64Activity.class);
                startActivity(intent);
            }
        });

        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                File fos=null;
                try {
                    fos=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"zhycheng.jpg");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Uri u=Uri.fromFile(fos);
                Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                i.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                i.putExtra(MediaStore.EXTRA_OUTPUT, u);

                startActivityForResult(i, 9);
            }
        });

        // For image
// Get the bitmap and image path onActivityResult of an activity or fragment
//        @Override
//        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//            super.onActivityResult(requestCode, resultCode, data);
//            if(requestCode == Camera.REQUEST_TAKE_PHOTO){
//                Bitmap bitmap = camera.getCameraBitmap();
//                if(bitmap != null) {
//                    mimageIC.setImageBitmap(bitmap);
//                }else{
//                    Toast.makeText(this.getApplicationContext(),"Picture not taken!",Toast.LENGTH_SHORT).show();
//                }
//            }
//        }


    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode==RESULT_OK)
        {
            if(requestCode==9)
            {
                File  bb=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"zhycheng.jpg");
                Log.e("bb",bb.toString());
                Log.e("bb",bb.getPath());
                fileToBase64(bb.getPath());
                Intent i=new Intent("com.android.camera.action.CROP");
                i.setType("image/*");
                //i.putExtra("data", bb);
                i.setDataAndType(Uri.fromFile(bb), "image/jpeg");
                i.putExtra("crop", "true");
                i.putExtra("aspectX", 1);
                i.putExtra("aspectY", 1);
                i.putExtra("outputX", 500);
                i.putExtra("outputY", 500);
                i.putExtra("return-data", true);
                this.startActivityForResult(i, 7);
            }
            if(requestCode==7)
            {
                Bitmap bb=data.getParcelableExtra("data");
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                bb.compress(Bitmap.CompressFormat.JPEG,100,bos);
//                byte[] bb1 = bos.toByteArray();
//                Log.e("bb1",bb1.toString());
//
//                String image = Base64.encodeToString(bb1, Base64.NO_WRAP);
//                Log.e("base64",image);
//                imgdata.setText(image);

//                String imagefile="";
//                try {
//                    bitmapToFile(imagefile,bb,100);
//                    fileToBase64(imagefile);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                Log.e("bb7",bb.toString());
                img.setImageBitmap(bb);
                Log.e("img",img.toString());
                getImgBase64(img);
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        //更新
//        UpdateApk.destory();
        //图片读取
        File f=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"zhycheng.jpg");
        if(f.exists())
        {
//            Log.e("f",f.getPath());
//            Log.e("f",f.toString());
//            fileToBase64(f.toString());
            f.delete();
        }


    }

    /**
     * 图片文件转化成base64字符串
     */
    public static String fileToBase64(String imgFile) {
        InputStream in = null;
        String base64 = null;
        // 读取图片字节数组
        try {
            if(imgFile==null||"".equals(imgFile)){
                imgFile="uploaddir/file/default.png";
            }
            in = new FileInputStream(imgFile);
            byte[] bytes = new byte[in.available()];
            int length = in.read(bytes);
            base64 = Base64.encodeToString(bytes, 0, length, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //关闭输入流
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.e("base64|||",base64);
        return base64;
    }

    /**
     * bitmap保存为file
     */
    public static void bitmapToFile(String filePath,
                                    Bitmap bitmap, int quality) throws IOException {
        if (bitmap != null) {
            File file = new File(filePath.substring(0,
                    filePath.lastIndexOf(File.separator)));
            if (!file.exists()) {
                file.mkdirs();
            }
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(filePath));
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, bos);
            bos.flush();
            bos.close();
        }
    }


    public String getImgBase64(ImageView imageView){
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
        byte[] bb = bos.toByteArray();
        final String image = Base64.encodeToString(bb, Base64.NO_WRAP);
//        String image2 = Base64.encodeToString(Bitmap.CompressFormat.PNG, Base64.NO_WRAP);
        Log.e("base64",image);

//        Log.e("base64 2",image2);
        imgdata.setText(image);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sendBdWithOKhttp(image);
                } catch (Exception e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }).start();
        return image;
    }

     public void sendBdWithOKhttp(String img) {
        HttpUtil.sendPost(img, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                int i = 0;
                Log.e("data", "l=" + responseData.length());
                Log.e("data", responseData);
//                Gson gson = new Gson();
//                JsonParser parser = new JsonParser();
//                JsonArray jsonArray = parser.parse(responseData).getAsJsonArray();
//                num=jsonArray.size();
//                Log.e("|||||",String.valueOf(jsonArray.size()));
//                Log.e("data", jsonArray.toString());
//
//                showResponse(responseData);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateFunGO.onResume(this);
    }
    @Override
    protected void onStop() {
        super.onStop();
        UpdateFunGO.onStop(this);
    }

}

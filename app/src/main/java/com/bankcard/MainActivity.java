package com.bankcard;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.camera.CameraActivity;

public class MainActivity extends AppCompatActivity {

    private TextView mCradText;
    private TextView mBankText;
    private Button mCradBtn;
    private Button mBankBtn;

    private static final int REQUEST_CODE_BANKCARD = 111;
    private AlertDialog.Builder alertDialog;
    private boolean hasGotToken = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alertDialog = new AlertDialog.Builder(this);
        initvivew();
        initdata();
    }

    private void initvivew() {

        mCradText = (TextView) findViewById(R.id.card_text);
        mBankText = (TextView) findViewById(R.id.bank_text);
        mCradBtn = (Button) findViewById(R.id.card_btn);
        mBankBtn = (Button) findViewById(R.id.bank_btn);
    }

    private void initdata() {
        //银行卡识别
     mBankBtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             if (!checkTokenStatus()) {
                 return;
             }
             Intent intent = new Intent(MainActivity.this, CameraActivity.class);
             intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                     FileUtil.getSaveFile(getApplication()).getAbsolutePath());
             intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                     CameraActivity.CONTENT_TYPE_BANK_CARD);
             startActivityForResult(intent, REQUEST_CODE_BANKCARD);
         }
     });
     mCradBtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             // 身份证识别

                     Intent intent = new Intent(MainActivity.this, CardIDActivity.class);
                     startActivity(intent);

         }
     });
        initAccessTokenWithAkSk();//初始化
    }

    private boolean checkTokenStatus() {
        if (!hasGotToken) {
            Toast.makeText(getApplicationContext(), "token还未成功获取", Toast.LENGTH_LONG).show();
        }
        return hasGotToken;
    }
    private void initAccessTokenWithAkSk() {
        OCR.getInstance().initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                Log.e("11","22"+"----"+token);
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                Log.e("11","222"+"----");
                alertText("AK，SK方式获取token失败", error.getMessage());
            }
        }, getApplicationContext(), "8EBGhAagcasNmiw3bO6uZ4Qs", "tm1W8El31mWgCEe0SkhTsqPTa6VFElCM");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BANKCARD && resultCode == Activity.RESULT_OK) {
            RecognizeService.recBankCard(FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }
    }

    private void alertText(final String title, final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("确定", null)
                        .show();
            }
        });
    }

    private void infoPopText(final String result) {
        alertText("", result);
    }
}

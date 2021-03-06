package com.bc.wechat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;
import com.bc.wechat.widget.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

public class UpdateWxIdActivity extends FragmentActivity {
    private EditText mWxIdEt;
    private TextView mSaveTv;
    private VolleyUtil volleyUtil;
    LoadingDialog dialog;
    User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_wx_id);

        PreferencesUtil.getInstance().init(this);
        user = PreferencesUtil.getInstance().getUser();
        volleyUtil = VolleyUtil.getInstance(this);
        dialog = new LoadingDialog(UpdateWxIdActivity.this);
        initView();

        mSaveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setMessage(getString(R.string.saving));
                dialog.show();
                String userId = user.getUserId();
                String userWxId = mWxIdEt.getText().toString();
                updateUserWxId(userId, userWxId);
            }
        });
    }

    private void initView() {
        mWxIdEt = findViewById(R.id.et_wx_id);
        mSaveTv = findViewById(R.id.tv_save);

        mWxIdEt.setText(user.getUserWxId());
        // 光标移至最后
        CharSequence charSequence = mWxIdEt.getText();
        if (charSequence instanceof Spannable) {
            Spannable spanText = (Spannable) charSequence;
            Selection.setSelection(spanText, charSequence.length());
        }
        mWxIdEt.addTextChangedListener(new TextChange());
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String newWxId = mWxIdEt.getText().toString();
            String oldWxId = user.getUserWxId() == null ? "" : user.getUserWxId();
            // 是否填写
            boolean isWxIdHasText = mWxIdEt.length() > 0;
            // 是否修改
            boolean isWxIdChanged = !oldWxId.equals(newWxId);

            if (isWxIdHasText && isWxIdChanged) {
                mSaveTv.setTextColor(0xFFFFFFFF);
                mSaveTv.setEnabled(true);
            } else {
                mSaveTv.setTextColor(0xFFD0EFC6);
                mSaveTv.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    public void back(View view) {
        finish();
    }

    private void updateUserWxId(final String userId, final String userWxId) {
        String url = Constant.BASE_URL + "users/" + userId + "/userWxId";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userWxId", userWxId);

        volleyUtil.httpPutRequest(url, paramMap, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                dialog.dismiss();
                setResult(RESULT_OK);
                user.setUserWxId(userWxId);
                PreferencesUtil.getInstance().setUser(user);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dialog.dismiss();
                if (volleyError instanceof NetworkError) {
                    Toast.makeText(UpdateWxIdActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    return;
                } else if (volleyError instanceof TimeoutError) {
                    Toast.makeText(UpdateWxIdActivity.this, R.string.network_time_out, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}

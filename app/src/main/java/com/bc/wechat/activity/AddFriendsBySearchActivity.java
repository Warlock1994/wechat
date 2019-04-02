package com.bc.wechat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bc.wechat.R;
import com.bc.wechat.cons.Constant;
import com.bc.wechat.entity.User;
import com.bc.wechat.utils.PreferencesUtil;
import com.bc.wechat.utils.VolleyUtil;

public class AddFriendsBySearchActivity extends FragmentActivity {

    private static final String TAG = "AddFriendsBySearch";

    private EditText mSearchEt;
    private RelativeLayout mSearchRl;
    private TextView mSearchTv;

    private VolleyUtil volleyUtil;
    ProgressDialog dialog;
    User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends_by_search);
        initView();
        PreferencesUtil.getInstance().init(this);
        user = PreferencesUtil.getInstance().getUser();
        volleyUtil = VolleyUtil.getInstance(this);
        dialog = new ProgressDialog(AddFriendsBySearchActivity.this);
        mSearchRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setMessage("正在查找联系人...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
                String keyword = mSearchEt.getText().toString().trim();
                searchUser(keyword);
            }
        });
    }

    private void initView() {
        mSearchEt = findViewById(R.id.et_search);
        mSearchRl = findViewById(R.id.rl_search);
        mSearchTv = findViewById(R.id.tv_search);

        mSearchEt.addTextChangedListener(new TextChange());
    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            boolean searchHasText = mSearchEt.getText().toString().length() > 0;
            if (searchHasText) {
                mSearchRl.setVisibility(View.VISIBLE);
                mSearchTv.setText(mSearchEt.getText().toString().trim());
            } else {
                mSearchRl.setVisibility(View.GONE);
                mSearchTv.setText("");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    public void back(View view) {
        finish();
    }

    private void searchUser(String keyword) {
        String userId = user.getUserId();
        String url = Constant.BASE_URL + "users/searchForAddFriends?keyword=" + keyword + "&userId=" + userId;
        volleyUtil.httpGetRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "server response: " + response);
                User user = JSON.parseObject(response, User.class);
                Log.d(TAG, "userId:" + user.getUserId());
                Intent intent = new Intent(AddFriendsBySearchActivity.this, UserInfoActivity.class);
                intent.putExtra("userId", user.getUserId());
                intent.putExtra("nickName", user.getUserNickName());
                intent.putExtra("avatar", user.getUserAvatar());
                intent.putExtra("sex", user.getUserSex());
                intent.putExtra("isFriend", user.getIsFriend());
                startActivity(intent);
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NetworkError) {
                    Toast.makeText(AddFriendsBySearchActivity.this, R.string.network_unavailable, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }

                int errorCode = volleyError.networkResponse.statusCode;
                switch (errorCode) {
                    case 400:
                        Toast.makeText(AddFriendsBySearchActivity.this,
                                "该用户不存在", Toast.LENGTH_SHORT)
                                .show();
                        break;
                }
                dialog.dismiss();
            }
        });
    }
}

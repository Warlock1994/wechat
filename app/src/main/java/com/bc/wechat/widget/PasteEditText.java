/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bc.wechat.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.bc.wechat.activity.WechatAlertDialog;
import com.bc.wechat.activity.ChatActivity;

/**
 * 自定义的textview，用来处理复制粘贴的消息
 *
 */
public class PasteEditText extends android.support.v7.widget.AppCompatEditText {
    private Context context;


    public PasteEditText(Context context) {
        super(context);
        this.context = context;
    }

    public PasteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public PasteEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        if (id == android.R.id.paste) {
            @SuppressWarnings("deprecation")
            ClipboardManager clip = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            String text = clip.getText().toString();
            if (text.startsWith(ChatActivity.COPY_IMAGE)) {
                text = text.replace(ChatActivity.COPY_IMAGE, "");
                Intent intent = new Intent(context, WechatAlertDialog.class);
                intent.putExtra("title", "发送以下图片？");
                intent.putExtra("forwardImage", text);
                intent.putExtra("cancel", true);
                ((Activity) context).startActivityForResult(intent, ChatActivity.REQUEST_CODE_COPY_AND_PASTE);
            }
        }
        return super.onTextContextMenuItem(id);
    }


    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (!TextUtils.isEmpty(text) && text.toString().startsWith(ChatActivity.COPY_IMAGE)) {
            setText("");
        }
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }


}

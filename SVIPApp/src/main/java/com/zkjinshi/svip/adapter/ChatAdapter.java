package com.zkjinshi.svip.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zkjinshi.base.util.ClipboardUtil;
import com.zkjinshi.base.util.DialogUtil;
import com.zkjinshi.base.util.ImageUtil;
import com.zkjinshi.base.util.TimeUtil;
import com.zkjinshi.svip.R;
import com.zkjinshi.svip.bean.BookOrder;
import com.zkjinshi.svip.utils.CacheUtil;
import com.zkjinshi.svip.utils.Constants;
import com.zkjinshi.svip.utils.EmotionType;
import com.zkjinshi.svip.utils.EmotionUtil;
import com.zkjinshi.svip.utils.FileUtil;
import com.zkjinshi.svip.utils.MediaPlayerUtil;
import com.zkjinshi.svip.utils.ProtocolUtil;
import com.zkjinshi.svip.view.ActionItem;
import com.zkjinshi.svip.view.CircleImageView;
import com.zkjinshi.svip.view.MessageSpanURL;
import com.zkjinshi.svip.view.QuickAction;
import com.zkjinshi.svip.vo.MessageVo;
import com.zkjinshi.svip.vo.MimeType;
import com.zkjinshi.svip.vo.SendStatus;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 开发者：vincent
 * 日期：2015/8/1
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class ChatAdapter extends BaseAdapter {

    public static final Pattern EMOTION_URL = Pattern.compile("\\[(\\S+?)\\]");

    private static final int VIEW_TYPE_COUNT = 2; // 总数
    private static final int TYPE_RECV_ITEM = 0; // 接收
    private static final int TYPE_SEND_ITEM = 1; // 发送

    DisplayImageOptions options, imageOptions, cardOptions; // DisplayImageOptions是用于设置图片显示的类
    private Context context;
    private LayoutInflater inflater;
    private List<MessageVo> messageList;
    private Map<String, Object> msgCacheMap = new HashMap<String, Object>();
    private boolean isDelEnabled; // ture：启用删除状态，false：不启用
    private String keyWord = "";
    private ResendListener mResendListener;

    public void setResendListener(ResendListener listener) {
        mResendListener = listener;
    }

    public ChatAdapter(Context context, List<MessageVo> messageChatList) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.setData(messageChatList);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        imageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.url_image_loading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        cardOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.url_image_loading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    public ChatAdapter(Context context, List<MessageVo> messageChatList,
                       String keyWord) {
        this(context, messageChatList);
        this.keyWord = keyWord;
    }

    public void setData(List<MessageVo> messageChatList) {
        if (null == messageChatList) {
            this.messageList = new ArrayList<>();
        } else {
            this.messageList = messageChatList;
        }
        notifyDataSetChanged();
    }

    // 获得图片位置
    public int getPicPositon(MessageVo vo, ArrayList<String> urls) {
        String url = vo.getContent();
        String mUrl = "";
        int picPostion = 0;
        for (int i = 0; i < urls.size(); i++) {
            mUrl = urls.get(i);
            if (mUrl.equals(url)) {
                picPostion = i;
                break;
            }
        }
        return picPostion;
    }

    // 获得图片集合
    public ArrayList<String> getImageUrls(List<MessageVo> messageChatList) {
        ArrayList<String> urls = new ArrayList<String>();
        MimeType mimeType = null;
        for (MessageVo messageChatVo : messageChatList) {
            String attachId = messageChatVo.getAttachId();
            if (!TextUtils.isEmpty(attachId)) {
                mimeType = messageChatVo.getMimeType();
                if (MimeType.IMAGE.equals(mimeType)) {
                    urls.add(attachId);
                }
            }
        }
        return urls;
    }

    public void upDateMsg(MessageVo msg) {
        messageList.add(msg);
        notifyDataSetChanged();
    }

    public void setDelEnabled(boolean enabled) {
        isDelEnabled = enabled;
    }

    public boolean isDelEnabled() {
        return isDelEnabled;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        MessageVo messageVo = messageList.get(position);
        String contactId = messageVo.getContactId();
        boolean isComMsg = !CacheUtil.getInstance().getUserId()
                .equals(contactId);
        return isComMsg ? TYPE_RECV_ITEM : TYPE_SEND_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MessageVo item = messageList.get(position);
        RecvViewHolder rvh = null;
        SendViewHolder svh = null;
        TipsViewHolder tvh = null;
        int itemType = getItemViewType(position);

        if (convertView == null) { // 按当前所需的样式，确定new的布局
            switch (itemType) {
                case TYPE_RECV_ITEM: // 别人发送的布局
                    convertView = inflater.inflate(R.layout.dataline_recv_item, parent, false);
                    rvh = new RecvViewHolder();
                    setViewHolder(rvh, convertView);
                    rvh.name = (TextView) convertView.findViewById(R.id.name);
                    convertView.setTag(rvh);
                    break;

                case TYPE_SEND_ITEM: // 自己发送的布局
                    convertView = inflater.inflate(R.layout.dataline_send_item,
                            parent, false);
                    svh = new SendViewHolder();
                    setViewHolder(svh, convertView);
                    svh.name = (TextView) convertView.findViewById(R.id.name);
                    svh.progressBar = (ProgressBar) convertView
                            .findViewById(R.id.sendtextprogressbar);
                    svh.errIconIv = (ImageView) convertView
                            .findViewById(R.id.erricon);
                    convertView.setTag(svh);
                    break;
            }
        } else { // 有convertView，按样式，取得不用的布局
            switch (itemType) {
                case TYPE_RECV_ITEM: // 别人发送的布局
                    rvh = (RecvViewHolder) convertView.getTag();
                    break;

                case TYPE_SEND_ITEM: // 自己发送的布局
                    svh = (SendViewHolder) convertView.getTag();
                    break;
            }
        }

        String contactName = item.getContactName();

        switch (itemType) {
            case TYPE_RECV_ITEM: // 别人发送的布局
                setViewValues(rvh, position, true);
                if (!TextUtils.isEmpty(contactName)) {
                    rvh.name.setText(contactName + "：");
                    rvh.name.setVisibility(View.VISIBLE);
                }
                break;

            case TYPE_SEND_ITEM: // 自己发送的布局
                setViewValues(svh, position, false);
                svh.name.setText("我：");
                svh.name.setVisibility(View.VISIBLE);
                SendStatus sendStatus = item.getSendStatus();
                if (SendStatus.SENDING.equals(sendStatus)) { // 正在发送
                    svh.progressBar.setVisibility(View.VISIBLE);
                    svh.errIconIv.setVisibility(View.GONE);
                } else if (SendStatus.SEND_FAIL.equals(sendStatus)) { // 发送失败
                    svh.progressBar.setVisibility(View.GONE);
                    svh.errIconIv.setVisibility(View.VISIBLE);
                    svh.errIconIv.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            showResendDialog(item);
                        }
                    });
                } else { // 发送成功
                    svh.progressBar.setVisibility(View.GONE);
                    svh.errIconIv.setVisibility(View.GONE);
                }
                break;
        }
        return convertView;
    }

    /**
     * findViewById初始化控件
     *
     * @param vh
     * @param convertView
     */
    private void setViewHolder(ViewHolder vh, View convertView) {
        vh.contentLayout = (LinearLayout) convertView.findViewById(R.id.content_layout);
        vh.head = (CircleImageView) convertView.findViewById(R.id.icon);
        vh.date = (TextView) convertView.findViewById(R.id.datetime);
        vh.msg = (TextView) convertView.findViewById(R.id.message);
        vh.img = (ImageView) convertView.findViewById(R.id.image);
        vh.voice = (ImageView) convertView.findViewById(R.id.voice);
        vh.time = (TextView) convertView.findViewById(R.id.tv_time);
        vh.selectCb = (CheckBox) convertView.findViewById(R.id.cb_select);
        vh.cardLayout = (LinearLayout) convertView.findViewById(R.id.card_layout);
        vh.contentTip = (TextView) convertView.findViewById(R.id.msg_content_tips);
        vh.orderContent = (TextView) convertView.findViewById(R.id.msg_order_content);
        vh.hotelImage = (ImageView) convertView.findViewById(R.id.msg_hotel_image);
    }

    /**
     * 设置View显示的值与相应的Listener
     *
     * @param vh
     * @param position
     * @param isComMsg
     */
    private void setViewValues(final ViewHolder vh, final int position,
                               final boolean isComMsg) {
        final MessageVo item = messageList.get(position);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        vh.contentLayout.setLayoutParams(params);
        vh.contentLayout.setTag(item);
        vh.date.setText(TimeUtil.getChatTime(item.getSendTime()));
        long lastSendDate = position > 0 ? messageList.get(position - 1)
                .getSendTime() : 0; // 上一条消息的发送时间戳
        boolean isShowDate = (item.getSendTime() - lastSendDate) > 5 * 60 * 1000;
        vh.date.setVisibility(isShowDate ? View.VISIBLE : View.GONE);

        /** 根据userid获取个人头像链接 */
        String avatarUrl = null;
        if(isComMsg){
            avatarUrl = Constants.GET_USER_AVATAR + item.getContactId() + ".jpg";
        } else {
            String userID = CacheUtil.getInstance().getUserId();
            avatarUrl = Constants.GET_USER_AVATAR + userID + ".jpg";
        }

        ImageLoader.getInstance().displayImage(avatarUrl, vh.head, options);
        if (!isDelEnabled) {
            vh.head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ToDo Jimmy 跳转到个人详情页面
//                    Intent goSelfInfo = new Intent((Activity)context, MineActivity.class);
//                    context.startActivity(goSelfInfo);
                }
            });
        }
        final String attachId = item.getAttachId();
        MimeType mimeType = item.getMimeType();
        if (mimeType.equals(MimeType.TEXT)) {// 文本消息
            String recent = item.getContent();
            if (null != recent && !"".equals(recent)) {
                String key = item.getMessageId();
                CharSequence charSequence = (CharSequence) msgCacheMap
                        .get(key);
                if (charSequence == null) {
                    String content = item.getContent();
                    String keyWord = getKeyWord();
                    if (content.contains("[") && content.contains("]")) {
                        charSequence = EmotionUtil.getInstance()
                                .convertStringToSpannable(context,
                                        item.getContent(),
                                        EmotionType.MESSAGE_LIST);
                    } else {
                        charSequence = EmotionUtil.getInstance()
                                .getTextStringBuilder(content, keyWord);
                    }
                    msgCacheMap.put(key, charSequence);// 缓存起来
                }
                vh.msg.setTag(item);
                vh.msg.setText(charSequence, TextView.BufferType.SPANNABLE);
                vh.msg.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        showChildQuickActionBar(v, isComMsg, position);
                        return true;
                    }
                });
                // 最新添加
                vh.msg.setMovementMethod(LinkMovementMethod.getInstance());
                CharSequence text = vh.msg.getText();
                if (text instanceof Spannable) {
                    int end = text.length();
                    Spannable sp = (Spannable) vh.msg.getText();
                    URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
                    SpannableStringBuilder style = new SpannableStringBuilder(
                            text);
                    style.clearSpans();// should clear old spans
                    // 循环把链接发过去
                    if (null != urls && urls.length > 0) {
                        for (URLSpan spUrl : urls) {
                            MessageSpanURL myURLSpan = new MessageSpanURL(
                                    context, spUrl.getURL());
                            style.setSpan(myURLSpan, sp.getSpanStart(spUrl),
                                    sp.getSpanEnd(spUrl),
                                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        }
                        if (!(text.toString().contains("[") && text.toString()
                                .contains("]"))) {
                            vh.msg.setText(style);
                        }
                    }
                }
            }
            if (!isDelEnabled) {
                vh.contentLayout
                        .setOnLongClickListener(new View.OnLongClickListener() {

                            @Override
                            public boolean onLongClick(View v) {
                                showChildQuickActionBar(v, isComMsg, position);
                                return true;
                            }
                        });
            }
            vh.msg.setVisibility(View.VISIBLE);
            vh.img.setVisibility(View.GONE);
            vh.voice.setVisibility(View.GONE);
            vh.time.setVisibility(View.GONE);
            vh.cardLayout.setVisibility(View.GONE);
        } else if (mimeType.equals(MimeType.AUDIO)) {// 语音
            final int voiceTime = item.getVoiceTime();
            if(!isComMsg){
                if(item.getSendStatus().equals(SendStatus.SENDING)){
                    //TODO Jimmy 监听发送语音成功状态
                }
            }
            vh.contentLayout.setTag(R.id.content_layout, vh.voice);
            if (!isDelEnabled) {
                vh.contentLayout
                        .setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                showChildQuickActionBar(v, isComMsg, position);
                                return true;
                            }
                        });

                if (isComMsg) {// 别人
                    vh.contentLayout.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            MessageVo vo = (MessageVo) v.getTag();
                            //播放语音文件
                            String mediaPath = vo.getFilePath();
                            if(!TextUtils.isEmpty(mediaPath)){
                                MediaPlayerUtil.play(context, mediaPath);
                            }else {
                                DialogUtil.getInstance().showToast(context, "音频文件不存在");
                            }
                        }
                    });

                } else {// 自己
                    vh.contentLayout.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            MessageVo vo = (MessageVo) v.getTag();
                            String mediaPath = vo.getFilePath();
                            if(!TextUtils.isEmpty(mediaPath)){
                                MediaPlayerUtil.play(context, mediaPath);
                            }else{
                                DialogUtil.getInstance().showToast(context, "音频文件不存在");
                            }
                        }
                    });
                }
            }
            setTimeView(voiceTime, vh.time, vh.contentLayout);
            vh.msg.setVisibility(View.GONE);
            vh.img.setVisibility(View.GONE);
            vh.voice.setVisibility(View.VISIBLE);
            vh.time.setVisibility(View.VISIBLE);
            vh.cardLayout.setVisibility(View.GONE);
        } else if (mimeType.equals(MimeType.IMAGE)) {// 图片

            if (!isDelEnabled) {
                vh.contentLayout
                        .setOnLongClickListener(new View.OnLongClickListener() {

                            @Override
                            public boolean onLongClick(View v) {
                                showChildQuickActionBar(v, isComMsg, position);
                                return true;
                            }
                        });
                vh.contentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO Jimmy 跳转到图片浏览页面

                    }
                });
            }
            vh.msg.setText("");
            vh.msg.setVisibility(View.GONE);
            String key = item.getMessageId();
            Bitmap displayBitmap = null;
            Bitmap bitmapCache = (Bitmap) msgCacheMap.get(key);
            final String fileName = item.getFileName();
            String filePath = item.getFilePath();
            if (bitmapCache == null) {
                if(!TextUtils.isEmpty(filePath)){
                    displayBitmap = BitmapFactory.decodeFile(filePath);
                }
                if(null == displayBitmap){
                    displayBitmap = BitmapFactory.decodeFile(FileUtil.getInstance()
                            .getImagePath() + fileName);
                }
                if (displayBitmap != null) {// 本地取
                    displayBitmap = ImageUtil.cropThumbBitmap(displayBitmap);
                    displayBitmap = ImageUtil.loadThumbBitmap(context,
                            displayBitmap);
                    vh.img.setImageBitmap(displayBitmap);
                    msgCacheMap.put(key, displayBitmap); // 缓存起来
                } else {// 从网络取
                    //ToDo Jimmy 获取图片链接
                    String thumbUrl = Constants.GET_USER_AVATAR + item.getContactId() + ".jpg";
                    ImageLoader.getInstance().displayImage(thumbUrl, vh.img,
                            imageOptions, new ImageLoadingListener() {

                                @Override
                                public void onLoadingStarted(String imageUri,
                                                             View view) {
                                }

                                @Override
                                public void onLoadingFailed(String imageUri,
                                                            View view, FailReason failReason) {
                                }

                                @Override
                                public void onLoadingComplete(String imageUri,
                                                              View view, Bitmap loadedImage) {
                                    File file = new File(
                                            FileUtil.getInstance().getImagePath()
                                                    + fileName);
                                    ImageUtil.saveBitmap(loadedImage,
                                            file.getPath());
                                }

                                @Override
                                public void onLoadingCancelled(String imageUri,
                                                               View view) {
                                }
                            });
                    vh.img.setTag(attachId);
                }
            } else {
                vh.img.setImageBitmap(bitmapCache);
            }
            vh.msg.setVisibility(View.GONE);
            vh.img.setVisibility(View.VISIBLE);
            vh.voice.setVisibility(View.GONE);
            vh.time.setVisibility(View.GONE);
            vh.cardLayout.setVisibility(View.GONE);
        } else if (mimeType.equals(MimeType.CARD)) {// 卡片布局
            BookOrder bookOrder = new Gson().fromJson(item.getContent(), BookOrder.class);
            if (null != bookOrder) {
                String roomType = bookOrder.getRoomType();
                String arrivaDate = bookOrder.getArrivalDate();
                String imageUrl = bookOrder.getImage();
                int dayNum = bookOrder.getDayNum();
                vh.contentTip.setText(bookOrder.getContent());
                vh.orderContent.setText(roomType + " | " + arrivaDate + "入住 | " + dayNum + "晚");
                if (!TextUtils.isEmpty(imageUrl)) {
                    String logoUrl = ProtocolUtil.getGoodImgUrl(imageUrl);
                    ImageLoader.getInstance().displayImage(logoUrl, vh.hotelImage, cardOptions);
                }
            }
            vh.msg.setVisibility(View.GONE);
            vh.img.setVisibility(View.GONE);
            vh.voice.setVisibility(View.GONE);
            vh.time.setVisibility(View.GONE);
            vh.cardLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示操作条
     *
     * @param view
     *            显示在该view上
     * @param isComMsg
     *            是否对方的消息
     * @param position
     *            item位置
     */
    private void showChildQuickActionBar(final View view,
                                         final boolean isComMsg, final int position) {
        QuickAction quickAction = new QuickAction(context,
                QuickAction.HORIZONTAL);
        String attachId = messageList.get(position).getAttachId();
        if (TextUtils.isEmpty(attachId)) {
            quickAction.addActionItem(new ActionItem(0, "复制"));
            // quickAction.addActionItem(new ActionItem(1, "删除"));
            quickAction
                    .setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {

                        @Override
                        public void onItemClick(QuickAction source, int pos,
                                                int actionId) {
                            MessageVo messageChatVo = (MessageVo) view
                                    .getTag();
                            switch (actionId) {
                                case 0:// 复制
                                    ClipboardUtil.copy(
                                            messageChatVo.getContent(), context);
                                    break;
                                case 1:// 删除

                                    break;
                            }
                        }
                    });
            quickAction.show(view);
        }
    }

    static class ViewHolder {
        CircleImageView head;
        TextView        date;
        TextView        msg;
        ImageView       img;
        ImageView       voice;
        TextView        time;
        CheckBox        selectCb;
        LinearLayout    contentLayout;
        LinearLayout cardLayout;
        TextView contentTip, orderContent;
        ImageView hotelImage;
    }

    static class RecvViewHolder extends ViewHolder {
        TextView name;
    }

    static class SendViewHolder extends ViewHolder {
        ProgressBar progressBar;
        ImageView errIconIv;
        TextView name;
    }

    static class TipsViewHolder {
        TextView notifyTv;
    }

    /**
     * 根据语音时间长度，计算语音Layout的宽度
     *
     * @param time
     * @param timeTv
     * @param layout
     */
    public void setTimeView(int time, TextView timeTv, LinearLayout layout) {;
        int minWidth = 66; // 最小宽度
        int maxWidth = 186; // 最大宽度
        int secWidth = 2; // 每秒宽度
        int curWidth; // 当前宽度
        int scaleWidth; // 缩放宽度
        timeTv.setText(time + "\"");
        LinearLayout.LayoutParams params = null;
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        float density = dm.density; // 屏幕密度（像素比例）
        curWidth = minWidth + secWidth * time;
        if (curWidth < maxWidth)
            scaleWidth = (int) (curWidth * density);
        else
            scaleWidth = (int) (maxWidth * density);
        params = new LinearLayout.LayoutParams(scaleWidth,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);
    }

    /**
     * 显示消息重新发送对话框
     */
    private void showResendDialog(final MessageVo messageVo) {
        final Dialog dlg = new Dialog(context, R.style.MMTheme_DataSheet);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.resendrsheet_dialog, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);
        Button cancelBtn = (Button) layout.findViewById(R.id.dialog_btn_cancel);
        Button resendBtn = (Button) layout.findViewById(R.id.dialog_btn_resend);
        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                if (mResendListener != null) {
                    mResendListener.onResend(messageVo);
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        // set a large value put it in bottom
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout);
        dlg.show();
    }

    /**
     * 重新发送回调
     */
    public interface ResendListener {
        public void onResend(MessageVo messageChatVo);
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }
}

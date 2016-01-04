package com.itemstudio.luen;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InfoView extends LinearLayout {
    private String titleLabel = "";
    private String contentLabel = "";
    private TextView titleTextView;
    private TextView contentTextView;


    public InfoView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.info_view, this);
    }

    public InfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context, attrs);
    }

    public InfoView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
        initViews(context, attrs);
    }

    private void initViews(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.InfoView, 0, 0);

        try {
            // get the text and colors specified using the names in attrs.xml
            titleLabel = a.getString(R.styleable.InfoView_infoTitle);
            contentLabel = a.getString(R.styleable.InfoView_infoContent);

        } finally {
            a.recycle();
        }

        LayoutInflater.from(context).inflate(R.layout.info_view, this);

        //left text view
        titleTextView = (TextView) this.findViewById(R.id.info_title);
        titleTextView.setText(titleLabel);

        //right text view
        contentTextView = (TextView) this.findViewById(R.id.info_content);
        contentTextView.setText(contentLabel);

    }

    public String getTitle() {
        return titleLabel;
    }

    public void setTitle(String title) {
        this.titleLabel = title;
        if (titleTextView != null) {
            titleTextView.setText(titleLabel);
        }
    }

    public String getContent() {
        return contentLabel;
    }

    public void setContent(String content) {
        this.contentLabel = content;
        if (contentTextView != null) {
            contentTextView.setText(contentLabel);
        }
    }
}
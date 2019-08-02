package com.zxj.zlz.ui.blog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.zxj.zlz.R;

public class BlogPaper extends AppCompatActivity {

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_paper);

        Intent intent = getIntent();
        Blog blog = (Blog) intent.getSerializableExtra("blog");


        textView = findViewById(R.id.blog_content);

        String data = blog.title + "\n\n" + blog.content;

        textView.setText(data);

    }
}

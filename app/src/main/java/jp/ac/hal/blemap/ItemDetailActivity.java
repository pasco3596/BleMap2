package jp.ac.hal.blemap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class ItemDetailActivity extends AppCompatActivity {

    final String url = "http://pasuco234lab.xyz/preview";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");

        Global global = (Global) getApplication();
        List<Item> list = global.items;
        Item detail = null;
        for (Item item : list) {
            if (item.getName().equals(name)) {
                detail = item;
            }
        }

        if (detail != null) {

            TextView nameTv = (TextView) findViewById(R.id.name);
            TextView disTv = (TextView) findViewById(R.id.detail);
            nameTv.setText("ì•i–¼:" + name);
            disTv.setText(detail.getDiscription());

        }

        Button bt = (Button) findViewById(R.id.button);
        bt.setOnClickListener(v -> {
            setResult(RESULT_OK, intent);
            preview();
            finish();
        });

    }
    public void preview() {
        MyAsycLoader myAsycLoader = new MyAsycLoader(new MyAsycLoader.MyAsyncCallback() {
            @Override
            public void preExecute() {

            }

            @Override
            public void postExecute(Void result) {

            }

            @Override
            public void progressUpdate(int progress) {

            }

            @Override
            public void cancel() {

            }
        });
        myAsycLoader.execute(url);

    }
}

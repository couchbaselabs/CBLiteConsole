package com.couchbase.cblite.cbliteconsole;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

import com.couchbase.cblite.CBLMapEmitFunction;
import com.couchbase.cblite.CBLMapFunction;
import com.couchbase.cblite.CBLView;

import java.util.Map;

public class AllDocsActivity extends Activity {

    protected ListView allDocsListView;
    String dDocName = "designDoc";
    String dDocId = "_design/" + dDocName;
    String viewName = "customAllDocsView";
    String VIEWS_VERSION = "1.4";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alldocs);

        allDocsListView = (ListView)findViewById(R.id.listViewAllDocs);

        CBLView view = CBLiteConsoleActivity.database.getView(String.format("%s/%s", dDocName, viewName));

        if (view.getMap() == null) {
            view.setMap(
                    new CBLMapFunction() {
                        @Override
                        public void map(Map<String, Object> stringObjectMap, CBLMapEmitFunction cblViewMapEmitBlock) {
                            Log.d(CBLiteConsoleActivity.TAG, "view map() called with: " + stringObjectMap);
                            cblViewMapEmitBlock.emit(stringObjectMap.get("_id"), null);
                        }
                    },
                    VIEWS_VERSION
            );
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.all_docs, menu);
        return true;
    }
    
}

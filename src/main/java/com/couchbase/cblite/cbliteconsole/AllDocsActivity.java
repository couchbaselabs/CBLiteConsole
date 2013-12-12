package com.couchbase.cblite.cbliteconsole;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.View;

import java.util.Map;

public class AllDocsActivity extends Activity {

    protected ListView allDocsListView;
    String dDocName = "designDoc";
    String dDocId = "_design/" + dDocName;
    String viewName = "customAllDocsView";
    String VIEWS_VERSION = "1.4";
    private Database database = CBLiteConsoleActivity.database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alldocs);

        allDocsListView = (ListView)findViewById(R.id.listViewAllDocs);

        View view = database.getView(String.format("%s/%s", dDocName, viewName));

        if (view.getMap() == null) {
            view.setMap(
                    new Mapper() {
                        @Override
                        public void map(Map<String, Object> stringObjectMap, Emitter cblViewMapEmitBlock) {
                            Log.d(CBLiteConsoleActivity.TAG, "view map() called with: " + stringObjectMap);
                            cblViewMapEmitBlock.emit(stringObjectMap.get("_id"), null);
                        }
                    },
                    VIEWS_VERSION
            );
        }

        try {
            Query query = view.createQuery();
            QueryEnumerator queryEnumerator = query.run();
            while (queryEnumerator.hasNext()) {
                QueryRow row = queryEnumerator.next();
                Document document = database.getDocument(row.getDocumentId());
                String message = String.format(
                        "row doc id: %s rev id: %s",
                        row.getDocumentId(),
                        document.getCurrentRevisionId()
                );
                Log.i(CBLiteConsoleActivity.TAG, message);
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error querying rows", Toast.LENGTH_LONG).show();
            Log.e(CBLiteConsoleActivity.TAG, e.getLocalizedMessage(), e);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.all_docs, menu);
        return true;
    }

}

package com.couchbase.cblite.cbliteconsole;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.couchbase.cblite.CBLView;
import com.couchbase.cblite.CBLViewMapBlock;
import com.couchbase.cblite.CBLViewMapEmitBlock;

import org.codehaus.jackson.JsonNode;
import org.ektorp.ViewQuery;

import java.util.Map;

public class AllDocsActivity extends Activity {

    protected ListView allDocsListView;
    protected AllDocsListAdapter allDocsListViewAdapter;
    String dDocName = "designDoc";
    String dDocId = "_design/" + dDocName;
    String viewName = "customAllDocsView";
    String VIEWS_VERSION = "1.4";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alldocs);

        allDocsListView = (ListView)findViewById(R.id.listViewAllDocs);

        CBLView view = CBLiteConsoleActivity.database.getViewNamed(String.format("%s/%s", dDocName, viewName));

        if (view.getMapBlock() == null) {
            view.setMapReduceBlocks(
                    new CBLViewMapBlock() {
                        @Override
                        public void map(Map<String, Object> stringObjectMap, CBLViewMapEmitBlock cblViewMapEmitBlock) {
                            Log.d(CBLiteConsoleActivity.TAG, "view map() called with: " + stringObjectMap);
                            cblViewMapEmitBlock.emit(stringObjectMap.get("_id"), null);
                        }
                    },
                    null,
                    VIEWS_VERSION
            );
        }

        ViewQuery viewQuery = new ViewQuery()
                .designDocId(dDocId)
                .viewName(viewName)
                .includeDocs(true);

        boolean followChanges = false;
        allDocsListViewAdapter = new AllDocsListAdapter(CBLiteConsoleActivity.couchDbConnector, viewQuery, followChanges, getApplicationContext());
        allDocsListView.setAdapter(allDocsListViewAdapter);


        // listening to single list item on click
        allDocsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                JsonNode docAsNode = allDocsListViewAdapter.getRow(position).getDocAsNode();
                String docId = docAsNode.get("_id").asText();
                Log.d(CBLiteConsoleActivity.TAG, "docId: " + docId);
                Toast toast = Toast.makeText(getApplicationContext(), docAsNode.toString(), Toast.LENGTH_LONG);
                toast.show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.all_docs, menu);
        return true;
    }
    
}

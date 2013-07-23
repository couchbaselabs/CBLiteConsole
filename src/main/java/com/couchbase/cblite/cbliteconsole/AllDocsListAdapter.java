package com.couchbase.cblite.cbliteconsole;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.codehaus.jackson.JsonNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.android.util.CouchbaseViewListAdapter;

public class AllDocsListAdapter extends CouchbaseViewListAdapter {

    Context context;

    public AllDocsListAdapter(CouchDbConnector couchDbConnector, ViewQuery viewQuery, boolean followChanges, Context context) {
        super(couchDbConnector, viewQuery, followChanges);
        this.context = context;
    }

    @Override
    public View getView(int position, View itemView, ViewGroup parent) {

        TextView textView = new TextView(context);
        JsonNode docAsNode = getRow(position).getDocAsNode();
        String docId = docAsNode.get("_id").asText();
        textView.setText(docId);
        return textView;

    }
}

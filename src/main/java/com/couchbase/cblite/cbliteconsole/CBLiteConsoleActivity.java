package com.couchbase.cblite.cbliteconsole;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.couchbase.cblite.CBLDatabase;
import com.couchbase.cblite.CBLQueryOptions;
import com.couchbase.cblite.CBLRevision;
import com.couchbase.cblite.CBLServer;
import com.couchbase.cblite.ektorp.CBLiteHttpClient;
import com.couchbase.cblite.router.CBLURLStreamHandlerFactory;

import org.codehaus.jackson.JsonNode;
import org.ektorp.AttachmentInputStream;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.android.util.EktorpAsyncTask;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbInstance;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

public class CBLiteConsoleActivity extends Activity {

    private static boolean initializedUrlHandler = false;

    public static String TAG = "CBLiteConsole";
    public static String INTENT_PARAMETER_DATABASE_NAME = "INTENT_PARAMETER_DATABASE_NAME";

    protected static HttpClient httpClient;
    protected CBLServer server = null;
    protected static CBLDatabase database = null;
    protected CouchDbInstance dbInstance;
    protected static CouchDbConnector couchDbConnector;
    protected String databaseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cblite_console);

        Bundle b = getIntent().getExtras();
        databaseName = b.getString(INTENT_PARAMETER_DATABASE_NAME);

        //for some reason a traditional static initializer causes junit to die
        if(!initializedUrlHandler) {
            CBLURLStreamHandlerFactory.registerSelfIgnoreError();
            initializedUrlHandler = true;
        }

        startCBLite();
        startDatabase();
        startEktorp();

        initializeButtonActions();

    }

    private void initializeButtonActions() {
        final Button button = (Button) findViewById(R.id.button_alldocs);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (database.getDocumentCount() > 0) {
                    startActivity(new Intent(CBLiteConsoleActivity.this, AllDocsActivity.class));
                }
                else {
                    String message = "The db is empty!";
                    Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });

        final Button buttonAddXDocs = (Button) findViewById(R.id.buttonAddXDocs);
        buttonAddXDocs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final EditText input = new EditText(CBLiteConsoleActivity.this);
                new AlertDialog.Builder(CBLiteConsoleActivity.this)
                        .setTitle("How many docs?")
                        .setMessage("Enter the number of test docs you want to add")
                        .setView(input)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Editable value = input.getText();
                                String stringValue = value.toString();
                                int intValue = Integer.parseInt(stringValue);
                                createXTestDocs(intValue);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
            }
        });

        final Button buttonAddXDocsPlusAttachments = (Button) findViewById(R.id.buttonAddXDocsPlusAttachments);
        buttonAddXDocsPlusAttachments.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final EditText input = new EditText(CBLiteConsoleActivity.this);
                new AlertDialog.Builder(CBLiteConsoleActivity.this)
                        .setTitle("How many docs with attachments?")
                        .setMessage("Enter the number of test docs you want to add")
                        .setView(input)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Editable value = input.getText();
                                String stringValue = value.toString();
                                int intValue = Integer.parseInt(stringValue);
                                createXTestDocsWithAttachments(intValue);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
            }
        });

        final Button buttonDBStats = (Button) findViewById(R.id.button_dbstats);
        buttonDBStats.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String message = String.format("Number of docs in db: %s", database.getDocumentCount());
                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                toast.show();
            }
        });


        final Button buttonDeleteDocs = (Button) findViewById(R.id.buttonDeleteDocs);
        buttonDeleteDocs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final EditText input = new EditText(CBLiteConsoleActivity.this);
                new AlertDialog.Builder(CBLiteConsoleActivity.this)
                        .setTitle("Are you sure you want to delete ALL docs?")
                        .setMessage("Type 'I am sure'")
                        .setView(input)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Editable value = input.getText();
                                String stringValue = value.toString();
                                if (stringValue.equalsIgnoreCase("I am sure")) {
                                    deleteAllDocs();
                                }
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
            }
        });

    }

    private void deleteAllDocs() {
        EktorpAsyncTask asyncTask = new EktorpAsyncTask() {
            @Override
            protected void doInBackground() {
                final int docCountBefore = database.getDocumentCount();

                ViewQuery query = new ViewQuery().allDocs();
                ViewResult viewResult = couchDbConnector.queryView(query);
                for (ViewResult.Row row : viewResult) {
                    String docId = row.getId();
                    Log.d(TAG, "docId to delete: " + docId);
                    //JsonNode docNode = row.getDocAsNode();
                    //String revId = docNode.get("_rev").asText();
                    TestObject testObject = couchDbConnector.get(TestObject.class, docId);
                    couchDbConnector.delete(testObject);
                }

                CBLiteConsoleActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        String message = String.format("Docs deleted.  Doc count before: %d, after: %d", docCountBefore, database.getDocumentCount());
                        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            }
        };
        asyncTask.execute();
    }

    private void createXTestDocs(final int numberOfDocs) {

        EktorpAsyncTask asyncTask = new EktorpAsyncTask() {
            @Override
            protected void doInBackground() {
                final int docCountBefore = database.getDocumentCount();
                for (int i=0; i<numberOfDocs; i++) {
                    TestObject test = new TestObject(1, false, "console doc");
                    couchDbConnector.create(test);
                }
                CBLiteConsoleActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        String message = String.format("Docs added.  Doc count before: %d, after: %d", docCountBefore, database.getDocumentCount());
                        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            }
        };
        asyncTask.execute();

    }

    private void createXTestDocsWithAttachments(final int numberOfDocs) {

        EktorpAsyncTask asyncTask = new EktorpAsyncTask() {
            @Override
            protected void doInBackground() {
                final int docCountBefore = database.getDocumentCount();
                for (int i=0; i<numberOfDocs; i++) {
                    TestObject test = new TestObject(1, false, "console doc");

                    couchDbConnector.create(test);

                    //attach file to it
                    byte[] attach1 = "This is the body of attach1".getBytes();
                    ByteArrayInputStream b = new ByteArrayInputStream(attach1);
                    AttachmentInputStream a = new AttachmentInputStream("attach", b, "text/plain");
                    couchDbConnector.createAttachment(test.getId(), test.getRevision(), a);
                }
                CBLiteConsoleActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        String message = String.format("Docs w/ attachments added.  Doc count before: %d, after: %d", docCountBefore, database.getDocumentCount());
                        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            }
        };
        asyncTask.execute();

    }




    protected String getServerPath() {
        String filesDir = getFilesDir().getAbsolutePath();
        return filesDir;
    }

    protected void startCBLite() {
        try {
            server = new CBLServer(getServerPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void startDatabase() {
        database = server.getDatabaseNamed(databaseName, true);
        database.open();
    }

    protected void startEktorp() {

        if(httpClient != null) {
            httpClient.shutdown();
        }

        httpClient = new CBLiteHttpClient(server);
        dbInstance = new StdCouchDbInstance(httpClient);

        EktorpAsyncTask startupTask = new EktorpAsyncTask() {

            @Override
            protected void doInBackground() {
                couchDbConnector = dbInstance.createConnector(databaseName, true);
            }

            @Override
            protected void onSuccess() {
                Log.d(TAG, "Ektorp started OK");
            }

        };
        startupTask.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cblite_console, menu);
        return true;
    }

    
}

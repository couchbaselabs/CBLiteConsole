package com.couchbase.cblite.cbliteconsole;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.couchbase.cblite.CBLDatabase;
import com.couchbase.cblite.CBLManager;
import com.couchbase.cblite.router.CBLURLStreamHandlerFactory;

public class CBLiteConsoleActivity extends Activity {

    private static boolean initializedUrlHandler = false;

    public static String TAG = "CBLiteConsole";
    public static String INTENT_PARAMETER_DATABASE_NAME = "INTENT_PARAMETER_DATABASE_NAME";

    protected CBLManager manager = null;
    protected static CBLDatabase database = null;
    protected String databaseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cblite_console);

        Bundle b = getIntent().getExtras();
        databaseName = b.getString(INTENT_PARAMETER_DATABASE_NAME);

        startCBLite();
        startDatabase();

        initializeButtonActions();

    }

    private void initializeButtonActions() {

        final Button buttonAllDocs = (Button) findViewById(R.id.button_alldocs);
        buttonAllDocs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (database.getDocumentCount() > 0) {
                    startActivity(new Intent(CBLiteConsoleActivity.this, AllDocsActivity.class));
                } else {
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
                /*CBLBlobStore blobStore = database.getAttachments();
                Set<CBLBlobKey> blobKeys = blobStore.allKeys();
                StringBuffer messageBuffer = new StringBuffer();
                for (CBLBlobKey blobKey : blobKeys) {
                    messageBuffer.append("path: ");
                    messageBuffer.append(blobStore.pathForKey(blobKey));
                    messageBuffer.append("size: ");
                    messageBuffer.append(blobStore.getSizeOfBlob(blobKey));
                    messageBuffer.append("stream: ");
                    messageBuffer.append(blobStore.blobStreamForKey(blobKey).toString());

                }
                Toast toast = Toast.makeText(getApplicationContext(), messageBuffer.toString(), Toast.LENGTH_LONG);
                toast.show();*/
            }
        });


        final Button buttonTouchAllDocs = (Button) findViewById(R.id.buttonTouchAllDocs);
        buttonTouchAllDocs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                touchAllDocs();
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

    }

    private void touchAllDocs() {

    }


    private void createXTestDocs(final int numberOfDocs) {



    }

    private void createXTestDocsWithAttachments(final int numberOfDocs) {



    }




    protected String getServerPath() {
        String filesDir = getFilesDir().getAbsolutePath();
        return filesDir;
    }

    protected void startCBLite() {
        manager = new CBLManager(getFilesDir());
    }

    protected void startDatabase() {
        database = manager.getDatabase(databaseName);
        database.open();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cblite_console, menu);
        return true;
    }

    
}

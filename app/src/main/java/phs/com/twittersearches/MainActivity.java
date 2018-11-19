package phs.com.twittersearches;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends ListActivity {

    private static final String SEARCHES = "searches";
    private EditText queryEditText;
    private EditText tagEditText;
    private SharedPreferences savedSearches;
    private ArrayList<String> tags;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queryEditText = findViewById(R.id.queryEditText);
        tagEditText = findViewById(R.id.tagEditText);

        savedSearches = getSharedPreferences(SEARCHES, MODE_PRIVATE);

        tags = new ArrayList<String>(savedSearches.getAll().keySet());
        Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);

        adapter = new ArrayAdapter<String>(this, R.layout.lista, tags);
        setListAdapter(adapter);

        ImageButton saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(saveButtonListener);

        getListView().setOnItemClickListener(itemClickListener);

        getListView().setOnItemClickListener(itemClickListener);
    }
    public View.OnClickListener saveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (queryEditText.getText().length() > 0 &&
               tagEditText.getText().length() > 0) {
                addTaggedSearches(queryEditText.getText().toString(), tagEditText.getText().toString());
                queryEditText.setText("");
                tagEditText.setText("");

                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(tagEditText.getWindowToken(), 0);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setMessage(R.string.missingMessage);

                builder.setPositiveButton(R.string.OK, null);

                AlertDialog errorDialog = builder.create();
                errorDialog.show();
            }
        }
    };
    private void addTaggedSearches(String query, String tag) {

        SharedPreferences.Editor preferencesEditor = savedSearches.edit();
        preferencesEditor.putString(tag, query);
        preferencesEditor.apply();

        if(!tags.contains(tag)) {
            tags.add(tag);
            Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);
            adapter.notifyDataSetChanged();
        }
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            String tag = ((TextView) view).getText().toString();
            String urlString = getString(R.string.searchUrl) +
                    Uri.encode(savedSearches.getString(tag, ""), "UTF-8");
            Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
            startActivity(web);
        }
    };
}

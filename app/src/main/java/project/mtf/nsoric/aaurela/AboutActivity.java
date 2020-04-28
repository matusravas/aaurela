package project.mtf.nsoric.aaurela;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import helpers.Constants;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {
    TextView textViewContact, textViewVersion, textViewSignedUser;
    Intent intent;
    Toolbar toolbar;
    SharedPreferences sharedPreferences;
    private final static String MAIL_TO = "mailto: ";
    int sensorID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_AAURELA, MODE_PRIVATE);
        intent = getIntent();
        sensorID = intent.getIntExtra(Constants.SENSOR_ID, 0);
        findViews();
    }

    @Override
    protected void onResume() {
        setDataToViews();
        super.onResume();
    }

    private void setDataToViews() {
        textViewVersion.setText(getString(R.string.app_version, BuildConfig.VERSION_NAME));
        textViewSignedUser.setText(getString(R.string.about_info_signed_as, sharedPreferences.getString(Constants.SP_FULL_NAME, "")));

    }

    private void findViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textViewSignedUser = findViewById(R.id.text_view_signed_user);
        textViewVersion = findViewById(R.id.text_view_version);
        textViewContact = findViewById(R.id.text_view_contact);
        textViewContact.setOnClickListener(this);
    }

    private void openMailActivity() {
        Intent mailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(MAIL_TO +
                getResources().getString(R.string.mail)));
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject));
        mailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_body));
        startActivity(Intent.createChooser(mailIntent, getString(R.string.chooser_title)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_view_contact:
                openMailActivity();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }
}

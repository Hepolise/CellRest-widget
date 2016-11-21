package am.hepolise.cellrest;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView version = (TextView) findViewById(R.id.about_version);
        try {
            version.setText(getPackageManager().getPackageInfo(getPackageName(), 0 ).versionName);
        } catch (PackageManager.NameNotFoundException e) {

        }
        TextView contact = (TextView) findViewById(R.id.about_contact);
        contact.setText(Html.fromHtml(String.format(getString(R.string.about_contact),
                "https://github.com/Hepolise/TrafficWidget/issues",
                "4pdalink")));
        contact.setMovementMethod(LinkMovementMethod.getInstance());
        TextView write = (TextView) findViewById(R.id.about_write);
        write.setText(Html.fromHtml(String.format(getString(R.string.about_write),
                "mailto:admin@srvr.tk")));
        write.setMovementMethod(LinkMovementMethod.getInstance());
    }
}

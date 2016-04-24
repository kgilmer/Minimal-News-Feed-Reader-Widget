package com.abk.mrw.settings;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.abk.mrw.R;

/**
 * Created by kgilmer on 4/16/16.
 */
public class FeedlySearchDialogPreference extends DialogPreference {

    private Context context;

    public FeedlySearchDialogPreference(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setPersistent(false);
        setDialogLayoutResource(R.layout.feedly_dialog_layout);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        ImageButton searchButton = (ImageButton) view.findViewById(R.id.feedlySearchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "boo", Toast.LENGTH_LONG).show();
            }
        });
    }
}

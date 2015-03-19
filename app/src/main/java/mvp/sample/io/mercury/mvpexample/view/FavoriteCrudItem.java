package mvp.sample.io.mercury.mvpexample.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mvp.sample.io.mercury.mvpexample.R;
import mvp.sample.io.mercury.mvpexample.entity.Favorite;

public class FavoriteCrudItem extends RelativeLayout {
    private TextView title;
    private OnRemoveClickListener removeListener;
    private Favorite favorite;

    public FavoriteCrudItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        title = (TextView) findViewById(R.id.title);
        View removeBtn = findViewById(R.id.remove_btn);

        removeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (removeListener != null) {
                    removeListener.onRemoveClicked(favorite);
                }
            }
        });
    }

    public void setOnRemoveClickListener(OnRemoveClickListener listener) {
        removeListener = listener;
    }

    public void bind(Favorite item) {
        favorite = item;
        title.setText(Integer.toString(item.getId()));
    }

    public interface OnRemoveClickListener {
        public void onRemoveClicked(Favorite favorite);
    }
}

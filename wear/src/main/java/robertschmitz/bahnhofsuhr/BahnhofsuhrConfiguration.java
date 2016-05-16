package robertschmitz.bahnhofsuhr;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BahnhofsuhrConfiguration extends Activity {

    private class ViewHolder extends WearableListView.ViewHolder {
        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
        }
    }

    public static enum TickConfig {
        LINEAR(R.string.config_linear),
        SMOOTH(R.string.config_smooth),
        HARD(R.string.config_hard);

        private final int mStringId;

        TickConfig(@StringRes int stringId) {
            mStringId = stringId;
        }
    }

    private WearableListView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bahnhofsuhr_configuration);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mList = (WearableListView) stub.findViewById(R.id.list);
                mList.setAdapter(new WearableListView.Adapter()  {
                    @Override
                    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tick_configuration_item, parent, false);
                        return new ViewHolder(view);
                    }

                    @Override
                    public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
                        ((ViewHolder) holder).textView.setText(TickConfig.values()[position].mStringId);
                        ((ViewHolder) holder).itemView.setTag(TickConfig.values()[position]);
                    }

                    @Override
                    public int getItemCount() {
                        return TickConfig.values().length;
                    }

                });
                mList.setClickListener(new WearableListView.ClickListener() {
                    @Override
                    public void onClick(WearableListView.ViewHolder viewHolder) {
                        TickConfig config = (TickConfig) viewHolder.itemView.getTag();
                        SharedPreferences prefs = getSharedPreferences(getApplication().getPackageName(), MODE_PRIVATE);
                        prefs.edit()
                                .putInt("tick", config.ordinal())
                                .commit();
                        finish();
                    }

                    @Override
                    public void onTopEmptyRegionClick() {

                    }
                });

                SharedPreferences prefs = getSharedPreferences(getApplication().getPackageName(), MODE_PRIVATE);
                mList.scrollToPosition(prefs.getInt("tick", 0));
            }
        });
    }
}

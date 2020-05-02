package com.felinkotech.flxsqlitemanager.customviews;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.felinkotech.flxsqlitemanager.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Felix
 * 27/04/2020
 */
public class HistoryViewScrollable extends RelativeLayout {
    private Context context ;
    private List<List<String>> data ;
    private HistoryViewScrollableAdapter adapter ;
    private RecyclerView recyclerView ;
    private static final int MAXIMUM_COLUMN_TEXT_LENGTH = 100 ;
    private NScrollView nestedScrollView ;
    private HorizontalScrollView horizontalScrollView,hView ;
    private Integer[] widths ;
    private onActionButtonClickedListener onActionButtonClickedListener ;

    public HistoryViewScrollable(Context context) {
        super(context);
        this.context = context ;
    }

    public HistoryViewScrollable(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context ;
    }

    public HistoryViewScrollable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context ;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HistoryViewScrollable(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context ;
    }

    private void setActionLayout(){
        nestedScrollView = new NScrollView(context) ;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView textView = new TextView(context);
        textView.setText("ACN");
        textView.setPadding(0,8,0,8) ;
        textView.setGravity(Gravity.CENTER_HORIZONTAL) ;
        int textColor = context.getResources().getColor(R.color.black);
        int backgroundColor = context.getResources().getColor(R.color.white) ;

        textView.setTextColor(textColor) ;
        textView.setBackgroundColor(backgroundColor) ;
        linearLayout.addView(textView);

        LinearLayout lin = new LinearLayout(context) ;
        lin.setOrientation(LinearLayout.VERTICAL);

        for(int i=1;i<getData().size();i++){
            LinearLayout l = new LinearLayout(context) ;
            l.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(30,context))) ;
            l.setClickable(true);
            l.setFocusable(true);
            l.setOrientation(LinearLayout.VERTICAL) ;
            l.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.ripple_background));
            l.setGravity(Gravity.CENTER_HORIZONTAL);
            ImageView imageView = new ImageView(context) ;
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) ;
            imageView.setLayoutParams(p);
            imageView.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
            l.addView(imageView);
            if(getOnActionButtonClickedListener()!=null) {
                final int index = i ;
                l.setOnClickListener(v -> {
                    getOnActionButtonClickedListener().onActionButtonClicked(getData().get(index));
                });
            }
            lin.addView(l) ;
        }

        lin.setLayoutParams(new NestedScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        nestedScrollView.addView(lin) ;

        nestedScrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)) ;
        linearLayout.addView(nestedScrollView) ;

        LayoutParams params = new LayoutParams(dpToPx(50,context), ViewGroup.LayoutParams.MATCH_PARENT) ;
        params.addRule(RelativeLayout.ALIGN_PARENT_END);
        linearLayout.setLayoutParams(params);

        linearLayout.setBackgroundColor(context.getResources().getColor(R.color.white));

        addView(linearLayout);

        setScrolling() ;
    }

    private void setScrolling(){
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                nestedScrollView.scrollBy(dx,dy);
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           /* nestedScrollView.setOnScrollChangeListener(new OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    recyclerView.scrollBy(i,i1);
                }
            });*/
            horizontalScrollView.setOnScrollChangeListener(new OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    hView.scrollTo(i,i1); ;
                }
            });
            hView.setOnScrollChangeListener(new OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    horizontalScrollView.scrollTo(i,i1); ;
                }
            });
        }
    }

    private void setHeader(){
        if(getData()!=null && getData().size()>0){
            hView = new HorizontalScrollView(context) ;

            List<String> headers = getData().get(0) ;
            LinearLayout linearLayout = new LinearLayout(context) ;
            linearLayout.setOrientation(LinearLayout.HORIZONTAL) ;

            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(30,context)) ;
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            hView.setLayoutParams(params);


            for(String string:headers){
                TextView textView = new TextView(context);
                textView.setTextColor(context.getResources().getColor(R.color.black));
                textView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.ripple_background));

                textView.setClickable(true);
                textView.setFocusable(true);
                textView.setMinLines(1);
                LinearLayout.LayoutParams P = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                textView.setLayoutParams(P) ;
                linearLayout.addView(textView) ;
            }
            linearLayout.setLayoutParams(new HorizontalScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            hView.addView(linearLayout);
            addView(hView) ;
        }
    }

    public void initializeComponent(){
        if(getData()==null){
            return ;
        }
        if(getData().size()<1) return ;

        horizontalScrollView = new HorizontalScrollView(context) ;
        horizontalScrollView.setBackgroundColor(context.getResources().getColor(R.color.white));
        setHeader();
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) ;
        params.topMargin = dpToPx(30,context) ;
        horizontalScrollView.setLayoutParams(params) ;
        recyclerView = new RecyclerView(context) ;
        recyclerView.setLayoutParams(new HorizontalScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL,false)) ;
        recyclerView.hasFixedSize() ;

        adapter = new HistoryViewScrollableAdapter(prepareData()) ;
        recyclerView.setAdapter(adapter) ;
        horizontalScrollView.addView(recyclerView);

        addView(horizontalScrollView);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                resizeToNormal() ;
            }
        },1000) ;
    }

    private List<List<String>> prepareData(){
        if(getData()!=null && getData().size()>0){
            List<List<String>> data = new ArrayList<>() ;
            for(int i=1;i<getData().size();i++){
                for(String string:getData().get(i)){
                    if(string!=null && string.length()>MAXIMUM_COLUMN_TEXT_LENGTH){
                        string = string.substring(0,MAXIMUM_COLUMN_TEXT_LENGTH)+"..." ;
                    }else{
                        if(string==null) string = "null" ;
                    }
                }
                data.add(getData().get(i)) ;
            }

            return data ;
        }else return null ;
    }

    private void resizeToNormal(){
        if(getData()!=null && getData().size()>0) {
            widths = new Integer[getData().get(0).size()] ;
            if(hView!=null && hView.getChildCount()>0){
                LinearLayout l = (LinearLayout) hView.getChildAt(0);
                for(int i=0;i<l.getChildCount();i++){
                    widths[i] = l.getChildAt(i).getMeasuredWidth() ;
                }
            }
            for (int i = 0; i < adapter.getItemCount(); i++) {
                try {
                    final LinearLayout linearLayout = (LinearLayout) Objects.requireNonNull(recyclerView.getLayoutManager()).findViewByPosition(i);
                    if(linearLayout!=null) {
                        for (int x = 0; x < linearLayout.getChildCount(); x++) {
                            TextView textView = (TextView) linearLayout.getChildAt(x);
                            if (widths[x] != null) {
                                if (textView.getMeasuredWidth() > widths[x]) {
                                    widths[x] = textView.getMeasuredWidth();
                                }
                            } else widths[x] = textView.getMeasuredWidth();
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < adapter.getItemCount(); i++) {
                try {
                    final LinearLayout linearLayout = (LinearLayout) Objects.requireNonNull(recyclerView.getLayoutManager()).findViewByPosition(i);
                    if (linearLayout != null) {
                        for (int x = 0; x < linearLayout.getChildCount() && widths.length==linearLayout.getChildCount(); x++) {
                            TextView textView = (TextView) linearLayout.getChildAt(x);
                            ViewGroup.LayoutParams params = textView.getLayoutParams() ;
                            if(widths[x]!=null) {
                                params.width = widths[x];
                                textView.setLayoutParams(params);
                            }
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            if(hView!=null && hView.getChildCount()>0){
                LinearLayout l = (LinearLayout) hView.getChildAt(0);
                for(int x=0;x<l.getChildCount();x++){
                    TextView textView = (TextView) l.getChildAt(x);
                    ViewGroup.LayoutParams params = textView.getLayoutParams() ;
                    if(widths[x]!=null) {
                        params.width = widths[x];
                        textView.setLayoutParams(params);
                    }
                }
            }
        }
    }

    public List<List<String>> getData() {
        return data;
    }

    public HistoryViewScrollable setData(List<List<String>> data) {
        this.data = data;
        return this ;
    }

    public HistoryViewScrollable.onActionButtonClickedListener getOnActionButtonClickedListener() {
        return onActionButtonClickedListener;
    }

    public HistoryViewScrollable setOnActionButtonClickedListener(HistoryViewScrollable.onActionButtonClickedListener onActionButtonClickedListener) {
        this.onActionButtonClickedListener = onActionButtonClickedListener;
        return this ;
    }

    private class HistoryViewScrollableAdapter extends RecyclerView.Adapter<HistoryViewScrollableAdapter.HistoryViewScollableViewHolder>{
        private List<List<String>> dataValues ;
        HistoryViewScrollableAdapter(List<List<String>> dataValues){
            this.dataValues = dataValues ;
        }
        @NonNull
        @Override
        public HistoryViewScollableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LinearLayout linearLayout = new LinearLayout(context) ;
            linearLayout.setOrientation(LinearLayout.HORIZONTAL) ;
            linearLayout.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(30,context)));

            return new HistoryViewScollableViewHolder(linearLayout) ;
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryViewScollableViewHolder holder, int position) {
            List<String> strings = dataValues.get(holder.getAdapterPosition()) ;
            for(int i=0;i<strings.size();i++) {
                TextView textView = new TextView(context);
                textView.setTextColor(context.getResources().getColor(R.color.black));
                textView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.ripple_background));
                textView.setText(strings.get(i) != null ? strings.get(i) : "null");
                if(getOnActionButtonClickedListener()!=null){
                    final int index = i ;
                    textView.setOnClickListener(v->{
                        getOnActionButtonClickedListener().onTextClicked(strings,strings.get(index));
                    });
                }
                int paddingTopBottom = 5 ;
                textView.setPadding(10, paddingTopBottom, 10, paddingTopBottom);
                textView.setClickable(true);
                textView.setFocusable(true);
                textView.setMinLines(1);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                if (widths!=null && widths[i] != null){
                    params.width = widths[i] ;
                }
                textView.setLayoutParams(params) ;
                holder.layout.addView(textView) ;
            }
        }

        @Override
        public int getItemCount() {
            return this.dataValues.size();
        }

        @Override
        public long getItemId(int position) {
            return position ;
        }

        @Override
        public int getItemViewType(int position) {
            return position ;
        }

        private class HistoryViewScollableViewHolder extends RecyclerView.ViewHolder{
            LinearLayout layout ;
            HistoryViewScollableViewHolder(@NonNull View itemView) {
                super(itemView);
                layout = (LinearLayout)itemView ;
            }
        }
    }

    public static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    public static int dpToPx(int dp,Context context) {
        float density = context.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    public interface onActionButtonClickedListener{
        void onActionButtonClicked(List<String> allRowData);
        default void onTextClicked(List<String> allRowData, String clickedValue){};
    }

    private class NScrollView extends NestedScrollView {

        public NScrollView(@NonNull Context context) {
            super(context);
        }

        public NScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public NScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            return false ;//scrollable && super.onTouchEvent(ev);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return false ;//scrollable && super.onInterceptTouchEvent(ev);
        }
    }
}

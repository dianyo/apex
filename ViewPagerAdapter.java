package dianyo.apex;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {
    private List<View> myListViews;

    public ViewPagerAdapter(List<View> myListViews){
        this.myListViews = myListViews;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object obj){
        container.removeView((View) obj);
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position){
        View view = myListViews.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public int getCount(){
        return myListViews.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1){
        return arg0 == arg1;
    }
}
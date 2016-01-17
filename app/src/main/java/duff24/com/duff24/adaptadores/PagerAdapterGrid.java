package duff24.com.duff24.adaptadores;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import duff24.com.duff24.fragments.ProductoGridFragment;

/**
 * Created by geovanny on 10/01/16.
 */
public class PagerAdapterGrid extends FragmentStatePagerAdapter
{

    private  List<ProductoGridFragment> data;

    public PagerAdapterGrid(FragmentManager fm,List<ProductoGridFragment> data)
    {
        super(fm);
        this.data=data;
    }
    @Override
    public Fragment getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        super.restoreState(state, loader);
    }
}

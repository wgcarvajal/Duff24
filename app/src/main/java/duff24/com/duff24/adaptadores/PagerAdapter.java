package duff24.com.duff24.adaptadores;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.List;

import duff24.com.duff24.fragments.FragmentGeneric;
import duff24.com.duff24.fragments.ProductoFragment;

/**
 * Created by geovanny on 9/01/16.
 */
public class PagerAdapter extends FragmentStatePagerAdapter
{
    private  List<FragmentGeneric> data;

    public PagerAdapter(FragmentManager fm,List<FragmentGeneric> data)
    {
        super(fm);
        this.data=data;
    }

    @Override
    public Fragment getItem(int position)
    {
        return data.get(position);
    }

    @Override
    public int getCount()
    {
        return data.size();
    }





}

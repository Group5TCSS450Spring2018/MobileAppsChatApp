package spr018.tcss450.clientapplication.model;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import spr018.tcss450.clientapplication.TabWeatherFragment;
/**Adapter calss holds information about the weather pages.
 * @author  Tuan Dinh Tenma Rollins Daryan Hanshew Deepjot Kaur
 */
public class WeatherCollectionPagerAdapter extends FragmentStatePagerAdapter {

    private List<TabWeatherFragment> mFragments;
    private List<String> mFragmentNames;

    public WeatherCollectionPagerAdapter(FragmentManager fragmentManager, List<TabWeatherFragment> fragments, List<String> fragmentNames) {
        super(fragmentManager);
        mFragments = fragments;
        mFragmentNames = fragmentNames;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentNames.get(position);
    }
}

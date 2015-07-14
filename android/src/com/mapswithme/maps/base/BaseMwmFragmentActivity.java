package com.mapswithme.maps.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.mapswithme.maps.MWMApplication;
import com.mapswithme.maps.R;
import com.mapswithme.util.Utils;
import com.mapswithme.util.statistics.Statistics;

import ru.mail.mrgservice.MRGService;

public class BaseMwmFragmentActivity extends AppCompatActivity
{
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    final int layoutId = getContentLayoutResId();
    if (layoutId != 0)
      setContentView(layoutId);

    // Use full-screen on Kindle Fire only
    if (Utils.isAmazonDevice())
    {
      getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
      getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }

    MWMApplication.get().initStats();

    attachDefaultFragment();
  }

  @Override
  protected void onStart()
  {
    super.onStart();
    Statistics.INSTANCE.startActivity(this);

    MRGService.instance().onStart(this);
  }

  @Override
  protected void onStop()
  {
    Statistics.INSTANCE.stopActivity(this);
    super.onStop();

    MRGService.instance().onStop(this);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    if (item.getItemId() == android.R.id.home)
    {
      onBackPressed();
      return true;
    }
    else
      return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    org.alohalytics.Statistics.logEvent("$onResume", this.getClass().getSimpleName()
        + ":" + com.mapswithme.util.UiUtils.deviceOrientationAsString(this));
  }

  @Override
  protected void onPause()
  {
    super.onPause();
    org.alohalytics.Statistics.logEvent("$onPause", this.getClass().getSimpleName());
  }

  protected Toolbar getToolbar()
  {
    return (Toolbar) findViewById(R.id.toolbar);
  }

  protected void displayToolbarAsActionBar()
  {
    setSupportActionBar(getToolbar());
  }

  /**
   * Override to set custom content view.
   * @return layout resId.
   */
  protected int getContentLayoutResId()
  {
    return 0;
  }

  protected void attachDefaultFragment()
  {
    final String fragmentName = getFragmentClassName();
    if (fragmentName != null)
      replaceFragment(getFragmentClassName(), false, getIntent().getExtras());
  }

  /**
   * Replace attached fragment with the new one.
   */
  public void replaceFragment(String fragmentClassName, boolean addToBackStack, Bundle args)
  {
    final int resId = getFragmentContentResId();
    if (findViewById(resId) == null)
      throw new IllegalStateException("Fragment can't be added, since getFragmentContentResId() isn't implemented or returns wrong resourceId.");

    final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    final Fragment fragment = Fragment.instantiate(this, fragmentClassName, args);
    transaction.replace(resId, fragment, fragmentClassName);
    if (addToBackStack)
      transaction.addToBackStack(null);
    transaction.commit();
  }

  /**
   * Override to automatically attach fragment in onCreate. Tag applied to fragment in back stack is set to fragment name, too.
   * WARNING : if custom layout for activity is set, getFragmentContentResId() must be implemented, too.
   * @return class name of the fragment, eg FragmentClass.getName()
   */
  protected String getFragmentClassName()
  {
    return null;
  }

  /**
   * Get resource id for the fragment. That must be implemented to return correct resource id, if custom layout is set.
   * @return resourceId for the fragment
   */
  protected int getFragmentContentResId()
  {
    return android.R.id.content;
  }
}

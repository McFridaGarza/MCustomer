package macguffinco.hellrazorbarber.Activities.Dashboard;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;

import macguffinco.hellrazorbarber.R;

public class Adapter extends FragmentPagerAdapter {

    Drawable myDrawable; //Drawable you want to display
    Context _context;
    public Adapter(FragmentManager fm,Context context) {
        super(fm);
        _context=context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0: return new GalleryFragment();
            case 1: return new AppointmentsFragment();
            case 2: return new ChatFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position)  {
        String title="";
        switch (position) {

            case 0: {title=  "GALERIA";break;}
            case 1: {title=  "CITAS";break;}
            case 2: {title=  "ULTIMA CITA";break;}
        }

        /*SpannableStringBuilder sb = new SpannableStringBuilder(); // space added before text for convenience

        // myDrawable = ContextCompat.getDrawable(, R.drawable.ic_baseline_event_24px);

       myDrawable=  ContextCompat.getDrawable(_context, R.drawable.ic_baseline_event_24px);

        myDrawable.setBounds(0, 0, myDrawable.getIntrinsicWidth(), myDrawable.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(myDrawable, ImageSpan.ALIGN_BASELINE);
        sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
*/
        return title;


    }





}

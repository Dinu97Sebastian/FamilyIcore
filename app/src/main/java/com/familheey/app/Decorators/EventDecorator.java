package com.familheey.app.Decorators;

import android.content.Context;
import android.graphics.Color;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;

public class EventDecorator implements DayViewDecorator {

    private final int color;
    private final HashSet<CalendarDay> dates;
    private Context context;

    public EventDecorator(Context context, int color, Collection<CalendarDay> dates) {
        this.color = color;
        this.dates = new HashSet<>(dates);
        this.context = context;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        /*Drawable drawable = ContextCompat.getDrawable(context, R.drawable.calendar_circle_selector);
        assert drawable != null;
        view.setBackgroundDrawable(drawable);*/
        view.addSpan(new DotSpan(8, Color.parseColor("#00b100")));
    }
}

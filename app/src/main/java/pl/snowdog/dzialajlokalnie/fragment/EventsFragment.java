package pl.snowdog.dzialajlokalnie.fragment;

import android.support.design.widget.Snackbar;
import android.util.Log;

import org.androidannotations.annotations.EFragment;

import java.util.Calendar;
import java.util.List;

import pl.snowdog.dzialajlokalnie.R;
import pl.snowdog.dzialajlokalnie.adapter.EventsAdapter;
import pl.snowdog.dzialajlokalnie.events.EventAttendEvent;
import pl.snowdog.dzialajlokalnie.events.ObjectAddedEvent;
import pl.snowdog.dzialajlokalnie.model.Event;
import pl.snowdog.dzialajlokalnie.model.ParticipateEvent;

/**
 * Created by bartek on 07.07.15.
 */
@EFragment(R.layout.fragment_list)
public class EventsFragment extends ListFragment {

    private static final String TAG = "EventsFragment";
    EventsAdapter adapter;

    @Override
    protected void afterView() {
        getEvents();
    }

    @Override
    protected void refreshItems() {
        super.refreshItems();
        getEvents();
    }

    @Override
    protected void eventsResult(List<Event> events) {
        adapter = new EventsAdapter(events);
        recyclerView.setAdapter(adapter);

        onItemsLoadComplete();
    }

    @Override
    protected boolean isImplementingEventBus() {
        return true;
    }

    public void onEvent(EventAttendEvent event) {
        Log.d(TAG, "onEvent " + event);

        final Calendar now = Calendar.getInstance();
        if(now.getTime().after(event.getEvent().getEndDate())) {
            Snackbar.make(getView(), getString(R.string.warning_event_ended), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }
        /*ParticipateEvent.ParcitipateType participateType;
        if (event.getEvent().getUserInEvent() != 1) {
            participateType = ParticipateEvent.ParcitipateType.attending;
        } else {
            participateType = ParticipateEvent.ParcitipateType.declined;
        }*/



        participate(new ParticipateEvent(event.getEvent().getEventID(), event.getParcitipateType()));

    }

    public void onEvent(ObjectAddedEvent event) {
        Log.d(TAG, "onEvent " + event);
        switch (event.getAdded()) {
            case event:
                Snackbar.make(getView(), getString(R.string.added_event_success_info), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
        }

        refreshItems();
    }

    @Override
    protected void participateResult(ParticipateEvent participateEvent) {
        for (int i = 0; i < adapter.getEvents().size(); i++) {
            Event event = adapter.getEvents().get(i);
            if (event.getEventID() == participateEvent.getEventID()) {
                if (event.getUserInEvent() != 1 && participateEvent.getParticipateType() == 1) {
                    event.setAttendingCount(event.getAttendingCount()+1);
                } else if (event.getUserInEvent() == 1 && participateEvent.getParticipateType() != 1) {
                    event.setAttendingCount(event.getAttendingCount()-1);
                }
                event.setUserInEvent(participateEvent.getParticipateType());
                adapter.notifyItemChanged(i);
                break;
            }
        }
    }
}

package pl.snowdog.dzialajlokalnie;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import java.io.File;
import java.util.Date;
import java.util.Locale;

import pl.snowdog.dzialajlokalnie.adapter.FragmentAdapter;
import pl.snowdog.dzialajlokalnie.events.CreateNewObjectEvent;
import pl.snowdog.dzialajlokalnie.events.ObjectAddedEvent;
import pl.snowdog.dzialajlokalnie.fragment.AddCategoriesFragment_;
import pl.snowdog.dzialajlokalnie.fragment.AddImageFragment_;
import pl.snowdog.dzialajlokalnie.fragment.AddLocationFragment_;
import pl.snowdog.dzialajlokalnie.fragment.AddTitleDateFragment;
import pl.snowdog.dzialajlokalnie.fragment.AddTitleDateFragment_;
import pl.snowdog.dzialajlokalnie.model.DateWrapper;
import pl.snowdog.dzialajlokalnie.model.Event;
import pl.snowdog.dzialajlokalnie.model.NewEvent;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by chomi3 on 2015-07-06.
 */
@EActivity(R.layout.activity_add)
public class AddEventActivity extends AddBaseActivity {
    private static final String TAG = "AddEventActivity";

    //Event specific fields:
    Date startDate;
    Date endDate;
    String facebookURL;

    @Extra
    Event mEditedEvent;

    @Override
    void setupViewPager(ViewPager viewPager) {
        Log.d(TAG, "setupViewPager");
        mViewPager.setOffscreenPageLimit(4);
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());

        adapter.addFragment(new AddTitleDateFragment_().builder()
                .mEditedObject(mEditedEvent != null ? new CreateNewObjectEvent.Builder()
                        .title(mEditedEvent.getTitle())
                        .description(mEditedEvent.getDescription())
                        .startDate(new DateWrapper(mEditedEvent.getStartDate()))
                        .endDate(new DateWrapper(mEditedEvent.getEndDate()))
                        .build() : null)
                .mAddingMode(AddTitleDateFragment.MODE_EVENT)
                .build());

        adapter.addFragment(new AddLocationFragment_().builder()
                .mEditedObject(mEditedEvent != null ? new CreateNewObjectEvent.Builder()
                        .lat(mEditedEvent.getLat())
                        .lon(mEditedEvent.getLon())
                        .districtID(mEditedEvent.getDistrictID())
                        .address(mEditedEvent.getAddress())
                        .build() : null)
                .build());

        adapter.addFragment(new AddImageFragment_().builder()
                .mEditedObject(mEditedEvent != null ? new CreateNewObjectEvent.Builder()
                        .image(mEditedEvent.getPhotoEventUri())
                        .build() : null)
                .build());

        adapter.addFragment(new AddCategoriesFragment_().builder()
                .mEditedObject(mEditedEvent != null ? new CreateNewObjectEvent.Builder()
                        .categoryIDs(mEditedEvent.getCategoryIdsList())
                        .build() : null)
                .build());

        viewPager.setAdapter(adapter);

        //Disable swipe events for viewpager
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    protected void afterView() {
        super.afterView();
        if (mEditedEvent != null) {
            getSupportActionBar().setTitle(getString(R.string.edit_event));
        }
    }

    public void onEvent(CreateNewObjectEvent event) {
        switch (event.getType()) {
            case date:
                //Consume Date setting
                title = event.getTitle();
                description = event.getDescription();
                endDate = event.getEndDate().getDate();
                startDate = event.getStartDate().getDate();
                goToNextPage();
                return;
            case category:
                categoryIDs = event.getCategoryIDs();
                if (mEditedEvent != null) {
                    putEvent();
                } else {
                    postEvent();
                }
                return;
        }
        super.onEvent(event);
    }

    private void postEvent() {
        toggleProgressWheel(true);
        NewEvent newEvent = createNewEventObject();

        DlApplication.eventApi.postEvent(newEvent, new Callback<Event>() {
            @Override
            public void success(Event eventWrapper, Response response) {
                Log.d(TAG, "eventApi post success: " + response + " newEventFromApi: " + eventWrapper.toString());
                if (photoUri != null && photoUri.length() > 0) {
                    putEventImage(eventWrapper.getEventID());
                } else {
                    //Finished adding, close view
                    finishAdding(ObjectAddedEvent.Type.event);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "eventApi post error: " + error);
                toggleProgressWheel(false);
            }
        });
    }

    private void putEvent() {
        toggleProgressWheel(true);
        NewEvent newEvent = createNewEventObject();
        DlApplication.eventApi.putEvent(newEvent, mEditedEvent.getEventID(), new Callback<Event>() {
            @Override
            public void success(Event eventWrapper, Response response) {
                Log.d(TAG, "eventApi post success: " + response + " newEventFromApi: " + eventWrapper.toString());
                if (photoUri != null && photoUri.length() > 0) {
                    putEventImage(eventWrapper.getEventID());
                } else {
                    //Finished adding, close view
                    finishAdding(ObjectAddedEvent.Type.event);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "eventApi post error: " + error);
                toggleProgressWheel(false);
            }
        });
    }

    private void putEventImage(int eventId) {

        TypedFile file = new TypedFile("image/jpg", new File(photoUri));

        DlApplication.eventApi.postEventImage(file, eventId, new Callback<Event>() {
            @Override
            public void success(Event issueWrapper, Response response) {
                Log.d(TAG, "eventApi put image success: " + response + " eventApi: " + issueWrapper.toString());
                //toggleProgressWheel(false);
                finishAdding(ObjectAddedEvent.Type.event);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "eventApi put imaget error: " + error);
                toggleProgressWheel(false);
            }
        });
    }

    @NonNull
    private NewEvent createNewEventObject() {
        NewEvent newEvent = new NewEvent();
        newEvent.setTitle(title);
        newEvent.setDescription(description);
        newEvent.setAddress(address);
        newEvent.setLocation(Double.toString(lat) + "," + Double.toString(lon));
        newEvent.setCategoryID(categoryIDs);
        newEvent.setDistrictID(districtID);
        newEvent.setStartDate(startDate);
        newEvent.setEndDate(endDate);
        return newEvent;
    }
}

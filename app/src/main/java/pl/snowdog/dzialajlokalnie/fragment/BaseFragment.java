package pl.snowdog.dzialajlokalnie.fragment;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;

import java.util.List;

import de.greenrobot.event.EventBus;
import pl.snowdog.dzialajlokalnie.DlApplication;
import pl.snowdog.dzialajlokalnie.api.DlApi;
import pl.snowdog.dzialajlokalnie.model.Category;
import pl.snowdog.dzialajlokalnie.model.Comment;
import pl.snowdog.dzialajlokalnie.model.District;
import pl.snowdog.dzialajlokalnie.model.Event;
import pl.snowdog.dzialajlokalnie.model.Filter;
import pl.snowdog.dzialajlokalnie.model.Issue;
import pl.snowdog.dzialajlokalnie.model.ParticipateEvent;
import pl.snowdog.dzialajlokalnie.model.Vote;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by bartek on 06.07.15.
 */
public abstract class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment";

    /**
     * Override this method to return true if you want to use EventBus and implemented onEvent method
     * @return
     */
    protected boolean isImplementingEventBus() {
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (isImplementingEventBus() && !EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        if (isImplementingEventBus()) {
            EventBus.getDefault().unregister(this);
        }

        super.onDestroy();
    }

    protected String parseCategories(String categoryID, List<Category> categories) {
        if (categoryID == null) {
            return null;
        }

        String[] catIds = categoryID.split(",");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < catIds.length; i++) {
            for (Category category : categories) {
                if (category.getCategoryID() == Integer.valueOf(catIds[i])) {
                    if (i > 0) {
                        stringBuilder.append(", ");
                    }

                    stringBuilder.append(category.getName());
                    break;
                }
            }
        }

        return stringBuilder.toString();
    }

    protected void getIssues() {
        Filter filter = DlApplication.filter;

        DlApplication.issueApi.getIssues(filter.getDistrictFilter(),
                filter.getCategoriesFilter(),
                filter.getSortForIssues(),
                new Callback<List<Issue>>() {
                    @Override
                    public void success(List<Issue> issues, Response response) {
                        Log.d(TAG, "getIssues success: " + issues);

                        List<Category> categories = new Select().from(Category.class).execute();
                        List<District> districts = new Select().from(District.class).execute();

                        for (Issue issue : issues) {
                            issue.parseCategoriesList();
                            issue.setCategoriesText(parseCategories(issue.getCategoryID(), categories));

                            for (District district : districts) {
                                if (district.getDistrictID() == issue.getDistrictID()) {
                                    issue.setDistrictName(district.getName());
                                    break;
                                }
                            }
                        }

                        issuesResult(issues);

                        ActiveAndroid.beginTransaction();
                        try {
                            for (Issue issue : issues) {
                                issue.save();
                            }
                            ActiveAndroid.setTransactionSuccessful();
                        } finally {
                            ActiveAndroid.endTransaction();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, "getIssues failure: " + error);
                    }
                });
    }

    //TODO protect calling this method from situation when Fragment is detached from activity (and other callback methods)
    protected void issuesResult(List<Issue> issues) {
        // implement by override
    }

    protected void getIssue(int id) {

        DlApplication.issueApi.getIssue(id,
                new Callback<Issue>() {
                    @Override
                    public void success(Issue issue, Response response) {
                        Log.d(TAG, "getIssue success: " + issue);

                        List<Category> categories = new Select().from(Category.class).execute();
                        issue.setCategoriesText(parseCategories(issue.getCategoryID(), categories));
                        issue.parseCategoriesList();

                        District district = new Select().from(District.class).
                                where("districtID == ?", issue.getDistrictID()).executeSingle();

                        if (district != null) {
                            issue.setDistrictName(district.getName());
                        }
                        issueResult(issue);

                        issue.save();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, "getIssue failure: " + error);
                    }
                });
    }

    protected void issueResult(Issue issue) {
        // implement by override
    }

    protected void getEvents() {
        Filter filter = DlApplication.filter;

        DlApplication.eventApi.getEvents(filter.getDistrictFilter(),
                filter.getCategoriesFilter(),
                filter.getSortForEvents(),
                new Callback<List<Event>>() {
                    @Override
                    public void success(List<Event> events, Response response) {
                        Log.d(TAG, "getEvents success: " + events);

                        List<Category> categories = new Select().from(Category.class).execute();
                        List<District> districts = new Select().from(District.class).execute();

                        for (Event event : events) {
                            event.parseCategoriesList();
                            event.setCategoriesText(parseCategories(event.getCategoryID(), categories));

                            for (District district : districts) {
                                if (district.getDistrictID() == event.getDistrictID()) {
                                    event.setDistrictName(district.getName());
                                    break;
                                }
                            }
                        }

                        eventsResult(events);

                        ActiveAndroid.beginTransaction();
                        try {
                            for (Event event : events) {
                                event.save();
                            }
                            ActiveAndroid.setTransactionSuccessful();
                        } finally {
                            ActiveAndroid.endTransaction();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, "getEvents failure: " + error);
                    }
                });
    }

    protected void eventsResult(List<Event> events) { }


    protected void getEvent(int id) {

        DlApplication.eventApi.getEvent(id,
                new Callback<Event>() {
                    @Override
                    public void success(Event event, Response response) {
                        Log.d(TAG, "getEvent success: " + event);

                        List<Category> categories = new Select().from(Category.class).execute();
                        event.setCategoriesText(parseCategories(event.getCategoryID(), categories));
                        event.parseCategoriesList();

                        District district = new Select().from(District.class).
                                where("districtID == ?", event.getDistrictID()).executeSingle();

                        if (district != null) {
                            event.setDistrictName(district.getName());
                        }
                        eventResult(event);

                        event.save();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, "getEvent failure: " + error);
                    }
                });
    }

    protected void eventResult(Event event) {
        // implement by override
    }


    protected void getComments(DlApi.ParentType parentType, int parentId) {
        DlApplication.commentApi.getComments(parentType.name(), parentId, new Callback<List<Comment>>() {
            @Override
            public void success(List<Comment> comments, Response response) {
                Log.d(TAG, "getComments success: " + comments);

                commentsResult(comments);

                ActiveAndroid.beginTransaction();
                try {
                    for (Comment comment : comments) {
                        comment.save();
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "getComments failure: " + error);
            }
        });
    }

    protected void commentsResult(List<Comment> comments) { }

    protected void vote(DlApi.ParentType parentType, int parentId, int value) {
        DlApplication.voteApi.vote(parentType.name(), parentId, new Vote(value), new Callback<Vote>() {
            @Override
            public void success(Vote vote, Response response) {
                Log.d(TAG, "vote success: " + vote);
                voteResult(vote);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "vote failure: " + error);
            }
        });
    }

    protected void voteResult(Vote vote) { }

    protected void participate(ParticipateEvent participateEvent) {
        DlApplication.eventApi.putParticipateEvent(participateEvent, participateEvent.getEventID(),
                new Callback<ParticipateEvent>() {
            @Override
            public void success(ParticipateEvent participateEvent, Response response) {
                Log.d(TAG, "participate success: " + participateEvent);
                participateResult(participateEvent);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "participate failure: " + error);
            }
        });
    }

    protected void participateResult(ParticipateEvent participateEvent) { }
}

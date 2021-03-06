package pl.snowdog.dzialajlokalnie.fragment;

import android.databinding.DataBindingUtil;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import de.greenrobot.event.EventBus;
import pl.snowdog.dzialajlokalnie.AddIssueActivity_;
import pl.snowdog.dzialajlokalnie.DlApplication;
import pl.snowdog.dzialajlokalnie.R;
import pl.snowdog.dzialajlokalnie.api.DlApi;
import pl.snowdog.dzialajlokalnie.databinding.FragmentIssueBinding;
import pl.snowdog.dzialajlokalnie.events.IssueVoteEvent;
import pl.snowdog.dzialajlokalnie.events.RefreshEvent;
import pl.snowdog.dzialajlokalnie.events.SetTitleAndPhotoEvent;
import pl.snowdog.dzialajlokalnie.events.VoteEvent;
import pl.snowdog.dzialajlokalnie.model.Issue;
import pl.snowdog.dzialajlokalnie.model.Vote;
import pl.snowdog.dzialajlokalnie.util.CircleTransform;

/**
 * Created by bartek on 15.07.15.
 */

@EFragment(R.layout.fragment_issue)
@OptionsMenu(R.menu.menu_details)
public class IssueFragment extends BaseFragment implements OnMapReadyCallback {

    private static final String TAG = "IssueFragment";
    FragmentIssueBinding binding;

    @FragmentArg
    int objId;

    @ViewById(R.id.issueDetails)
    View rootView;

    private SupportMapFragment mapFragment;
    private GoogleMap map;

    @OptionsMenuItem(R.id.action_edit)
    MenuItem menuEdit;

    @AfterViews
    void afterViews() {
        binding = DataBindingUtil.bind(rootView);
        getIssue(objId);


    }

    @Click(R.id.ibRateUp)
    protected void rateUp() {
        rate(IssueVoteEvent.Vote.UP);
    }

    @Click(R.id.ibRateDown)
    protected void rateDown() {
        rate(IssueVoteEvent.Vote.DOWN);
    }

    private void rate(IssueVoteEvent.Vote vote) {
        if (binding.getIssue() != null) {
            vote(DlApi.ParentType.issues, binding.getIssue().getIssueID(), vote == VoteEvent.Vote.UP ? 1 : -1);
        }
    }

    @Override
    protected void issueResult(Issue issue) {
        Log.d(TAG, "issueResult " + issue);
        binding.setIssue(issue);

        EventBus.getDefault().post(new SetTitleAndPhotoEvent(issue.getTitle(),
                String.format(DlApi.PHOTO_NORMAL_URL, issue.getPhotoIssueUri())));

        Picasso.with(binding.getRoot().getContext()).
                load(String.format(DlApi.AVATAR_THUMB_URL, issue.getAuthorAvatar())).
                error(R.drawable.ic_editor_insert_emoticon).
                transform(new CircleTransform()).
                into(binding.voteCard.ivAuthorAvatar);

        GoogleMapOptions options = new GoogleMapOptions().liteMode(true);
        options.camera(new CameraPosition(new LatLng(issue.getLat(), issue.getLon()), 15, 0, 0));
        mapFragment = SupportMapFragment.newInstance(options);
        getChildFragmentManager().beginTransaction().add(R.id.mapCard, mapFragment).commit();
        mapFragment.getMapAsync(this);
        menuEdit.setVisible(isLoggedInUserAuthor());
    }

    @Override
    protected boolean isImplementingEventBus() {
        return true;
    }

    public void onEvent(RefreshEvent event) {
        Log.d(TAG, "onEvent " + event);
        getIssue(objId);
    }

    @Override
    protected void voteResult(Vote vote) {
        Issue issue = binding.getIssue();
        if (issue.getIssueID() == vote.getParentID()) {
            issue.setIssueRating(issue.getIssueRating() + vote.getValue());
            issue.setUserVotedValue(vote.getValue());
            //TODO - this is dirty implementation. Observables shoud be used but it requires extending BaseObservable - conflict with Model
            binding.setIssue(issue);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);

        Marker marker = map.addMarker(new MarkerOptions().
                position(new LatLng(binding.getIssue().getLat(), binding.getIssue().getLon())).
                title(binding.getIssue().getAddress()).
                snippet(binding.getIssue().getDistrictName()).
                icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_issue_marker)));
        marker.showInfoWindow();
    }

    @OptionsItem(R.id.action_edit)
    void edit() {
        AddIssueActivity_.intent(this).mEditedIssue(binding.getIssue()).start();
    }

    private boolean isLoggedInUserAuthor() {
        if(DlApplication.currentSession != null) {
            return DlApplication.currentSession.getUserID() == binding.getIssue().getUserID();
        } else {
            return false;
        }
    }
}

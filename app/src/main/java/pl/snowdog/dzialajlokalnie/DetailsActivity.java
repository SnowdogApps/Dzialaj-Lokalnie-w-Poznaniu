package pl.snowdog.dzialajlokalnie;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import de.greenrobot.event.EventBus;
import pl.snowdog.dzialajlokalnie.adapter.FragmentAdapter;
import pl.snowdog.dzialajlokalnie.api.DlApi;
import pl.snowdog.dzialajlokalnie.databinding.AddCommentBinding;
import pl.snowdog.dzialajlokalnie.events.CommentClickedEvent;
import pl.snowdog.dzialajlokalnie.events.CommentsLoadedEvent;
import pl.snowdog.dzialajlokalnie.events.NewCommentEvent;
import pl.snowdog.dzialajlokalnie.events.RefreshEvent;
import pl.snowdog.dzialajlokalnie.events.SetTitleAndPhotoEvent;
import pl.snowdog.dzialajlokalnie.fragment.CommentsFragment;
import pl.snowdog.dzialajlokalnie.fragment.CommentsFragment_;
import pl.snowdog.dzialajlokalnie.fragment.EventFragment_;
import pl.snowdog.dzialajlokalnie.fragment.IssueFragment;
import pl.snowdog.dzialajlokalnie.fragment.IssueFragment_;
import pl.snowdog.dzialajlokalnie.gcm.NotificationAction;
import pl.snowdog.dzialajlokalnie.model.Comment;
import pl.snowdog.dzialajlokalnie.util.FadeInAnimation;
import pl.snowdog.dzialajlokalnie.util.FadeOutAnimation;
import pl.snowdog.dzialajlokalnie.view.ControllableAppBarLayout;

@EActivity(R.layout.activity_details)
@OptionsMenu(R.menu.menu_issue)
public class DetailsActivity extends BaseActivity {

    private static final String TAG = "DetailsActivity";

    @ViewById(R.id.appbar)
    ControllableAppBarLayout appBarLayout;

    @ViewById(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @ViewById(R.id.ivAvatar)
    ImageView ivAvatar;

    @ViewById(R.id.avatarOverlay)
    View avatarOverlay;

    @ViewById(R.id.tabs)
    TabLayout tabLayout;

    @ViewById(R.id.pager)
    ViewPager viewPager;

    @ViewById(R.id.focus_background)
    View focusBackground;

    @ViewById(R.id.add_comment_widget)
    View addCommentWidget;

    @ViewById(R.id.fab)
    FloatingActionButton fab;

    AddCommentBinding binding;

    @Extra
    DlApi.ParentType objType;

    @Extra
    int objId;

    private FragmentAdapter adapter;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        parseIntent(intent);
    }

    private void parseIntent(Intent intent) {
        if(intent == null) return;

        Bundle extras = intent.getExtras();
        if (extras != null) {
            int action = extras.getInt(NotificationAction.INTENT_ACTION);
            int objectId = extras.getInt(NotificationAction.ACTION_VALUE);
            switch (action) {
                case NotificationAction.NEW_ISSUE_SURROUND:
                    objType = DlApi.ParentType.issues;
                    objId = objectId;
                    break;
                case NotificationAction.NEW_EVENT_SURROUND:
                    objType = DlApi.ParentType.events;
                    objId = objectId;
                    break;
                case NotificationAction.EDIT_ISSUE:
                    objType = DlApi.ParentType.issues;
                    objId = objectId;
                    break;
                case NotificationAction.EDIT_EVENT:
                    objType = DlApi.ParentType.events;
                    objId = objectId;
                    break;
                case NotificationAction.EVENT_REMINDER:
                    objType = DlApi.ParentType.events;
                    objId = objectId;
                    break;
                case NotificationAction.COMMENT_TO_ISSUE:
                    objType = DlApi.ParentType.issues;
                    objId = objectId;
                    break;
                case NotificationAction.COMMENT_TO_EVENT:
                    objType = DlApi.ParentType.events;
                    objId = objectId;
                    break;
            }
        }
    }

    @Override
    protected void afterView() {
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        parseIntent(getIntent());

        Log.d(TAG, "afterView " + objType + " " + objId);

        Fragment firstFragment = null;
        if (objType == DlApi.ParentType.issues) {
            firstFragment = IssueFragment_.builder().arg("objId", objId).build();
        } else if (objType == DlApi.ParentType.events) {
            firstFragment = EventFragment_.builder().arg("objId", objId).build();
        }

        CommentsFragment commentsFragment = CommentsFragment_.builder().arg("objId", objId).
                arg("objType", objType).build();

        adapter = new FragmentAdapter(getSupportFragmentManager());
        if (firstFragment != null) {
            adapter.addFragment(firstFragment, getString(R.string.details_title_section1));
        }
        adapter.addFragment(commentsFragment, getString(R.string.details_title_section2));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);

        binding = new AddCommentBinding(addCommentWidget);
        binding.etComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    send();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "back button pressed");

        //TODO onBackPressed does not catch closing the soft keyboard. Possible solution: http://tech.leolink.net/2014/02/a-hack-to-catch-soft-keyboard-showhide.html
        if (binding.etComment.hasFocus()) {
            unfocus();
        } else {
            super.onBackPressed();
        }
    }

    @FocusChange(R.id.et_comment)
    protected void onFocusChangedOnNewComment(View v, boolean hasFocus) {
        if (hasFocus) {
            new FadeInAnimation(focusBackground).startAnimation();
            new FadeOutAnimation(fab).startAnimation();
        } else {
            new FadeOutAnimation(focusBackground).startAnimation();
            new FadeInAnimation(fab).startAnimation();
        }
    }

    @Click(R.id.focus_background)
    protected void unfocus() {
        binding.etComment.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.etComment.getWindowToken(), 0);

        binding.clear();
    }

    @Click(R.id.bt_send)
    protected void send() {
        //TODO how to send Reports to api? binding.ctvReport = true

        if (binding.getComment() != null) {
            comment(DlApi.ParentType.comments, binding.getComment().getCommentID(),
                    binding.ctvSolution.isChecked() ? 1 : 0,
                    binding.etComment.getText().toString());
        } else {
            comment(objType, objId,
                    binding.ctvSolution.isChecked() ? 1 : 0,
                    binding.etComment.getText().toString());
        }
        unfocus();
    }

    public void onEvent(SetTitleAndPhotoEvent event) {
        collapsingToolbarLayout.setTitle(event.getTitle());

        Picasso.with(binding.getRoot().getContext()).
                load(event.getPhotoUrl()).
                error(R.drawable.ic_editor_insert_emoticon). //TODO change error drawable and remove setting INVISIBLE below
                into(ivAvatar, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
                appBarLayout.collapseToolbar(true);
                ivAvatar.setVisibility(View.INVISIBLE);
                // TODO lock expanding and collapsing the appbar because there is no photo
            }
        });


        //TODO we need to handle clicks on imageview in header to open zoom details activity
        //Unfortunately CollapsingToolbarLayout is not passing click events, oh yeah! sweet android:(
        /*ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageZoomActivity_.intent(DetailsActivity.this).imageBitmap(((BitmapDrawable)ivAvatar.getDrawable()).getBitmap()).start();
            }
        });*/
    }

    @Click(R.id.fab)
    protected void openAddCommentWidget() {
        binding.etComment.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void onEvent(CommentClickedEvent event) {
        binding.setComment(event.getComment());
        binding.itemComment.getRoot().setVisibility(View.VISIBLE);
        binding.etComment.setHint(R.string.response_hint);

        Picasso.with(binding.getRoot().getContext()).
                load(String.format(DlApi.PHOTO_THUMB_URL, event.getComment().getAuthorAvatar())).
                error(R.drawable.ic_editor_insert_emoticon).
                into(binding.itemComment.ivAvatar);

        openAddCommentWidget();
    }

    public void onEvent(CommentsLoadedEvent event) {
        adapter.setPageTitle(1, getString(R.string.details_title_section2_number, event.getCount()));
        tabLayout.getTabAt(1).setText(adapter.getPageTitle(1));
    }

    @Override
    protected void commentResult(Comment comment) {
        binding.etComment.setText("");
        EventBus.getDefault().post(new NewCommentEvent(comment));
    }

    @OptionsItem(R.id.action_refresh)
    void refresh() {
        EventBus.getDefault().post(new RefreshEvent());
    }
}

package com.github.rezalotfi01.weberpro.Activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.github.rezalotfi01.weberpro.BaseClasses.WeberActivity;
import com.github.rezalotfi01.weberpro.Utils.BrowserUtils;
import com.github.rezalotfi01.weberpro.Utils.IntentUtils;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.github.rezalotfi01.weberpro.Browser.AdBlock;
import com.github.rezalotfi01.weberpro.Browser.AlbumController;
import com.github.rezalotfi01.weberpro.Browser.BrowserContainer;
import com.github.rezalotfi01.weberpro.Browser.BrowserController;
import com.github.rezalotfi01.weberpro.Database.Record;
import com.github.rezalotfi01.weberpro.Database.RecordAction;
import com.github.rezalotfi01.weberpro.Fragment.DialogDownloadFragment;
import com.github.rezalotfi01.weberpro.R;
import com.github.rezalotfi01.weberpro.Service.BubbleButtonService;
import com.github.rezalotfi01.weberpro.Service.ClearService;
import com.github.rezalotfi01.weberpro.Service.HolderService;
import com.github.rezalotfi01.weberpro.Task.ScreenshotTask;
import com.github.rezalotfi01.weberpro.Utils.ViewUtils;
import com.github.rezalotfi01.weberpro.View.CompleteAdapter;
import com.github.rezalotfi01.weberpro.View.DialogAdapter;
import com.github.rezalotfi01.weberpro.View.FullscreenHolder;
import com.github.rezalotfi01.weberpro.View.GridAdapter;
import com.github.rezalotfi01.weberpro.View.GridItem;
import com.github.rezalotfi01.weberpro.View.RecordAdapter;
import com.github.rezalotfi01.weberpro.View.SwipeToBoundListener;
import com.github.rezalotfi01.weberpro.View.SwitcherPanel;
import com.github.rezalotfi01.weberpro.View.WeberRelativeLayout;
import com.github.rezalotfi01.weberpro.View.WeberToast;
import com.github.rezalotfi01.weberpro.View.WeberWebView;

import org.askerov.dynamicgrid.DynamicGridView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FloatingActivity extends WeberActivity implements BrowserController {

    private static final int DOUBLE_TAPS_QUIT_DEFAULT = 2000;

    private SwitcherPanel switcherPanel;
    private float dimen156dp;
    private float dimen144dp;
    private float dimen117dp;
    private float dimen108dp;
    private float dimen48dp;

    private HorizontalScrollView switcherScroller;
    private LinearLayout switcherContainer;

    private TextView searchEngineHint;
    private ImageView searchEngineLogo;

    private Typeface typeface;

    private RelativeLayout omnibox;
    private AutoCompleteTextView inputBox;
    private ImageButton omniboxBookmark;
    private ImageButton omniboxRefresh;
    private Drawer drawerResult;
    private ProgressBar progressBar;

    private RelativeLayout searchPanel;
    private EditText searchBox;

    private Button relayoutOK;
    private FrameLayout contentFrame;
    private SwipeRefreshLayout contentRefreshLayout;

    private class VideoCompletionListener implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            onHideCustomView();
        }
    }
    private FullscreenHolder fullscreenHolder;
    private View customView;
    private VideoView videoView;
    private int originalOrientation;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private ValueCallback<Uri> uploadMsg = null;
    private ValueCallback<Uri[]> filePathCallback = null;

    private static boolean quit = false;
    private boolean create = true;
    private int shortAnimTime = 0;
    private int mediumAnimTime = 0;
    private int longAnimTime = 0;
    private AlbumController currentAlbumController = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            filePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
            ActivityManager.TaskDescription description = new ActivityManager.TaskDescription(
                    getString(R.string.app_name),
                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher),
                    getResources().getColor(R.color.background_dark)
            );
            setTaskDescription(description);
        }

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int anchor = Integer.valueOf(sp.getString(getString(R.string.sp_anchor), "1"));
        if (anchor == 0) {
            setContentView(R.layout.main_top);
        } else {
            setContentView(R.layout.main_bottom);
        }

        create = true;
        shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mediumAnimTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        longAnimTime = getResources().getInteger(android.R.integer.config_longAnimTime);
        switcherPanel = findViewById(R.id.switcher_panel);
        switcherPanel.setStatusListener(new SwitcherPanel.StatusListener() {
            @Override
            public void onFling() {}

            @Override
            public void onExpanded() {}

            @Override
            public void onCollapsed() {
                inputBox.clearFocus();
            }
        });

        dimen156dp = getResources().getDimensionPixelSize(R.dimen.layout_width_156dp);
        dimen144dp = getResources().getDimensionPixelSize(R.dimen.layout_width_144dp);
        dimen117dp = getResources().getDimensionPixelSize(R.dimen.layout_height_117dp);
        dimen108dp = getResources().getDimensionPixelSize(R.dimen.layout_height_108dp);
        dimen48dp = getResources().getDimensionPixelOffset(R.dimen.layout_height_48dp);


        typeface = ViewUtils.INSTANCE.getTypeface();

        initSwitcherView();
        initOmnibox();
        initSearchPanel();
        relayoutOK = findViewById(R.id.main_relayout_ok);

        contentFrame = findViewById(R.id.main_content);

        new AdBlock(this); // For AdBlock cold boot
        try {
            dispatchIntent(getIntent());
        }catch (Exception e){
            Log.e("Weber TAG", "onCreate Floating Activity Exception : "+e.toString());
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

/*        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        switch (screenSize) {

            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                this.getWindow().setLayout(900, 755);
                break;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                this.getWindow().setLayout(1080, 1000); //width x height
                break;
        }*/
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //initMaterialDrawer();
        //initBubbleButton();
    }



    @Override
    protected void onStart() {
        super.onStart();
        try {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            int num = Integer.valueOf(sp.getString(getString(R.string.sp_search_engine), "0"));
            if (num == 0) {
                searchEngineLogo.setImageResource(R.drawable.ic_google_compressed);
                Log.e("TAG", "initSearchBox : already is google search engine");
            } else if (num == 1) {
                searchEngineLogo.setImageResource(R.drawable.ic_duckduckgo_compressed);
            } else if (num == 2) {
                searchEngineLogo.setImageResource(R.drawable.ic_yahoo_compressed);
            } else if (num == 3) {
                searchEngineLogo.setImageResource(R.drawable.ic_bing_compressed);
            }
        }catch (Exception e){
            Log.e("TAG", "onStart Exception : "+e.toString());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentUtils.setContext(this);
        if (create) {
            return;
        }

        dispatchIntent(getIntent());

        if (IntentUtils.isDBChange()) {
            updateBookmarks();
            updateAutoComplete();
            IntentUtils.setDBChange(false);
        }

        if (IntentUtils.isSPChange()) {
            for (AlbumController controller : BrowserContainer.list()) {
                if (controller instanceof WeberWebView) {
                    ((WeberWebView) controller).initPreferences();
                }
            }

            IntentUtils.setSPChange(false);
        }


        String text = getIntent().getStringExtra("CopyText");
        if (text != null){
            if (currentAlbumController == null) {// || !(actionId == EditorInfo.IME_ACTION_DONE)
                Log.e("Weber TAG", "onResume current album is null");
                return;
            }

            String query = text.trim();
            if (query.isEmpty()) {
                WeberToast.Companion.show(FloatingActivity.this, R.string.toast_input_empty);
                return;
            }

            updateAlbum(query);
            //hideSoftInput(inputBox);
        }


    }

    private void dispatchIntent(Intent intent) {
        Intent toHolderService = new Intent(this, HolderService.class);
        IntentUtils.setClear(false);
        stopService(toHolderService);

        if (intent != null && intent.hasExtra(IntentUtils.OPEN)) { // From HolderActivity's menu
            pinAlbums(intent.getStringExtra(IntentUtils.OPEN));
        } else if (intent != null && intent.getAction() != null && intent.getAction().equals(Intent.ACTION_WEB_SEARCH)) { // From ActionMode and some others
            pinAlbums(intent.getStringExtra(SearchManager.QUERY));
        } else if (intent != null && filePathCallback != null) {
            filePathCallback = null;
        } else {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            if (sp.getBoolean(getString(R.string.sp_first), true)) {
                pinAlbums(BrowserUtils.BASE_URL);
                sp.edit().putBoolean(getString(R.string.sp_first), false).apply();
            } else {
                pinAlbums(null);
            }
        }
    }

    @Override
    public void onPause() {
        Intent toHolderService = new Intent(this, HolderService.class);
        IntentUtils.setClear(false);
        stopService(toHolderService);

        create = false;
        inputBox.clearFocus();
        if (currentAlbumController instanceof WeberRelativeLayout) {
            WeberRelativeLayout layout = (WeberRelativeLayout) currentAlbumController;
            if (layout.getFlag() == BrowserUtils.FLAG_HOME) {
                DynamicGridView gridView = layout.findViewById(R.id.home_grid);
                if (gridView.isEditMode()) {
                    gridView.stopEditMode();
                    relayoutOK.setVisibility(View.GONE);
                    omnibox.setVisibility(View.VISIBLE);
                    initHomeGrid(layout, true);
                }
            }
        }

        IntentUtils.setContext(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Intent toHolderService = new Intent(this, HolderService.class);
        IntentUtils.setClear(true);
        stopService(toHolderService);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean exit = true;
        if (sp.getBoolean(getString(R.string.sp_clear_quit), false)) {
            Intent toClearService = new Intent(this, ClearService.class);
            startService(toClearService);
            exit = false;
        }

        BrowserContainer.clear();
        IntentUtils.setContext(null);
        super.onDestroy();
        if (exit) {
            //System.exit(0); // For remove all WebView thread
/*            Context context = getApplicationContext();
            List<DownloadEntity> resumingDownloads = DownloadDBUtils.getDownloadsByStatus(context, DownloadEntity.FIELD_VALUE_STATUS_RESUMING);
            List<DownloadEntity> inQueueDownloads = DownloadDBUtils.getDownloadsByStatus(context, DownloadEntity.FIELD_VALUE_STATUS_IN_QUEUE);
            if (resumingDownloads.size() <=0 && inQueueDownloads.size() <= 0 && JobManager.instance().getAllJobs().size() <= 0){
                //set maxDownloadSize in shared preference to 0
                String myPackName = context.getPackageName();
                SharedPreferences.Editor editor = context.getSharedPreferences(myPackName, MODE_PRIVATE).edit();
                editor.putString("max_volume_size_each_download", "0");
                editor.putString("max_volume_size_all_downloads", "0");
                editor.putBoolean("check_box_download_limit",false);
                editor.commit();
                Log.e("Weber TAG", "onDestroy: No Running Service and Job, Jobs Number : "+ JobManager.instance().getAllJobs().size());
            }*/
            finish();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (currentAlbumController instanceof WeberRelativeLayout) {
            WeberRelativeLayout layout = (WeberRelativeLayout) currentAlbumController;
            if (layout.getFlag() == BrowserUtils.FLAG_HOME) {
                DynamicGridView gridView = layout.findViewById(R.id.home_grid);
                if (gridView.isEditMode()) {
                    gridView.stopEditMode();
                    relayoutOK.setVisibility(View.GONE);
                    omnibox.setVisibility(View.VISIBLE);
                }
            }
        }

        hideSoftInput(inputBox);
        hideSearchPanel();
        if (switcherPanel.getStatus() != SwitcherPanel.Status.EXPANDED) {
            switcherPanel.expanded();
        }
        super.onConfigurationChanged(newConfig);

        float coverHeight = ViewUtils.INSTANCE.getWindowHeight(this) - ViewUtils.INSTANCE.getStatusBarHeight(this) - dimen108dp - dimen48dp;
        switcherPanel.setCoverHeight(coverHeight);
        switcherPanel.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                switcherPanel.fixKeyBoardShowing(switcherPanel.getHeight());
                switcherPanel.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        if (currentAlbumController instanceof WeberRelativeLayout) {
            WeberRelativeLayout layout = (WeberRelativeLayout) currentAlbumController;
            if (layout.getFlag() == BrowserUtils.FLAG_HOME) {
                initHomeGrid(layout, true);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            // When video fullscreen, just control the sound
            return !(fullscreenHolder != null || customView != null || videoView != null) && onKeyCodeVolumeUp();
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // When video fullscreen, just control the sound
            return !(fullscreenHolder != null || customView != null || videoView != null) && onKeyCodeVolumeDown();
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true; //showDrawer();
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            // When video fullscreen, first close it
            if (fullscreenHolder != null || customView != null || videoView != null) {
                return onHideCustomView();
            }
            return onKeyCodeBack(true);
        }

        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // When video fullscreen, just control the sound
        if (fullscreenHolder != null || customView != null || videoView != null) {
            return false;
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            int vc = Integer.valueOf(sp.getString(getString(R.string.sp_volume), "1"));
            if (vc != 2) {
                return true;
            }
        }

        return false;
    }

    private void initSwitcherView() {
        switcherScroller = findViewById(R.id.switcher_scroller);
        switcherContainer = findViewById(R.id.switcher_container);
        ImageButton switcherBack = findViewById(R.id.switcher_back_page);
        //switcherBookmarks = (ImageButton) findViewById(R.id.switcher_bookmarks);
        ImageButton switcherForward = findViewById(R.id.switcher_forward_page);
        ImageButton switcherAdd = findViewById(R.id.switcher_add);

        switcherBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to back page
                if (currentAlbumController instanceof WeberWebView && ((WeberWebView) currentAlbumController).canGoBack())
                {
                    ((WeberWebView) currentAlbumController).goBack();
                }
            }
        });

        switcherForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to next page
                if (currentAlbumController instanceof WeberWebView && ((WeberWebView) currentAlbumController).canGoForward())
                {
                    ((WeberWebView) currentAlbumController).goForward();
                }
            }
        });


        switcherAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlbum(BrowserUtils.FLAG_HOME);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initOmnibox() {
        omnibox = findViewById(R.id.main_omnibox);
        inputBox = findViewById(R.id.main_omnibox_input);
        inputBox.setTypeface(typeface);
        omniboxBookmark = findViewById(R.id.main_omnibox_bookmark);
        omniboxRefresh = findViewById(R.id.main_omnibox_refresh);
        ImageButton omniboxOverflow = findViewById(R.id.main_omnibox_overflow);
        progressBar = findViewById(R.id.main_progress_bar);

        inputBox.setOnTouchListener(new SwipeToBoundListener(omnibox, new SwipeToBoundListener.BoundCallback() {
            private final KeyListener keyListener = inputBox.getKeyListener();

            @Override
            public boolean canSwipe() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(FloatingActivity.this);
                boolean ob = sp.getBoolean(getString(R.string.sp_omnibox_control), true);
                return !switcherPanel.isKeyBoardShowing() && ob;
            }

            @Override
            public void onSwipe() {
                inputBox.setKeyListener(null);
                inputBox.setFocusable(false);
                inputBox.setFocusableInTouchMode(false);
                inputBox.clearFocus();
            }

            @Override
            public void onBound(boolean canSwitch, boolean left) {
                inputBox.setKeyListener(keyListener);
                inputBox.setFocusable(true);
                inputBox.setFocusableInTouchMode(true);
                inputBox.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                inputBox.clearFocus();

                if (canSwitch) {
                    AlbumController controller = nextAlbumController(left);
                    showAlbum(controller, false, false, true);
                    WeberToast.Companion.show(FloatingActivity.this, controller.getAlbumTitle());
                }
            }
        }));

        inputBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (currentAlbumController == null) { // || !(actionId == EditorInfo.IME_ACTION_DONE)
                    return false;
                }

                String query = inputBox.getText().toString().trim();
                if (query.isEmpty()) {
                    WeberToast.Companion.show(FloatingActivity.this, R.string.toast_input_empty);
                    return true;
                }

                updateAlbum(query);
                hideSoftInput(inputBox);
                return false;
            }
        });
        inputBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    try {
                        searchEngineHint.setText(getString(R.string.main_search_engine_input_hint));
                    }catch (Exception e){
                        Log.e("Weber TAG", "onFocusChange Exception : "+e.toString());
                    }
                    searchBox.setVisibility(View.VISIBLE);
                }
                else {
                    searchBox.setVisibility(View.INVISIBLE);
                }
            }
        });
        updateBookmarks();
        updateAutoComplete();

        omniboxBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!prepareRecord()) {
                    WeberToast.Companion.show(FloatingActivity.this, R.string.toast_add_bookmark_failed);
                    return;
                }

                WeberWebView weberWebView = (WeberWebView) currentAlbumController;
                String title = weberWebView.getTitle();
                String url = weberWebView.getUrl();

                RecordAction action = new RecordAction(FloatingActivity.this);
                action.open(true);
                if (action.checkBookmark(url)) {
                    action.deleteBookmark(url);
                    WeberToast.Companion.show(FloatingActivity.this, R.string.toast_delete_bookmark_successful);
                } else {
                    action.addBookmark(new Record(title, url, System.currentTimeMillis()));
                    WeberToast.Companion.show(FloatingActivity.this, R.string.toast_add_bookmark_successful);
                }
                action.close();

                updateBookmarks();
                updateAutoComplete();
            }
        });

        omniboxRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentAlbumController == null) {
                    WeberToast.Companion.show(FloatingActivity.this, R.string.toast_refresh_failed);
                    return;
                }

                if (currentAlbumController instanceof WeberWebView) {
                    WeberWebView weberWebView = (WeberWebView) currentAlbumController;
                    if (weberWebView.isLoadFinish()) {
                        weberWebView.reload();
                    } else {
                        weberWebView.stopLoading();
                    }
                } else if (currentAlbumController instanceof WeberRelativeLayout) {
                    final WeberRelativeLayout layout = (WeberRelativeLayout) currentAlbumController;
                    if (layout.getFlag() == BrowserUtils.FLAG_HOME) {
                        initHomeGrid(layout, true);
                        return;
                    }
                    initBHList(layout, true);
                } else {
                    WeberToast.Companion.show(FloatingActivity.this, R.string.toast_refresh_failed);
                }
            }
        });

        omniboxOverflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDrawer();
            }
        });



/*
        contentRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
        contentRefreshLayout.setColorSchemeResources(R.color.blue_500 , R.color.md_orange_500 , R.color.md_pink_500);
        contentRefreshLayout.setHorizontalScrollBarEnabled(true);
        contentRefreshLayout.setVerticalScrollBarEnabled(true);
        contentRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (currentAlbumController == null) {
                    WeberToast.show(FloatingActivity.this, R.string.toast_refresh_failed);
                    setRefreshing(false);
                    return;
                }

                if (currentAlbumController instanceof WeberWebView) {
                    WeberWebView weberWebView = (WeberWebView) currentAlbumController;
                    if (weberWebView.isLoadFinish()) {
                        weberWebView.reload();
                        setRefreshing(false);
                    } else {
                        weberWebView.stopLoading();
                        setRefreshing(false);
                    }
                } else if (currentAlbumController instanceof WeberRelativeLayout) {
                    final WeberRelativeLayout layout = (WeberRelativeLayout) currentAlbumController;
                    if (layout.getFlag() == BrowserUtils.FLAG_HOME) {
                        initHomeGrid(layout, true);
                        setRefreshing(false);
                        return;
                    }
                    initBHList(layout, true);
                    setRefreshing(false);
                } else {
                    WeberToast.show(FloatingActivity.this, R.string.toast_refresh_failed);
                    setRefreshing(false);
                }
            }
        });
*/

    }

    private void setRefreshing(final boolean refreshing) {
        if (contentRefreshLayout != null) {
            contentRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    contentRefreshLayout.setRefreshing(refreshing);
                }
            });
        }
    }

    private void initMaterialDrawer(){
        PrimaryDrawerItem itemHome = new PrimaryDrawerItem()
                .withName(getResources().getString(R.string.album_title_home))
                .withIdentifier(1)
                .withTypeface(typeface)
                .withIcon(R.drawable.ic_d_home);
        PrimaryDrawerItem itemBookmarks = new PrimaryDrawerItem()
                .withName(getResources().getString(R.string.item_bookmarks))
                .withIdentifier(2)
                .withTypeface(typeface)
                .withIcon(R.drawable.ic_d_bookmark);
        PrimaryDrawerItem itemShare = new PrimaryDrawerItem()
                .withName(getResources().getString(R.string.item_share))
                .withIdentifier(3)
                .withTypeface(typeface)
                .withIcon(R.drawable.ic_d_share);
        PrimaryDrawerItem itemHistory = new PrimaryDrawerItem()
                .withName(getResources().getString(R.string.item_history))
                .withIdentifier(4)
                .withTypeface(typeface)
                .withIcon(R.drawable.ic_d_history);
        PrimaryDrawerItem itemSetting = new PrimaryDrawerItem()
                .withName(getResources().getString(R.string.item_setting))
                .withIdentifier(5)
                .withTypeface(typeface)
                .withIcon(R.drawable.ic_d_setting);
//        PrimaryDrawerItem itemScreenshot = new PrimaryDrawerItem()
//                .withName(getResources().getString(R.string.item_screenshot))
//                .withIcon(new IconicsDrawable(FloatingActivity.this).icon(GoogleMaterial.Icon.gmd_camera).color(Color.parseColor("#568203")));
        PrimaryDrawerItem itemDownloads = new PrimaryDrawerItem()
                .withName(getResources().getString(R.string.item_downloads))
                .withIdentifier(6)
                .withTypeface(typeface)
                .withIcon(R.drawable.ic_d_download);

        PrimaryDrawerItem itemPDF = new PrimaryDrawerItem()
                .withName(getResources().getString(R.string.item_PDF))
                .withIdentifier(7)
                .withTypeface(typeface)
                .withIcon(R.drawable.ic_d_pdf);
        PrimaryDrawerItem itemFind = new PrimaryDrawerItem()
                .withName(getResources().getString(R.string.item_Find))
                .withIdentifier(8)
                .withTypeface(typeface)
                .withIcon(R.drawable.ic_d_find);
        PrimaryDrawerItem itemAdd = new PrimaryDrawerItem()
                .withName(getResources().getString(R.string.item_add_to_home))
                .withIdentifier(9)
                .withTypeface(typeface)
                .withIcon(R.drawable.ic_d_add);

        drawerResult = new DrawerBuilder()
                .withActivity(this)
                .addDrawerItems(
                        new DividerDrawerItem().withSelectable(false),
                        itemHome,
                        new DividerDrawerItem().withSelectable(false),
                        itemShare,
                        new DividerDrawerItem().withSelectable(false),
                        itemAdd,
                        new DividerDrawerItem().withSelectable(false),
                        itemBookmarks,
                        new DividerDrawerItem().withSelectable(false),
                        itemHistory,
                        new DividerDrawerItem().withSelectable(false),
                        itemDownloads,
                        new DividerDrawerItem().withSelectable(false),
                        itemPDF,
                        new DividerDrawerItem().withSelectable(false),
                        itemFind,
                        new DividerDrawerItem().withSelectable(false),
                        itemSetting,
                        new DividerDrawerItem().withSelectable(false)
                )
                .withDrawerWidthRes(R.dimen.layout_width_258dp)
                .withDrawerGravity(Gravity.END)
                .withSelectedItem(-1)
                .withTranslucentStatusBar(false)
                .withTranslucentNavigationBar(false)
                .build();
        drawerResult.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                if (drawerItem.getIdentifier() == 9)
                {
                    if (currentAlbumController instanceof WeberWebView){
                        WeberWebView weberWebView = (WeberWebView) currentAlbumController;
                        RecordAction action = new RecordAction(FloatingActivity.this);
                        action.open(true);
                        if (action.checkGridItem(weberWebView.getUrl())) {
                            WeberToast.Companion.show(FloatingActivity.this, R.string.toast_already_exist_in_home);
                        } else {
                            String title = weberWebView.getTitle().trim();
                            String url = weberWebView.getUrl().trim();
                            Bitmap bitmap = ViewUtils.INSTANCE.capture(weberWebView, dimen156dp, dimen117dp, false, Bitmap.Config.ARGB_8888);
                            String filename = System.currentTimeMillis() + BrowserUtils.SUFFIX_PNG;
                            int ordinal = action.listGrid().size();
                            GridItem item = new GridItem(title, url, filename, ordinal);

                            if (BrowserUtils.bitmap2File(FloatingActivity.this, bitmap, filename) && action.addGridItem(item)) {
                                WeberToast.Companion.show(FloatingActivity.this, R.string.toast_add_to_home_successful);
                            } else {
                                WeberToast.Companion.show(FloatingActivity.this, R.string.toast_add_to_home_failed);
                            }
                        }
                        action.close();
                        drawerResult.closeDrawer();
                    }else {
                        drawerResult.closeDrawer();
                    }

                }else if (drawerItem.getIdentifier() == 8){
                    if (currentAlbumController instanceof WeberWebView){                        drawerResult.closeDrawer();
                        hideSoftInput(inputBox);
                        showSearchPanel();
                    }else {
                        drawerResult.closeDrawer();
                    }
                }else if (drawerItem.getIdentifier() == 7)
                {
                    //close drawer and save page
                    if (currentAlbumController instanceof WeberWebView) {
                        String dialogTitle = getString(R.string.dialog_save_title);
                        String btnText = getString(R.string.dialog_button_positive);

                        showSaveDialog(dialogTitle,btnText);

                        drawerResult.closeDrawer();

                    }else{
                        drawerResult.closeDrawer();
                    }
                }
                else if (drawerItem.getIdentifier() == 6)
                {
                    //close drawer and get downloads
                    Intent intent = new Intent(FloatingActivity.this, DownloadActivity.class);
                    startActivity(intent);
                    drawerResult.closeDrawer();
                }
                else if (drawerItem.getIdentifier() == 5)
                {
                    Intent intent = new Intent(FloatingActivity.this, SettingActivity.class);
                    startActivity(intent);
                    drawerResult.closeDrawer();
                }else if(drawerItem.getIdentifier() == 4)
                {
                    addAlbum(BrowserUtils.FLAG_HISTORY);
                    drawerResult.closeDrawer();
                }else if (drawerItem.getIdentifier() == 3)
                {
                    //share page
                    if (currentAlbumController instanceof WeberWebView)
                    {
                        String title = ((WeberWebView) currentAlbumController).getTitle();
                        String link = ((WeberWebView) currentAlbumController).getUrl();
                        String shared = getString(R.string.share_page_sent);
                        String intentText = getString(R.string.intent_choose);
                        ShareCompat.IntentBuilder
                                .from(FloatingActivity.this) // getActivity() or activity field if within Fragment
                                .setText(title + "\n" + link + "\n" + "\n" + shared)
                                .setType("text/plain") // most general text sharing MIME type
                                .setChooserTitle(intentText)
                                .startChooser();
                        drawerResult.closeDrawer();
                    }else
                    {
                        drawerResult.closeDrawer();
                    }
                }
                else if (drawerItem.getIdentifier() == 2)
                {
                    addAlbum(BrowserUtils.FLAG_BOOKMARKS);
                    drawerResult.closeDrawer();
                }else if (drawerItem.getIdentifier() == 1){
                    //go to home
                    //GeneralUtils.changeWiFiConnection(getApplicationContext(),false);

                    FragmentManager fm = getSupportFragmentManager();
                    DialogDownloadFragment dialogFragment = new DialogDownloadFragment ();
                    dialogFragment.setCancelable(true);
                    dialogFragment.show(fm, "Download");
                    drawerResult.closeDrawer();
                }
                return true;
            }
        });
    }

    private void initBubbleButton(){
        Intent serviceIntent = new Intent(FloatingActivity.this, BubbleButtonService.class);
        startService(serviceIntent);
    }

    private void showSaveDialog(String dialogTitle , String btnText) {
        final CharSequence[] items = { "PNG" , "PDF" };
        final String [] exportFormat = {"PIC"};
        final int[] itemNumber = {0};

        AlertDialog.Builder builder = new AlertDialog.Builder(FloatingActivity.this);
        builder.setTitle(dialogTitle);
        builder.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        itemNumber[0] = item;
                    }
                });

        builder.setPositiveButton(btnText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (itemNumber[0] == 0){
                    WeberWebView weberWebView = (WeberWebView) currentAlbumController;
                    new ScreenshotTask(FloatingActivity.this, weberWebView , exportFormat[0]).execute();
                }else {
                    exportFormat[0] = "PDF";
                    WeberWebView weberWebView = (WeberWebView) currentAlbumController;
                    new ScreenshotTask(FloatingActivity.this, weberWebView , exportFormat[0]).execute();
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void initSearchBox(WeberRelativeLayout layout)
    {
        searchEngineLogo = layout.findViewById(R.id.search_engine_logo);
        searchEngineHint = layout.findViewById(R.id.search_engine_box_hint);
        searchEngineHint.setTypeface(typeface);
        ViewGroup searchEngineBox = layout.findViewById(R.id.lut_search_engine_box);
        searchEngineBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEngineHint.setText("");
                inputBox.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(inputBox, InputMethodManager.SHOW_IMPLICIT);
            }
        });

    }

    private void initHomeGrid(final WeberRelativeLayout layout, boolean update) {
        if (update) {
            updateProgress(BrowserUtils.PROGRESS_MIN);
        }

        initSearchBox(layout);

        RecordAction action = new RecordAction(this);
        action.open(false);
        final List<GridItem> gridList = action.listGrid();
        action.close();

        DynamicGridView gridView = layout.findViewById(R.id.home_grid);
        TextView aboutBlank = layout.findViewById(R.id.home_about_blank);
        gridView.setEmptyView(aboutBlank);

        final GridAdapter gridAdapter;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridAdapter = new GridAdapter(this, gridList, 3);
        } else {
            gridAdapter = new GridAdapter(this, gridList, 2);
        }
        gridView.setAdapter(gridAdapter);
        gridAdapter.notifyDataSetChanged();

        /* Wait for gridAdapter.notifyDataSetChanged() */
        if (update) {
            gridView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    layout.setAlbumCover(ViewUtils.INSTANCE.capture(layout, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
                    updateProgress(BrowserUtils.PROGRESS_MAX);
                }
            }, shortAnimTime);
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateAlbum(gridList.get(position).getURL());
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showGridMenu(gridList.get(position));
                return true;
            }
        });
    }

    private void initBHList(final WeberRelativeLayout layout, boolean update) {
        if (update) {
            updateProgress(BrowserUtils.PROGRESS_MIN);
        }

        RecordAction action = new RecordAction(FloatingActivity.this);
        action.open(false);
        final List<Record> list;
        if (layout.getFlag() == BrowserUtils.FLAG_BOOKMARKS) {
            list = action.listBookmarks();
            Collections.sort(list, new Comparator<Record>() {
                @Override
                public int compare(Record first, Record second) {
                    return first.getTitle().compareTo(second.getTitle());
                }
            });
        } else if (layout.getFlag() == BrowserUtils.FLAG_HISTORY) {
            list = action.listHistory();
        } else {
            list = new ArrayList<>();
        }
        action.close();

        ListView listView = layout.findViewById(R.id.record_list);
        TextView textView = layout.findViewById(R.id.record_list_empty);
        listView.setEmptyView(textView);

        final RecordAdapter adapter = new RecordAdapter(FloatingActivity.this, R.layout.record_item, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        /* Wait for adapter.notifyDataSetChanged() */
        if (update) {
            listView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    layout.setAlbumCover(ViewUtils.INSTANCE.capture(layout, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
                    updateProgress(BrowserUtils.PROGRESS_MAX);
                }
            }, shortAnimTime);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateAlbum(list.get(position).getURL());
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showListMenu(adapter, list, position);
                return true;
            }
        });
    }

    private void initSearchPanel() {
        searchPanel = findViewById(R.id.main_search_panel);
        searchBox = findViewById(R.id.main_search_box);
        ImageButton searchUp = findViewById(R.id.main_search_up);
        ImageButton searchDown = findViewById(R.id.main_search_down);
        ImageButton searchCancel = findViewById(R.id.main_search_cancel);

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (currentAlbumController instanceof WeberWebView) {
                    ((WeberWebView) currentAlbumController).findAllAsync(s.toString());
                }
            }
        });

        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != EditorInfo.IME_ACTION_DONE) {
                    return false;
                }

                if (searchBox.getText().toString().isEmpty()) {
                    WeberToast.Companion.show(FloatingActivity.this, R.string.toast_input_empty);
                    return true;
                }
                return false;
            }
        });

        searchUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchBox.getText().toString();
                if (query.isEmpty()) {
                    WeberToast.Companion.show(FloatingActivity.this, R.string.toast_input_empty);
                    return;
                }

                hideSoftInput(searchBox);
                if (currentAlbumController instanceof WeberWebView) {
                    ((WeberWebView) currentAlbumController).findNext(false);
                }
            }
        });

        searchDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchBox.getText().toString();
                if (query.isEmpty()) {
                    WeberToast.Companion.show(FloatingActivity.this, R.string.toast_input_empty);
                    return;
                }

                hideSoftInput(searchBox);
                if (currentAlbumController instanceof WeberWebView) {
                    ((WeberWebView) currentAlbumController).findNext(true);
                }
            }
        });

        searchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSearchPanel();
            }
        });
    }

    private synchronized void addAlbum(int flag) {
        final AlbumController holder;
        if (flag == BrowserUtils.FLAG_BOOKMARKS) {
            WeberRelativeLayout layout = (WeberRelativeLayout) getLayoutInflater().inflate(R.layout.record_list, null, false);
            layout.setBrowserController(this);
            layout.setFlag(BrowserUtils.FLAG_BOOKMARKS);
            layout.setAlbumCover(ViewUtils.INSTANCE.capture(layout, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
            layout.setAlbumTitle(getString(R.string.album_title_bookmarks));
            holder = layout;
            initBHList(layout, false);
        } else if (flag == BrowserUtils.FLAG_HISTORY) {
            WeberRelativeLayout layout = (WeberRelativeLayout) getLayoutInflater().inflate(R.layout.record_list, null, false);
            layout.setBrowserController(this);
            layout.setFlag(BrowserUtils.FLAG_HISTORY);
            layout.setAlbumCover(ViewUtils.INSTANCE.capture(layout, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
            layout.setAlbumTitle(getString(R.string.album_title_history));
            holder = layout;
            initBHList(layout, false);
        } else if (flag == BrowserUtils.FLAG_HOME) {
            WeberRelativeLayout layout = (WeberRelativeLayout) getLayoutInflater().inflate(R.layout.home, null, false);
            layout.setBrowserController(this);
            layout.setFlag(BrowserUtils.FLAG_HOME);
            layout.setAlbumCover(ViewUtils.INSTANCE.capture(layout, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
            layout.setAlbumTitle(getString(R.string.album_title_home));
            holder = layout;
            initHomeGrid(layout, true);
        } else {
            return;
        }

        final View albumView = holder.getAlbumView();
        albumView.setVisibility(View.INVISIBLE);

        BrowserContainer.add(holder);
        switcherContainer.addView(albumView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.album_slide_in_up);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
                albumView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showAlbum(holder, false, true, true);
            }
        });
        albumView.startAnimation(animation);
    }

    private synchronized void addAlbum(String title, final String url, final boolean foreground, final Message resultMsg) {
        final WeberWebView webView = new WeberWebView(this);
        webView.setBrowserController(this);
        webView.setFlag(BrowserUtils.FLAG_WEBER);
        webView.setAlbumCover(ViewUtils.INSTANCE.capture(webView, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
        webView.setAlbumTitle(title);
        ViewUtils.INSTANCE.bound(this, webView);

        final View albumView = webView.getAlbumView();
        if ((currentAlbumController instanceof WeberWebView) && resultMsg != null) {
            int index = BrowserContainer.indexOf(currentAlbumController) + 1;
            BrowserContainer.add(webView, index);
            switcherContainer.addView(albumView, index, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        } else {
            BrowserContainer.add(webView);
            switcherContainer.addView(albumView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        if (!foreground) {
            ViewUtils.INSTANCE.bound(this, webView);
            webView.loadUrl(url);
            webView.deactivate();

            albumView.setVisibility(View.VISIBLE);
            if (currentAlbumController != null) {
                switcherScroller.smoothScrollTo(currentAlbumController.getAlbumView().getLeft(), 0);
            }
            return;
        }

        albumView.setVisibility(View.INVISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.album_slide_in_up);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
                albumView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showAlbum(webView, false, true, false);

                if (url != null && !url.isEmpty()) {
                    webView.loadUrl(url);
                } else if (resultMsg != null) {
                    WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                    transport.setWebView(webView);
                    resultMsg.sendToTarget();
                }
            }
        });
        albumView.startAnimation(animation);
    }

    private synchronized void pinAlbums(String url) {
        hideSoftInput(inputBox);
        hideSearchPanel();
        switcherContainer.removeAllViews();

        for (AlbumController controller : BrowserContainer.list()) {
            if (controller instanceof WeberWebView) {
                ((WeberWebView) controller).setBrowserController(this);
            } else if (controller instanceof WeberRelativeLayout) {
                ((WeberRelativeLayout) controller).setBrowserController(this);
            }
            switcherContainer.addView(controller.getAlbumView(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            controller.getAlbumView().setVisibility(View.VISIBLE);
            controller.deactivate();
        }

        if (BrowserContainer.size() < 1 && url == null) {
            addAlbum(BrowserUtils.FLAG_HOME);
        } else if (BrowserContainer.size() >= 1 && url == null) {
            if (currentAlbumController != null) {
                currentAlbumController.activate();
                return;
            }

            int index = BrowserContainer.size() - 1;
            currentAlbumController = BrowserContainer.get(index);
            contentFrame.removeAllViews();
            contentFrame.addView((View) currentAlbumController);
            currentAlbumController.activate();

            updateOmnibox();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    switcherScroller.smoothScrollTo(currentAlbumController.getAlbumView().getLeft(), 0);
                    currentAlbumController.setAlbumCover(ViewUtils.INSTANCE.capture(((View) currentAlbumController), dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
                }
            }, shortAnimTime);
        } else { // When url != null
            WeberWebView webView = new WeberWebView(this);
            webView.setBrowserController(this);
            webView.setFlag(BrowserUtils.FLAG_WEBER);
            webView.setAlbumCover(ViewUtils.INSTANCE.capture(webView, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
            webView.setAlbumTitle(getString(R.string.album_untitled));
            ViewUtils.INSTANCE.bound(this, webView);
            webView.loadUrl(url);

            BrowserContainer.add(webView);
            final View albumView = webView.getAlbumView();
            albumView.setVisibility(View.VISIBLE);
            switcherContainer.addView(albumView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            contentFrame.removeAllViews();
            contentFrame.addView(webView);

            if (currentAlbumController != null) {
                currentAlbumController.deactivate();
            }
            currentAlbumController = webView;
            currentAlbumController.activate();

            updateOmnibox();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    switcherScroller.smoothScrollTo(currentAlbumController.getAlbumView().getLeft(), 0);
                    currentAlbumController.setAlbumCover(ViewUtils.INSTANCE.capture(((View) currentAlbumController), dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
                }
            }, shortAnimTime);
        }
    }

    @Override
    public synchronized void showAlbum(AlbumController controller, boolean anim, final boolean expand, final boolean capture) {
        if (controller == null || controller == currentAlbumController) {
            switcherPanel.expanded();
            return;
        }

        if (currentAlbumController != null && anim) {
            currentAlbumController.deactivate();
            final View rv = (View) currentAlbumController;
            final View av = (View) controller;

            Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.album_fade_out);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationRepeat(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {}

                @Override
                public void onAnimationStart(Animation animation) {
                    contentFrame.removeAllViews();
                    contentFrame.addView(av);
                }
            });
            rv.startAnimation(fadeOut);
        } else {
            if (currentAlbumController != null) {
                currentAlbumController.deactivate();
            }
            contentFrame.removeAllViews();
            contentFrame.addView((View) controller);
        }

        currentAlbumController = controller;
        currentAlbumController.activate();
        switcherScroller.smoothScrollTo(currentAlbumController.getAlbumView().getLeft(), 0);
        updateOmnibox();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (expand) {
                    switcherPanel.expanded();
                }

                if (capture) {
                    currentAlbumController.setAlbumCover(ViewUtils.INSTANCE.capture(((View) currentAlbumController), dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
                }
            }
        }, shortAnimTime);
    }

    private synchronized void updateAlbum() {
        if (currentAlbumController == null) {
            return;
        }

        WeberRelativeLayout layout = (WeberRelativeLayout) getLayoutInflater().inflate(R.layout.home, null, false);
        layout.setBrowserController(this);
        layout.setFlag(BrowserUtils.FLAG_HOME);
        layout.setAlbumCover(ViewUtils.INSTANCE.capture(layout, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
        layout.setAlbumTitle(getString(R.string.album_title_home));
        initHomeGrid(layout, true);

        int index = switcherContainer.indexOfChild(currentAlbumController.getAlbumView());
        currentAlbumController.deactivate();
        switcherContainer.removeView(currentAlbumController.getAlbumView());
        contentFrame.removeAllViews(); ///

        switcherContainer.addView(layout.getAlbumView(), index, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        contentFrame.addView(layout);
        BrowserContainer.set(layout, index);
        currentAlbumController = layout;
        updateOmnibox();
    }

    private synchronized void updateAlbum(String url) {
        if (currentAlbumController == null) {
            return;
        }

        if (currentAlbumController instanceof WeberWebView) {
            ((WeberWebView) currentAlbumController).loadUrl(url);
            updateOmnibox();
        } else if (currentAlbumController instanceof WeberRelativeLayout) {
            WeberWebView webView = new WeberWebView(this);
            webView.setBrowserController(this);
            webView.setFlag(BrowserUtils.FLAG_WEBER);
            webView.setAlbumCover(ViewUtils.INSTANCE.capture(webView, dimen144dp, dimen108dp, false, Bitmap.Config.RGB_565));
            webView.setAlbumTitle(getString(R.string.album_untitled));
            ViewUtils.INSTANCE.bound(this, webView);

            int index = switcherContainer.indexOfChild(currentAlbumController.getAlbumView());
            currentAlbumController.deactivate();
            switcherContainer.removeView(currentAlbumController.getAlbumView());
            contentFrame.removeAllViews(); ///

            switcherContainer.addView(webView.getAlbumView(), index, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            contentFrame.addView(webView);
            BrowserContainer.set(webView, index);
            currentAlbumController = webView;
            webView.activate();

            webView.loadUrl(url);
            updateOmnibox();
        } else {
            WeberToast.Companion.show(this, R.string.toast_load_error);
        }
    }

    @Override
    public synchronized void removeAlbum(AlbumController controller) {
        if (currentAlbumController == null || BrowserContainer.size() <= 1) {
            switcherContainer.removeView(controller.getAlbumView());
            BrowserContainer.remove(controller);
            addAlbum(BrowserUtils.FLAG_HOME);
            return;
        }

        if (controller != currentAlbumController) {
            switcherContainer.removeView(controller.getAlbumView());
            BrowserContainer.remove(controller);
        } else {
            switcherContainer.removeView(controller.getAlbumView());
            int index = BrowserContainer.indexOf(controller);
            BrowserContainer.remove(controller);
            if (index >= BrowserContainer.size()) {
                index = BrowserContainer.size() - 1;
            }
            showAlbum(BrowserContainer.get(index), false, false, false);
        }
    }

    @Override
    public void updateAutoComplete() {
        RecordAction action = new RecordAction(this);
        action.open(false);
        List<Record> list = action.listBookmarks();
        list.addAll(action.listHistory());
        action.close();

        final CompleteAdapter adapter = new CompleteAdapter(this, R.layout.complete_item, list);
        inputBox.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            inputBox.setDropDownVerticalOffset(getResources().getDimensionPixelOffset(R.dimen.layout_height_6dp));
        }
        inputBox.setDropDownWidth(ViewUtils.INSTANCE.getWindowWidth(this));
        inputBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = ((TextView) view.findViewById(R.id.complete_item_url)).getText().toString();
                inputBox.setText(Html.fromHtml(BrowserUtils.urlWrapper(url)), EditText.BufferType.SPANNABLE);
                inputBox.setSelection(url.length());
                updateAlbum(url);
                hideSoftInput(inputBox);
            }
        });
    }

    @Override
    public void updateBookmarks() {
        if (!(currentAlbumController instanceof WeberWebView)) {
            omniboxBookmark.setImageDrawable(ViewUtils.INSTANCE.getDrawable(this, R.drawable.bookmark_selector_dark));
            return;
        }

        RecordAction action = new RecordAction(this);
        action.open(false);
        String url = ((WeberWebView) currentAlbumController).getUrl();
        if (action.checkBookmark(url)) {
            omniboxBookmark.setImageDrawable(ViewUtils.INSTANCE.getDrawable(this, R.drawable.bookmark_selector_blue));
        } else {
            omniboxBookmark.setImageDrawable(ViewUtils.INSTANCE.getDrawable(this, R.drawable.bookmark_selector_dark));
        }
        action.close();
    }

    @Override
    public void updateInputBox(String query) {
        if (query != null) {
            inputBox.setText(Html.fromHtml(BrowserUtils.urlWrapper(query)), EditText.BufferType.SPANNABLE);
        } else {
            inputBox.setText(null);
        }
        inputBox.clearFocus();
    }

    private void updateOmnibox() {
        if (currentAlbumController == null) {
            return;
        }

        if (currentAlbumController instanceof WeberRelativeLayout) {
            updateProgress(BrowserUtils.PROGRESS_MAX);
            updateBookmarks();
            updateInputBox(null);
        } else if (currentAlbumController instanceof WeberWebView) {
            WeberWebView weberWebView = (WeberWebView) currentAlbumController;
            updateProgress(weberWebView.getProgress());
            updateBookmarks();
            if (weberWebView.getUrl() == null && weberWebView.getOriginalUrl() == null) {
                updateInputBox(null);
            } else if (weberWebView.getUrl() != null) {
                updateInputBox(weberWebView.getUrl());
            } else {
                updateInputBox(weberWebView.getOriginalUrl());
            }
        }
    }

    @Override
    public synchronized void updateProgress(int progress) {
        if (progress > progressBar.getProgress()) {
            ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", progress);
            animator.setDuration(shortAnimTime);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.start();
        } else if (progress < progressBar.getProgress()) {
            ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", 0, progress);
            animator.setDuration(shortAnimTime);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.start();
        }

        updateBookmarks();
        if (progress < BrowserUtils.PROGRESS_MAX) {
            updateRefresh(true);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            updateRefresh(false);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void updateRefresh(boolean running) {
        if (running) {
            omniboxRefresh.setImageDrawable(ViewUtils.INSTANCE.getDrawable(this, R.drawable.cl_selector_dark));
        } else {
            omniboxRefresh.setImageDrawable(ViewUtils.INSTANCE.getDrawable(this, R.drawable.refresh_selector));
        }
    }

    @Override
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        // Because Activity launchMode is singleInstance,
        // so we can not get result from onActivityResult when Android 4.X,
        // what a pity
        //
        // this.uploadMsg = uploadMsg;
        // Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // intent.addCategory(Intent.CATEGORY_OPENABLE);
        // intent.setType("*/*");
        // startActivityForResult(Intent.createChooser(intent, getString(R.string.main_file_chooser)), IntentUtils.REQUEST_FILE_16);
        uploadMsg.onReceiveValue(null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        FrameLayout layout = (FrameLayout) getLayoutInflater().inflate(R.layout.dialog_desc, null, false);
        TextView textView = layout.findViewById(R.id.dialog_desc);
        textView.setText(R.string.dialog_content_upload);

        builder.setView(layout);
        builder.create().show();
    }

    @Override
    public void showFileChooser(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.filePathCallback = filePathCallback;

            try {
                Intent intent = fileChooserParams.createIntent();
                startActivityForResult(intent, IntentUtils.REQUEST_FILE_21);
            } catch (Exception e) {
                WeberToast.Companion.show(this, R.string.toast_open_file_manager_failed);
            }
        }
    }

    @Override
    public void onCreateView(WebView view, final Message resultMsg) {
        if (resultMsg == null) {
            return;
        }
        switcherPanel.collapsed();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addAlbum(getString(R.string.album_untitled), null, true, resultMsg);
            }
        }, shortAnimTime);
    }

    @Override
    public boolean onShowCustomView(View view, int requestedOrientation, WebChromeClient.CustomViewCallback callback) {
        return onShowCustomView(view, callback);
    }

    @Override
    public boolean onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        if (view == null) {
            return false;
        }
        if (customView != null && callback != null) {
            callback.onCustomViewHidden();
            return false;
        }

        customView = view;
        originalOrientation = getRequestedOrientation();

        fullscreenHolder = new FullscreenHolder(this);
        fullscreenHolder.addView(
                customView,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                ));

        FrameLayout decorView = (FrameLayout) getWindow().getDecorView();
        decorView.addView(
                fullscreenHolder,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                ));

        customView.setKeepScreenOn(true);
        ((View) currentAlbumController).setVisibility(View.GONE);
        setCustomFullscreen(true);

        if (view instanceof FrameLayout) {
            if (((FrameLayout) view).getFocusedChild() instanceof VideoView) {
                videoView = (VideoView) ((FrameLayout) view).getFocusedChild();
                videoView.setOnErrorListener(new VideoCompletionListener());
                videoView.setOnCompletionListener(new VideoCompletionListener());
            }
        }
        customViewCallback = callback;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Auto landscape when video shows

        return true;
    }

    @Override
    public boolean onHideCustomView() {
        if (customView == null || customViewCallback == null || currentAlbumController == null) {
            return false;
        }

        FrameLayout decorView = (FrameLayout) getWindow().getDecorView();
        if (decorView != null) {
            decorView.removeView(fullscreenHolder);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            try {
                customViewCallback.onCustomViewHidden();
            } catch (Throwable ignored) {}
        }

        customView.setKeepScreenOn(false);
        ((View) currentAlbumController).setVisibility(View.VISIBLE);
        setCustomFullscreen(false);

        fullscreenHolder = null;
        customView = null;
        if (videoView != null) {
            videoView.setOnErrorListener(null);
            videoView.setOnCompletionListener(null);
            videoView = null;
        }
        setRequestedOrientation(originalOrientation);

        return true;
    }

    @Override
    public void onLongPress(String url) {
        WebView.HitTestResult result;
        if (!(currentAlbumController instanceof WeberWebView)) {
            return;
        }
        result = ((WeberWebView) currentAlbumController).getHitTestResult();

        final List<String> list = new ArrayList<>();
        list.add(getString(R.string.main_menu_new_tab));
        list.add(getString(R.string.main_menu_copy_link));
        if (result != null && (result.getType() == WebView.HitTestResult.IMAGE_TYPE || result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE)) {
            list.add(getString(R.string.main_menu_save));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        FrameLayout layout = (FrameLayout) getLayoutInflater().inflate(R.layout.dialog_list, null, false);
        builder.setView(layout);

        ListView listView = layout.findViewById(R.id.dialog_list);
        DialogAdapter adapter = new DialogAdapter(this, R.layout.dialog_text_item, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        final AlertDialog dialog = builder.create();
        if (url != null || (result != null && result.getExtra() != null)) {
            if (url == null) {
                url = result.getExtra();
            }
            dialog.show();
        }

        final String target = url;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = list.get(position);
                if (s.equals(getString(R.string.main_menu_new_tab))) { // New tab
                    addAlbum(getString(R.string.album_untitled), target, false, null);
                    WeberToast.Companion.show(FloatingActivity.this, R.string.toast_new_tab_successful);
                } else if (s.equals(getString(R.string.main_menu_copy_link))) { // Copy link
                    BrowserUtils.copyURL(FloatingActivity.this, target);
                } else if (s.equals(getString(R.string.main_menu_save))) { // Save
                    BrowserUtils.download(FloatingActivity.this, target, target, BrowserUtils.MIME_TYPE_IMAGE);
                }
                dialog.hide();
                dialog.dismiss();
            }
        });
    }

    private boolean onKeyCodeVolumeUp() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int vc = Integer.valueOf(sp.getString(getString(R.string.sp_volume), "1"));

        if (vc == 0) { // Switch tabs
            if (switcherPanel.isKeyBoardShowing()) {
                return true;
            }

            AlbumController controller = nextAlbumController(false);
            showAlbum(controller, false, false, true);
            WeberToast.Companion.show(this, controller.getAlbumTitle());

            return true;
        } else if (vc == 1 && currentAlbumController instanceof WeberWebView) { // Scroll webpage
            WeberWebView weberWebView = (WeberWebView) currentAlbumController;
            int height = weberWebView.getMeasuredHeight();
            int scrollY = weberWebView.getScrollY();
            int distance = Math.min(height, scrollY);

            ObjectAnimator anim = ObjectAnimator.ofInt(weberWebView, "scrollY", scrollY, scrollY - distance);
            anim.setDuration(mediumAnimTime);
            anim.start();

            return true;
        }

        return false;
    }

    private boolean onKeyCodeVolumeDown() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int vc = Integer.valueOf(sp.getString(getString(R.string.sp_volume), "1"));

        if (vc == 0) { // Switch tabs
            if (switcherPanel.isKeyBoardShowing()) {
                return true;
            }

            AlbumController controller = nextAlbumController(true);
            showAlbum(controller, false, false, true);
            WeberToast.Companion.show(this, controller.getAlbumTitle());

            return true;
        } else if (vc == 1 && currentAlbumController instanceof WeberWebView) {
            WeberWebView weberWebView = (WeberWebView) currentAlbumController;
            int height = weberWebView.getMeasuredHeight();
            int scrollY = weberWebView.getScrollY();
            int surplus = (int) (weberWebView.getContentHeight() * ViewUtils.INSTANCE.getDensity(this) - height - scrollY);
            int distance = Math.min(height, surplus);

            ObjectAnimator anim = ObjectAnimator.ofInt(weberWebView, "scrollY", scrollY, scrollY + distance);
            anim.setDuration(mediumAnimTime);
            anim.start();

            return true;
        }

        return false;
    }

    private boolean onKeyCodeBack(boolean douQ) {
        hideSoftInput(inputBox);
        if (switcherPanel.getStatus() != SwitcherPanel.Status.EXPANDED) {
            switcherPanel.expanded();
        } else if (currentAlbumController == null) {
            finish();
        } else if (currentAlbumController instanceof WeberWebView) {
            WeberWebView weberWebView = (WeberWebView) currentAlbumController;
            if (weberWebView.canGoBack()) {
                weberWebView.goBack();
            } else {
                updateAlbum();
            }
        } else if (currentAlbumController instanceof WeberRelativeLayout) {
            switch (currentAlbumController.getFlag()) {
                case BrowserUtils.FLAG_BOOKMARKS:
                    updateAlbum();
                    break;
                case BrowserUtils.FLAG_HISTORY:
                    updateAlbum();
                    break;
                case BrowserUtils.FLAG_HOME:
                    if (douQ) {
                        doubleTapsQuit();
                    }
                    break;
                default:
                    finish();
                    break;
            }
        } else {
            finish();
        }

        return true;
    }

    private void doubleTapsQuit() {
        final Timer timer = new Timer();
        if (!quit) {
            quit = true;
            WeberToast.Companion.show(this, R.string.toast_double_taps_quit);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    quit = false;
                    timer.cancel();
                }
            }, DOUBLE_TAPS_QUIT_DEFAULT);
        } else {
            timer.cancel();
            finish();
        }
    }

    private void hideSoftInput(View view) {
        view.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showSoftInput(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void hideSearchPanel() {
        hideSoftInput(searchBox);
        searchBox.setText("");
        searchPanel.setVisibility(View.GONE);
        omnibox.setVisibility(View.VISIBLE);
    }

    private void showSearchPanel() {
        omnibox.setVisibility(View.GONE);
        searchPanel.setVisibility(View.VISIBLE);
        showSoftInput(searchBox);
    }

    private boolean showDrawer(){
//        drawerResult.openDrawer();
        return true;
    }

    private void showGridMenu(final GridItem gridItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        FrameLayout layout = (FrameLayout) getLayoutInflater().inflate(R.layout.dialog_list, null, false);
        builder.setView(layout);

        final String[] array = getResources().getStringArray(R.array.list_menu);
        final List<String> stringList = new ArrayList<>(Arrays.asList(array));
        stringList.remove(array[1]); // Copy link
        stringList.remove(array[2]); // Share

        ListView listView = layout.findViewById(R.id.dialog_list);
        DialogAdapter dialogAdapter = new DialogAdapter(this, R.layout.dialog_text_item, stringList);
        listView.setAdapter(dialogAdapter);
        dialogAdapter.notifyDataSetChanged();

        final AlertDialog dialog = builder.create();
        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = stringList.get(position);
                if (s.equals(array[0])) { // New tab
                    addAlbum(getString(R.string.album_untitled), gridItem.getURL(), false, null);
                    WeberToast.Companion.show(FloatingActivity.this, R.string.toast_new_tab_successful);
                } else if (s.equals(array[3])) { // Edit
                    showEditDialog(gridItem);
                } else if (s.equals(array[4])) { // Delete
                    RecordAction action = new RecordAction(FloatingActivity.this);
                    action.open(true);
                    action.deleteGridItem(gridItem);
                    action.close();
                    FloatingActivity.this.deleteFile(gridItem.getFilename());

                    initHomeGrid((WeberRelativeLayout) currentAlbumController, true);
                    WeberToast.Companion.show(FloatingActivity.this, R.string.toast_delete_successful);
                }

                dialog.hide();
                dialog.dismiss();
            }
        });
    }

    private void showListMenu(final RecordAdapter recordAdapter, final List<Record> recordList, final int location) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        FrameLayout layout = (FrameLayout) getLayoutInflater().inflate(R.layout.dialog_list, null, false);
        builder.setView(layout);

        final String[] array = getResources().getStringArray(R.array.list_menu);
        final List<String> stringList = new ArrayList<>(Arrays.asList(array));
        if (currentAlbumController.getFlag() != BrowserUtils.FLAG_BOOKMARKS) {
            stringList.remove(array[3]);
        }

        ListView listView = layout.findViewById(R.id.dialog_list);
        DialogAdapter dialogAdapter = new DialogAdapter(this, R.layout.dialog_text_item, stringList);
        listView.setAdapter(dialogAdapter);
        dialogAdapter.notifyDataSetChanged();

        final AlertDialog dialog = builder.create();
        dialog.show();

        final Record record = recordList.get(location);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = stringList.get(position);
                if (s.equals(array[0])) { // New tab
                    addAlbum(getString(R.string.album_untitled), record.getURL(), false, null);
                    WeberToast.Companion.show(FloatingActivity.this, R.string.toast_new_tab_successful);
                } else if (s.equals(array[1])) { // Copy link
                    BrowserUtils.copyURL(FloatingActivity.this, record.getURL());
                } else if (s.equals(array[2])) { // Share
                    IntentUtils.share(FloatingActivity.this, record.getTitle(), record.getURL());
                } else if (s.equals(array[3])) { // Edit
                    showEditDialog(recordAdapter, recordList, location);
                } else if (s.equals(array[4])) { // Delete
                    RecordAction action = new RecordAction(FloatingActivity.this);
                    action.open(true);
                    if (currentAlbumController.getFlag() == BrowserUtils.FLAG_BOOKMARKS) {
                        action.deleteBookmark(record);
                    } else if (currentAlbumController.getFlag() == BrowserUtils.FLAG_HISTORY) {
                        action.deleteHistory(record);
                    }
                    action.close();

                    recordList.remove(location);
                    recordAdapter.notifyDataSetChanged();

                    updateBookmarks();
                    updateAutoComplete();

                    WeberToast.Companion.show(FloatingActivity.this, R.string.toast_delete_successful);
                }

                dialog.hide();
                dialog.dismiss();
            }
        });
    }

    private void showEditDialog(final GridItem gridItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        FrameLayout layout = (FrameLayout) getLayoutInflater().inflate(R.layout.dialog_edit, null, false);
        builder.setView(layout);

        final AlertDialog dialog = builder.create();
        dialog.show();

        final EditText editText = layout.findViewById(R.id.dialog_edit);
        editText.setHint(R.string.dialog_title_hint);
        editText.setText(gridItem.getTitle());
        editText.setSelection(gridItem.getTitle().length());
        hideSoftInput(inputBox);
        showSoftInput(editText);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != EditorInfo.IME_ACTION_DONE) {
                    return false;
                }

                String text = editText.getText().toString().trim();
                if (text.isEmpty()) {
                    WeberToast.Companion.show(FloatingActivity.this, R.string.toast_input_empty);
                    return true;
                }

                RecordAction action = new RecordAction(FloatingActivity.this);
                action.open(true);
                gridItem.setTitle(text);
                action.updateGridItem(gridItem);
                action.close();

                hideSoftInput(editText);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.hide();
                        dialog.dismiss();
                    }
                }, longAnimTime);
                return false;
            }
        });
    }

    private void showEditDialog(final RecordAdapter recordAdapter, List<Record> recordList, int location) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        FrameLayout layout = (FrameLayout) getLayoutInflater().inflate(R.layout.dialog_edit, null, false);
        builder.setView(layout);

        final AlertDialog dialog = builder.create();
        dialog.show();

        final Record record = recordList.get(location);
        final EditText editText = layout.findViewById(R.id.dialog_edit);
        editText.setHint(R.string.dialog_title_hint);
        editText.setText(record.getTitle());
        editText.setSelection(record.getTitle().length());
        hideSoftInput(inputBox);
        showSoftInput(editText);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId != EditorInfo.IME_ACTION_DONE) {
                    return false;
                }

                String text = editText.getText().toString().trim();
                if (text.isEmpty()) {
                    WeberToast.Companion.show(FloatingActivity.this, R.string.toast_input_empty);
                    return true;
                }

                RecordAction action = new RecordAction(FloatingActivity.this);
                action.open(true);
                record.setTitle(text);
                action.updateBookmark(record);
                action.close();

                recordAdapter.notifyDataSetChanged();
                hideSoftInput(editText);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.hide();
                        dialog.dismiss();
                    }
                }, longAnimTime);
                return false;
            }
        });
    }

    private boolean prepareRecord() {
        if (!(currentAlbumController instanceof WeberWebView)) {
            return false;
        }

        WeberWebView webView = (WeberWebView) currentAlbumController;
        String title = webView.getTitle();
        String url = webView.getUrl();
        return title != null
                && !title.isEmpty()
                && url != null
                && !url.isEmpty()
                && !url.startsWith(BrowserUtils.URL_SCHEME_ABOUT)
                && !url.startsWith(BrowserUtils.URL_SCHEME_MAIL_TO)
                && !url.startsWith(BrowserUtils.URL_SCHEME_INTENT);
    }

    private void setCustomFullscreen(boolean fullscreen) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        /*
         * Can not use View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION,
         * so we can not hide NavigationBar :(
         */
        int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;

        if (fullscreen) {
            layoutParams.flags |= bits;
        } else {
            layoutParams.flags &= ~bits;
            if (customView != null) {
                customView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            } else {
                contentFrame.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
        getWindow().setAttributes(layoutParams);
    }

    private AlbumController nextAlbumController(boolean next) {
        if (BrowserContainer.size() <= 1) {
            return currentAlbumController;
        }

        List<AlbumController> list = BrowserContainer.list();
        int index = list.indexOf(currentAlbumController);
        if (next) {
            index++;
            if (index >= list.size()) {
                index = 0;
            }
        } else {
            index--;
            if (index < 0) {
                index = list.size() - 1;
            }
        }

        return list.get(index);
    }

}

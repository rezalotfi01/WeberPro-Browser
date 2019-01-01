package com.github.rezalotfi01.weberpro.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.github.rezalotfi01.weberpro.BaseClasses.WeberActivity;
import com.github.rezalotfi01.weberpro.Browser.AdBlock;
import com.github.rezalotfi01.weberpro.Database.RecordAction;
import com.github.rezalotfi01.weberpro.R;
import com.github.rezalotfi01.weberpro.Utils.BrowserUtils;
import com.github.rezalotfi01.weberpro.View.WeberToast;
import com.github.rezalotfi01.weberpro.View.WhitelistAdapter;

import java.util.List;

public class WhitelistActivity extends WeberActivity {
    private WhitelistAdapter adapter;
    private List<String> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whitelist);

        ActionBar settingAction = getSupportActionBar();

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.toolbar_app, null);
//        settingAction.setCustomView(mCustomView);
        settingAction.setDisplayShowCustomEnabled(true);
        settingAction.setDisplayHomeAsUpEnabled(true);

        RecordAction action = new RecordAction(this);
        action.open(false);
        list = action.listDomains();
        action.close();

        ListView listView = findViewById(R.id.whitelist);
        listView.setEmptyView(findViewById(R.id.whitelist_empty));

        adapter = new WhitelistAdapter(this, R.layout.whitelist_item, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        final EditText editText = findViewById(R.id.whilelist_edit);
        showSoftInput(editText);

        Button button = findViewById(R.id.whilelist_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String domain = editText.getText().toString().trim();
                if (domain.isEmpty()) {
                    WeberToast.Companion.show(WhitelistActivity.this, R.string.toast_input_empty);
                } else if (!BrowserUtils.isURL(domain)) {
                    WeberToast.Companion.show(WhitelistActivity.this, R.string.toast_invalid_domain);
                } else {
                    RecordAction action = new RecordAction(WhitelistActivity.this);
                    action.open(true);
                    if (action.checkDomain(domain)) {
                        WeberToast.Companion.show(WhitelistActivity.this, R.string.toast_domain_already_exists);
                    } else {
                        AdBlock adBlock = new AdBlock(WhitelistActivity.this);
                        adBlock.addDomain(domain.trim());
                        list.add(0, domain.trim());
                        adapter.notifyDataSetChanged();
                        WeberToast.Companion.show(WhitelistActivity.this, R.string.toast_add_whitelist_successful);
                    }
                    action.close();
                }
            }
        });
    }


    @Override
    public void onPause() {
        hideSoftInput(findViewById(R.id.whilelist_edit));
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.whilelist_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.whitelist_menu_clear:
                AdBlock adBlock = new AdBlock(this);
                adBlock.clearDomains();
                list.clear();
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
        return true;
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
}

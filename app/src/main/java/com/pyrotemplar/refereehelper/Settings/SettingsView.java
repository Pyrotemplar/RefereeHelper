package com.pyrotemplar.refereehelper.Settings;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pyrotemplar.refereehelper.DialogFragments.ConfirmationDialogFragment;
import com.pyrotemplar.refereehelper.R;

import butterknife.OnClick;

/**
 * Created by Manuel Montes de Oca on 5/4/2017.
 */

public class SettingsView extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, SettingsContract.View {


    SharedPreferences sharedPreferences;
    private SettingsContract.Presenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.app_preference);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        //rootView.setBackgroundResource(R.drawable.main_background);

        new SettingsPresenter(this);

        return rootView;
    }

    @Override
    public void setPresenter(SettingsPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        onSharedPreferenceChanged(sharedPreferences, getString(R.string.setting_share_preference_key));

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getKey().equals(getResources().getString(R.string.SP_RESET_CLICKER_DATA_SETTINGS_KEY)))
            resetClicker();
        if (preference.getKey().equals(getResources().getString(R.string.SP_RATE_APP_KEY)))
            rateApp();
        return super.onPreferenceTreeClick(preference);
    }

    private void rateApp() {

        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), " unable to find market app", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }

    private void resetClicker() {
        ConfirmationDialogFragment confirmationDialogFragment = new ConfirmationDialogFragment();
        confirmationDialogFragment.show(getFragmentManager(), "TAG");
    }

    @Override
    public void onResume() {
        super.onResume();
        //unregister the preferenceChange listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        //unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

}
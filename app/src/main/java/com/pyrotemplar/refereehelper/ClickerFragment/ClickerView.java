package com.pyrotemplar.refereehelper.ClickerFragment;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pyrotemplar.refereehelper.DialogFragments.GameClockDialogFragment;
import com.pyrotemplar.refereehelper.R;
import com.pyrotemplar.refereehelper.DialogFragments.NameAndColorPickerDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;


/**
 * Created by Manuel Montes de Oca on 4/21/2017.
 */

public class ClickerView extends Fragment implements ClickerContract.View {

    public final static String SCORE_BOARD_BUTTONS_PRESSED = "scoreboardButtonPressed";
    public final static String GAME_CLOCK_BUTTONS_PRESSED = "gameClockButtonPressed";

    @BindView(R.id.awayTeamNameTextView)
    TextView awayTeamNameTextView;
    @BindView(R.id.homeTeamNameTextView)
    TextView homeTeamNameTextView;

/*    @BindView(R.id.homeTeamBannerLayout)
    LinearLayout homeTeamBannerLayout;*/

    @BindView(R.id.awayTeamScoreTextView)
    TextView awayTeamScoreTextView;
    @BindView(R.id.homeTeamScoreTextView)
    TextView homeTeamScoreTextView;
    @BindView(R.id.inningCountTextView)
    TextView inningCountTextView;
    @BindView(R.id.ballCountTextView)
    TextView ballCountTextView;
    @BindView(R.id.strikeCountTextView)
    TextView strikeCountTextView;
    @BindView(R.id.foulCountTextView)
    TextView foulCountTextView;
    @BindView(R.id.outCountTextView)
    TextView outCountTextView;
    @BindView(R.id.gameClockTextView)
    TextView gameClockTextView;
    @BindView(R.id.inningArrowImageView)
    ImageView inningArrowImageView;
    @BindView(R.id.awayArrowImageView)
    ImageView awayArrowImageView;
    @BindView(R.id.homeArrowImageView)
    ImageView homeArrowImageView;
    @BindView(R.id.undoLayout)
    LinearLayout undoLayout;
    @BindView(R.id.redoLayout)
    LinearLayout redoLayout;

    private boolean isHapticFeedbackEnabled;
    private SharedPreferences sharedPreferences;
    private NameAndColorPickerDialogFragment nameAndColorPickerDialogFragment;
    private GameClockDialogFragment gameClockDialogFragment;
    private Bundle mArgs;

    private static ClickerContract.Presenter mPresenter;
    private boolean isViewShown;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.clicker_layout, null);
        ButterKnife.bind(this, rootView);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        nameAndColorPickerDialogFragment = new NameAndColorPickerDialogFragment();
        nameAndColorPickerDialogFragment.setTargetFragment(this, 1);

        gameClockDialogFragment = new GameClockDialogFragment();
        gameClockDialogFragment.setTargetFragment(this, 2);

        mArgs = new Bundle();


        new ClickerPresenter(this);
        if (!isViewShown) {
            //  updateSharePreferences() contains logic to update Share preference when page is selected
            updateSharePreferences();
        }

        // set selected textview to true to enable marquee scrolling feature
        awayTeamNameTextView.setSelected(true);
        homeTeamNameTextView.setSelected(true);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //todo: update the teams name and color from intent.


            if (data.getStringExtra("caller") == "awayTeamButton")
                mPresenter.updateAwayTeamBanner(data.getStringExtra("teamName"), data.getIntExtra("teamColor", 0));
            else if (data.getStringExtra("caller") == "homeTeamButton")
                mPresenter.updateHomeTeamBanner(data.getStringExtra("teamName"), data.getIntExtra("teamColor", 0));
            else if (data.getStringExtra("caller") == "gameClockButton") {
                mPresenter.setGameClockString(Integer.parseInt(data.getStringExtra("newTime")));
                mPresenter.startStopGameClock(true);
            }
        }

    }

    @Override
    public void setPresenter(ClickerPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @OnLongClick(R.id.awayTeamBannerLayout)
    @Override
    public boolean AwayTeamButtonLongClicked(View view) {
        mArgs.putString(SCORE_BOARD_BUTTONS_PRESSED, "awayTeamButton");
        mArgs.putString("teamName", awayTeamNameTextView.getText().toString());
        nameAndColorPickerDialogFragment.setArguments(mArgs);
        nameAndColorPickerDialogFragment.show(getFragmentManager(), "TAG");
        if (isHapticFeedbackEnabled)
            hapticFeedback(view);
        return true;
    }

    @OnLongClick(R.id.homeTeamBannerLayout)
    @Override
    public boolean homeTeamButtonLongClicked(View view) {

        mArgs.putString(SCORE_BOARD_BUTTONS_PRESSED, "homeTeamButton");
        mArgs.putString("teamName", homeTeamNameTextView.getText().toString());
        nameAndColorPickerDialogFragment.setArguments(mArgs);
        nameAndColorPickerDialogFragment.show(getFragmentManager(), "TAG");
        if (isHapticFeedbackEnabled)
            hapticFeedback(view);
        return true;
    }

    @OnClick({R.id.awayTeamBannerLayout, R.id.homeTeamBannerLayout, R.id.runnerScoredButton})
    @Override()
    public void incrementRunButtonClicked(View view) {

        //mPresenter.incrementRun(view.getId());
        if (mPresenter.incrementRun(view.getId()) && isHapticFeedbackEnabled)
            hapticFeedback(view);
    }

    @OnClick(R.id.ballLayout)
    @Override
    public void ballButtonClicked(View view) {

        mPresenter.incrementBall();
        if (isHapticFeedbackEnabled)
            hapticFeedback(view);

    }

    @OnClick(R.id.strikeLayout)
    @Override
    public void strikeButtonClicked(View view) {
        mPresenter.incrementStrike();
        if (isHapticFeedbackEnabled)
            hapticFeedback(view);
    }

    @OnClick(R.id.foulLayout)
    @Override
    public void foulButtonClicked(View view) {
        mPresenter.incrementFoul();
        if (isHapticFeedbackEnabled)
            hapticFeedback(view);
    }

    @OnClick(R.id.outLayout)
    @Override
    public void outButtonClicked(View view) {
        mPresenter.incrementOut();
        if (isHapticFeedbackEnabled)
            hapticFeedback(view);
    }

    @OnClick(R.id.undoLayout)
    @Override
    public void undoButtonClicked(View view) {
        mPresenter.undo();
        if (isHapticFeedbackEnabled)
            hapticFeedback(view);
    }

    @OnClick(R.id.redoLayout)
    @Override
    public void redoButtonClicked(View view) {
        mPresenter.redo();
        if (isHapticFeedbackEnabled)
            hapticFeedback(view);
    }

    @OnClick(R.id.kickerIsSafeButton)
    @Override
    public void kickerIsSafeButtonClicked(View view) {
        mPresenter.resetCount();
        if (isHapticFeedbackEnabled)
            hapticFeedback(view);
    }

    @OnClick(R.id.gameClockLayout)
    @Override
    public void gameClockButtonClicked(View view) {
        mPresenter.startStopGameClock(false);
        if (isHapticFeedbackEnabled)
            hapticFeedback(view);
    }

    @OnLongClick(R.id.gameClockLayout)
    @Override
    public boolean gameClockButtonLongClicked(View view) {
        mArgs.putString(GAME_CLOCK_BUTTONS_PRESSED, "gameClockButton");
        mArgs.putString("gameClockTime", gameClockTextView.getText().toString().substring(0, gameClockTextView.getText().toString().length() - 3));
        gameClockDialogFragment.setArguments(mArgs);
        mPresenter.startStopGameClock(true);
        gameClockDialogFragment.show(getFragmentManager(), "TAG");
        if (isHapticFeedbackEnabled)
            hapticFeedback(view);
        return true;
    }

    @Override
    public void updateBallCountTextView(String ballCount) {
        ballCountTextView.setText(ballCount);
    }

    @Override
    public void updateStrikeCountTextView(String strikeCount) {
        strikeCountTextView.setText(strikeCount);
    }

    @Override
    public void updateFoulCountTextView(String foulCount) {
        foulCountTextView.setText(foulCount);
    }

    @Override
    public void updateOutCountTextView(String outCount) {
        outCountTextView.setText(outCount);
    }

    @Override
    public void updateHomeScoreTextView(String homeScore) {
        homeTeamScoreTextView.setText(homeScore);
    }

    @Override
    public void updateAwayScoreTextView(String awayScore) {
        awayTeamScoreTextView.setText(awayScore);
    }

    @Override
    public void updateInningTextView(String inning) {
        inningCountTextView.setText(inning);
    }

    @Override
    public void updateUndoLayoutVisibility(boolean isNotClickable) {

        if (isNotClickable) {
            undoLayout.setAlpha(.5f);
            undoLayout.setClickable(false);
        } else {
            undoLayout.setAlpha(1);
            undoLayout.setClickable(true);
        }
    }

    @Override
    public void updateRedoLayoutVisibility(boolean isNotClickable) {

        if (isNotClickable) {
            redoLayout.setAlpha(.5f);
            redoLayout.setClickable(false);
        } else {
            redoLayout.setAlpha(1);
            redoLayout.setClickable(true);
        }
    }

    @Override
    public void updateInningArrowImageView(boolean isBottomOfInning) {

        if (isBottomOfInning)
            inningArrowImageView.setRotation(180);
        else
            inningArrowImageView.setRotation(0);
    }

    @Override
    public void updateAwayArrowImageView(boolean isBottomOfInning) {
        if (isBottomOfInning)
            awayArrowImageView.setVisibility(View.INVISIBLE);
        else
            awayArrowImageView.setVisibility(View.VISIBLE);

    }

    @Override
    public void updateHomeArrowImageView(boolean isBottomOfInning) {
        if (isBottomOfInning)
            homeArrowImageView.setVisibility(View.VISIBLE);
        else
            homeArrowImageView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void updateAwayTeamBannerView(String teamName, int teamColor) {
        awayTeamNameTextView.setText(teamName);
        GradientDrawable backgroundGradient = (GradientDrawable) awayTeamNameTextView.getBackground();
        if (teamColor != 0) {
            backgroundGradient.setColor(teamColor);
        } else {
            backgroundGradient.setColor(getResources().getColor(android.R.color.transparent));

        }
    }

    @Override
    public void updateHomeTeamBannerView(String teamName, int teamColor) {

        homeTeamNameTextView.setText(teamName);
        GradientDrawable backgroundGradient = (GradientDrawable) homeTeamNameTextView.getBackground();
        if (teamColor != 0) {
            backgroundGradient.setColors(new int[]{Color.WHITE, teamColor});
        } else {
            backgroundGradient.setColor(getResources().getColor(android.R.color.transparent));

        }

    }

    @Override
    public void updateGameClockTextView(String gameClock) {
        gameClockTextView.setText(gameClock);
    }

    @Override
    public void setStartingCount() {
        mPresenter.setStartingCount(sharedPreferences.getInt(getResources().getString(R.string.SP_STARTING_BALL_COUNT_KEY), 0),
                sharedPreferences.getInt(getResources().getString(R.string.SP_STARTING_STRIKE_COUNT_KEY), 0),
                sharedPreferences.getInt(getResources().getString(R.string.SP_STARTING_FOUL_COUNT_KEY), 0),
                sharedPreferences.getInt(getResources().getString(R.string.SP_STARTING_OUT_COUNT_KEY), 0));
    }

    public static void resetClickerData() {
        mPresenter.resetData();
    }

    public void setMaxInnings() {
        mPresenter.setMaxInnings(sharedPreferences.getInt(getResources().getString(R.string.SP_MAX_NUMBER_INNING_KEY), 5));
    }

    private void hapticFeedback(View view) {

        if (view.isHapticFeedbackEnabled()) {
            view.setHapticFeedbackEnabled(true);
        }
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
    }

    @Override
    public void updateSharePreferences() {

        isHapticFeedbackEnabled = sharedPreferences.getBoolean(getResources().getString(R.string.SP_HAPTIC_FEEDBACK_SETTINGS_KEY), false);
        mPresenter.setThreeFoulOption(sharedPreferences.getBoolean(getResources().getString(R.string.SP_THREE_FOULS_SETTINGS_KEY), false));
        setStartingCount();
        setMaxInnings();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getView() != null) {
            isViewShown = true;
            //  updateSharePreferences() contains logic to update Share preference when page is selected
            updateSharePreferences();
        } else {
            isViewShown = false;
        }
    }
}
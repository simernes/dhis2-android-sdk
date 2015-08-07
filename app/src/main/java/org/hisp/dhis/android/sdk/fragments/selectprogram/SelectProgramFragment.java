package org.hisp.dhis.android.sdk.fragments.selectprogram;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.activities.INavigationHandler;
import org.hisp.dhis.android.sdk.controllers.Dhis2;
import org.hisp.dhis.android.sdk.controllers.ResponseHolder;
import org.hisp.dhis.android.sdk.fragments.SettingsFragment;
import org.hisp.dhis.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.Program;
import org.hisp.dhis.android.sdk.utils.ui.adapters.AbsAdapter;
import org.hisp.dhis.android.sdk.utils.ui.adapters.rows.events.EventRow;
import org.hisp.dhis.android.sdk.utils.ui.dialogs.AutoCompleteDialogFragment;
import org.hisp.dhis.android.sdk.utils.ui.dialogs.OrgUnitDialogFragment;
import org.hisp.dhis.android.sdk.utils.ui.dialogs.ProgramDialogFragment;
import org.hisp.dhis.android.sdk.utils.ui.views.CardTextViewButton;

import java.util.List;

public abstract class SelectProgramFragment extends Fragment
        implements View.OnClickListener, AutoCompleteDialogFragment.OnOptionSelectedListener,
        SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<List<EventRow>> {
    public static final String TAG = SelectProgramFragment.class.getSimpleName();
    protected final String STATE;
    protected final int LOADER_ID;

    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected ListView mListView;
    protected ProgressBar mProgressBar;
    protected AbsAdapter mAdapter;

    protected CardTextViewButton mOrgUnitButton;
    protected CardTextViewButton mProgramButton;

    protected SelectProgramFragmentState mState;
    protected SelectProgramFragmentPreferences mPrefs;

    protected INavigationHandler mNavigationHandler;

    public SelectProgramFragment() {
        this("state:SelectProgramFragment", 1);
    }

    public SelectProgramFragment(String stateName, int loaderId) {
        STATE = stateName;
        LOADER_ID = loaderId;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof INavigationHandler) {
            mNavigationHandler = (INavigationHandler) activity;
        } else {
            throw new IllegalArgumentException("Activity must " +
                    "implement INavigationHandler interface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // we need to nullify reference
        // to parent activity in order not to leak it
        mNavigationHandler = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_program, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mPrefs = new SelectProgramFragmentPreferences(
                getActivity().getApplicationContext());

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_to_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.Green, R.color.Blue, R.color.orange);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mListView = (ListView) view.findViewById(R.id.event_listview);
        mAdapter = getAdapter(savedInstanceState);
        View header = getListViewHeader(savedInstanceState);
        setStandardButtons(header);
        mListView.addHeaderView(header, TAG, false);
        mListView.setAdapter(mAdapter);

        setRefreshing(Dhis2.getInstance().isLoading());

        if (savedInstanceState != null &&
                savedInstanceState.getParcelable(STATE) != null) {
            mState = savedInstanceState.getParcelable(STATE);
        }

        if (mState == null) {
            // restoring last selection of program
            Pair<String, String> orgUnit = mPrefs.getOrgUnit();
            Pair<String, String> program = mPrefs.getProgram();
            mState = new SelectProgramFragmentState();
            if (orgUnit != null) {
                mState.setOrgUnit(orgUnit.first, orgUnit.second);
                if (program != null) {
                    mState.setProgram(program.first, program.second);
                }
            }
        }

        onRestoreState(true);
    }

    protected abstract AbsAdapter getAdapter(Bundle savedInstanceState);

    protected View getListViewHeader(Bundle savedInstanceState) {
        View header = getLayoutInflater(savedInstanceState).inflate(
                R.layout.fragment_select_program_header, mListView, false
        );
        return header;
    }

    protected void setStandardButtons(View header) {
        mProgressBar = (ProgressBar) header.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);
        mOrgUnitButton = (CardTextViewButton) header.findViewById(R.id.select_organisation_unit);
        mProgramButton = (CardTextViewButton) header.findViewById(R.id.select_program);

        mOrgUnitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrgUnitDialogFragment fragment = OrgUnitDialogFragment
                        .newInstance(SelectProgramFragment.this);
                fragment.show(getChildFragmentManager());
            }
        });
        mProgramButton.setOnClickListener(getProgramButtonOnClickListener());

        mOrgUnitButton.setEnabled(true);
        mProgramButton.setEnabled(false);
    }

    protected abstract View.OnClickListener getProgramButtonOnClickListener();

    @Override
    public void onPause() {
        super.onPause();
        Dhis2Application.getEventBus().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Dhis2Application.getEventBus().register(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            mNavigationHandler.switchFragment(
                    new SettingsFragment(), SettingsFragment.TAG, true);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        out.putParcelable(STATE, mState);
        super.onSaveInstanceState(out);
    }

    @Override
    public void onOptionSelected(int dialogId, int position, String id, String name) {
        switch (dialogId) {
            case OrgUnitDialogFragment.ID: {
                onUnitSelected(id, name);
                break;
            }
            case ProgramDialogFragment.ID: {
                onProgramSelected(id, name);
                break;
            }
        }
    }

    @Override
    public abstract Loader<List<EventRow>> onCreateLoader(int id, Bundle args);

    @Override
    public void onLoadFinished(Loader<List<EventRow>> loader, List<EventRow> data) {
        if (LOADER_ID == loader.getId()) {
            mProgressBar.setVisibility(View.GONE);
            mAdapter.swapData(data);
            setRefreshing(false);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<EventRow>> loader) {
        mAdapter.swapData(null);
    }

    public void onRefreshFinished() {
        setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        if (isAdded()) {
            Context context = getActivity().getBaseContext();
            Toast.makeText(context, getString(R.string.syncing), Toast.LENGTH_SHORT).show();
            ApiRequestCallback callback = new ApiRequestCallback() {
                @Override
                public void onSuccess(ResponseHolder holder) {
                    onRefreshFinished();
                }

                @Override
                public void onFailure(ResponseHolder holder) {
                    onRefreshFinished();
                }
            };
            Dhis2.synchronize(context, callback);
        }
    }

    protected void setRefreshing(final boolean refreshing) {
        /* workaround for bug in android support v4 library */
        if (mSwipeRefreshLayout.isRefreshing() != refreshing) {
            System.out.println("VIEW: " + mSwipeRefreshLayout.isRefreshing() +
                    " BOOL: " + refreshing);
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(refreshing);
                }
            });
        }
    }

    public void onRestoreState(boolean hasUnits) {
        mOrgUnitButton.setEnabled(hasUnits);
        if (!hasUnits) {
            return;
        }

        SelectProgramFragmentState backedUpState = new SelectProgramFragmentState(mState);
        if (!backedUpState.isOrgUnitEmpty()) {
            onUnitSelected(
                    backedUpState.getOrgUnitId(),
                    backedUpState.getOrgUnitLabel()
            );

            if (!backedUpState.isProgramEmpty()) {
                onProgramSelected(
                        backedUpState.getProgramId(),
                        backedUpState.getProgramName()
                );
            }
        }
    }

    public void onUnitSelected(String orgUnitId, String orgUnitLabel) {
        mOrgUnitButton.setText(orgUnitLabel);
        mProgramButton.setEnabled(true);

        mState.setOrgUnit(orgUnitId, orgUnitLabel);
        mState.resetProgram();

        mPrefs.putOrgUnit(new Pair<>(orgUnitId, orgUnitLabel));
        mPrefs.putProgram(null);

        handleViews(0);
    }

    public void onProgramSelected(String programId, String programName) {
        mProgramButton.setText(programName);

        mState.setProgram(programId, programName);
        mPrefs.putProgram(new Pair<>(programId, programName));
        handleViews(1);

        mProgressBar.setVisibility(View.VISIBLE);
        // this call will trigger onCreateLoader method
        getLoaderManager().restartLoader(LOADER_ID, getArguments(), this);
    }

    protected abstract void handleViews(int level);
}
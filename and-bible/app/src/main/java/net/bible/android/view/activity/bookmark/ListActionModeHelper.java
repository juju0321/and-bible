package net.bible.android.view.activity.bookmark;

import android.support.v7.view.ActionMode;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import net.bible.android.activity.R;
import net.bible.service.db.bookmark.BookmarkDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Assists ListViews with action mode
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 * @see gnu.lgpl.License for license details.<br>
 * The copyright to this program is held by it's author.
 */
public class ListActionModeHelper {

	private ActionMode actionMode;

	private ListView list;

	private ArrayAdapter arrayAdapter;

	private static final String TAG = "ActionModeHelper";
	private boolean inActionMode = false;

	public ListActionModeHelper(ListView list, ArrayAdapter<BookmarkDto> bookmarkArrayAdapter) {
		this.list = list;
		this.arrayAdapter = bookmarkArrayAdapter;
	}

	public boolean isInActionMode() {
		return inActionMode;
	}

	public boolean startActionMode(final ActionModeActivity activity, int position) {
		inActionMode = true;
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		list.setItemChecked(position, true);
		list.setLongClickable(false);

		actionMode = activity.startSupportActionMode(new ActionMode.Callback() {
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				if (inflater != null) {
					inflater.inflate(R.menu.bookmark_context_menu, menu);
				}
				return true;
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				List<Integer> selectedItemPositions = getSelecteditemPositions(list.getCheckedItemPositions());

				activity.onActionItemClicked(item, selectedItemPositions);

				actionMode.finish();
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				Log.d(TAG, "onDeestroy");
				if (actionMode!=null) {
					Log.d(TAG, "onDeestroy not null");
					inActionMode = false;
					actionMode = null;
					list.setLongClickable(true);

					list.clearChoices();
					list.requestLayout();

					list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
				}
			}
		});

		return(true);
	}

	private List<Integer> getSelecteditemPositions(SparseBooleanArray positionStates) {
		List<Integer> selectedItemPositions = new ArrayList<>();

		for (int i=0; i<positionStates.size(); i++) {
			int position = positionStates.keyAt(i);
			if (positionStates.get(position)) {
				selectedItemPositions.add(position);
			}
		}

		return selectedItemPositions;
	}


	public interface ActionModeActivity {
		ActionMode startSupportActionMode(ActionMode.Callback callback);
		boolean onActionItemClicked(MenuItem item, List<Integer> selectedItemPositions);
		ListView getListView();
	}
}

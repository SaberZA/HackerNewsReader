package com.example.stevenv.hackernewsreader;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;

import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.JauntException;
import com.jaunt.SearchException;
import com.jaunt.UserAgent;

import java.util.ArrayList;
import java.util.List;


/**
 * An activity representing a list of Posts. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PostDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link PostListFragment} and the item details
 * (if present) is a {@link PostDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link PostListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class PostListActivity extends Activity
        implements PostListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        loadBlogData();

        if (findViewById(R.id.post_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((PostListFragment) getFragmentManager()
                    .findFragmentById(R.id.post_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    private void loadBlogData() {
        DataLoaderTask task = new DataLoaderTask();
        task.execute(null,null,null);

    }

    private class DataLoaderTask extends AsyncTask<Object,Void,Integer>
    {

        @Override
        protected Integer doInBackground(Object... objects) {

            LoadDataFromSource();


            return 1;
        }

        private void LoadDataFromSource() {
            try{
                UserAgent userAgent = new UserAgent();
                userAgent.visit("https://news.ycombinator.com/");
//                System.out.println(userAgent.doc.innerHTML());
//                List<Element> blogTable = userAgent.doc.findFirst("<table>").getChildElements();
                Elements blogTable = userAgent.doc.findFirst("<body>")
                        .findFirst("<center>")
                        .findFirst("<table>")
                        .findEach("<tr>")
                        ;
//                Element innerTable = blogTable.findFirst("<table>");
//                Elements tds = innerTable.findEach("<td>");
                System.out.println("Blog table element: " +blogTable.innerHTML());
                System.out.println("Blog table size: " +blogTable.size());
                Element ele1 = blogTable.getElement(2);
                System.out.println("Required table holder: " + ele1.innerHTML());

                Elements correctTable = ele1.findFirst("<td>")
                    .findFirst("<table>")
                        .findEach("<tr>");

                System.out.println("Blog table With titles: "+correctTable.innerHTML());
                int size = correctTable.size();
                System.out.println("Blog table With titles size: "+ size);

                ArrayList<Element> titleRows = new ArrayList<Element>();

                for (int i =0 ; i < size ; i=i++ ){
                    if (i%3 == 0) {
                        titleRows.add(correctTable.getElement(i));
                    }
                }

                

//                System.out.println("Blog table children: "+blogTable.size());
//                System.out.println("Blog table With Titles: "+blogTable.get(2).innerHTML());
//                for (Element td : tds) {
//                    System.out.println(td.outerHTML());
//                }

//                Element tableBody = blogTable.findFirst("<tbody>");


//                System.out.println("Blog table body element: " +tableBody);
//
//                Elements titles = blogTable.findEvery("<td class=title>");
//                System.out.println("Titles: "+titles.size());
            }
            catch(SearchException e){
                System.err.println(e);
            }
            catch(JauntException e) {
                System.err.println(e);
            }

        }
    }


    /**
     * Callback method from {@link PostListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(PostDetailFragment.ARG_ITEM_ID, id);
            PostDetailFragment fragment = new PostDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.post_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, PostDetailActivity.class);
            detailIntent.putExtra(PostDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}

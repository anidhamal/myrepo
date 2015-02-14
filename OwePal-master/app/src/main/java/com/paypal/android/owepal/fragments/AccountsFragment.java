package com.paypal.android.owepal.fragments;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import android.app.Activity;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.paypal.android.owepal.R;
import com.paypal.android.owepal.data.DemoContent;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountsFragment extends ListFragment

//        implements AbsListView.OnItemClickListener{
//implements AdapterView.OnItemClickListener{
{

    public AccountsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(com.paypal.android.owepal.R.layout.fragment_accounts, container, false);


        View view = inflater.inflate(com.paypal.android.owepal.R.layout.fragment_accounts, container, false);

        // Set the adapter
        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
//        mListView.setOnItemClickListener(this);
//        mListView.setOnItemClickListener(this);

        return view;
    }


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static AccountsFragment newInstance(String param1, String param2) {
        AccountsFragment fragment = new AccountsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // TODO: Change Adapter to display your content
        mAdapter = new ArrayAdapter<DemoContent.DummyItem>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, DemoContent.ITEMS);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


//    @Override
//         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        if (null != mListener) {
//            // Notify the active callbacks interface (the activity, if the
//            // fragment is attached to one) that an item has been selected.
//            mListener.onFragmentInteraction(DemoContent.ITEMS.get(position).id);
//            Toast.makeText(getActivity(), "Item clicked :: " + position, Toast.LENGTH_SHORT).show();
////            Log.v("Clicked ITEM", "Item position :: " + position);
//        }
//    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DemoContent.ITEMS.get(position).id);
            Toast.makeText(getActivity(), "Item clicked :: " + position, Toast.LENGTH_SHORT).show();
//            Log.v("Clicked ITEM", "Item position :: " + position);
            showDetails(position);
        }
    }
    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    void showDetails(int index) {
////        mCurCheckPosition = index;
//
////        if (mDualPane) {
//            // We can display everything in-place with fragments, so update
//            // the list to highlight the selected item and show the data.
//            getListView().setItemChecked(index, true);

            // Check what fragment is currently shown, replace if needed.
//            AccountDetailsFragment details = (AccountDetailsFragment)
//                    getFragmentManager().findFragmentById(R.id.content_frame);
////            if (details == null || details.getShownIndex() != index) {
//                // Make new fragment to show this selection.
////                details = AccountDetailsFragment.newInstance("param1", "param2");
//
//                // Execute a transaction, replacing any existing fragment
//                // with this one inside the frame.
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
////                if (index == 0) {
//                    ft.replace(R.id.content_frame, details);
////                } else {
////                    ft.replace(R.id.content_frame, details);
////                }
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//                ft.commit();
//            }
//
//        } else {
//            // Otherwise we need to launch a new activity to display
//            // the dialog fragment with selected text.
//            Intent intent = new Intent();
//            intent.setClass(getActivity(), DetailsActivity.class);
//            intent.putExtra("index", index);
//            startActivity(intent);
//        }

        // Create new fragment and transaction
        Fragment newFragment = new AccountDetailsFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack
        transaction.replace(R.id.content_frame, newFragment,"fragBack");
        transaction.addToBackStack("fragBack");

// Commit the transaction
        transaction.commit();
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                if (null != mListener) {
////            // Notify the active callbacks interface (the activity, if the
////            // fragment is attached to one) that an item has been selected.
////            mListener.onFragmentInteraction(DemoContent.ITEMS.get(position).id);
////            Toast.makeText(getActivity(), "Item clicked :: " + position, Toast.LENGTH_SHORT).show();
//////            Log.v("Clicked ITEM", "Item position :: " + position);
////        }
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

//    @Override
//    public void onBackPressed() {
//        if (getFragmentManager().findFragmentByTag("FragmentC") != null) {
//            // I'm viewing Fragment C
//            ge().popBackStack("A_B_TAG",
//                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
//        } else {
//            super.onBackPressed();
//        }
//    }

}

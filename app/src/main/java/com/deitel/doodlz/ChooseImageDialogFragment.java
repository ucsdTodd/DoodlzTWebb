package com.deitel.doodlz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChooseImageDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ChooseImageDialogFragment extends DialogFragment implements View.OnClickListener {

    private final static int FILE_SELECT_CODE = 11;

    private Button browseButton;
    private EditText imageText;

    public ChooseImageDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public Dialog onCreateDialog( Bundle bundle ) {
        // Inflate the layout for this fragment
        View chooseImageView = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_choose_image_dialog, null );

        browseButton = (Button) chooseImageView.findViewById( R.id.buttonImageBrowse );
        browseButton.setOnClickListener( this );

        imageText = (EditText)chooseImageView.findViewById( R.id.textImagePath );

        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        builder.setView( chooseImageView ); // add GUI to dialog

        final DoodleView doodleView = getDoodleFragment().getDoodleView();
        // set the AlertDialog's message
        builder.setTitle( "Choose Image" );
        // add Set Color Button
        builder.setPositiveButton(R.string.button_set_bg_image,
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    (new Thread( new Runnable(){ // Networking Off UI thread
                        @Override
                        public void run() {
                            final Bitmap bgImage = getBitmapInBackground( imageText.getText().toString() );
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                public void run() {
                                    doodleView.setBackgroundImage( bgImage );
                                }

                            });
                        }
                    })).start();
                }
            }
        );

        return builder.create();
    }

    // gets a reference to the MainActivityFragment
    private MainActivityFragment getDoodleFragment() {
        return (MainActivityFragment) getFragmentManager().findFragmentById(
                R.id.doodleFragment);
    }

    private Bitmap getBitmapInBackground(String urlText ){
        Bitmap bitmap = null;
        HttpURLConnection connection = null;
        try{
            URL url = new URL( urlText ); // create URL for image
            connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        }
        catch( Exception x ){
            Log.e( "IMAGE", "Exception attempting to load" +urlText +" " +x.getMessage(), x );
        }
        finally{
            if( connection != null ) {
                connection.disconnect();
            }
        }
        return bitmap;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        if( view.getId() == browseButton.getId() ){
            Log.i( "TEST", "Button pressed" );
            showFileChooser();
        }
    }

    private void showFileChooser() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/xml");   //XML file only
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult( Intent.createChooser (intent, "Select a File to Upload"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText( getContext(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

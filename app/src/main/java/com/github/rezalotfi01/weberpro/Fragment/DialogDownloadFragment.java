package com.github.rezalotfi01.weberpro.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.github.rezalotfi01.weberpro.Activity.DownloadSettingsActivity;
import com.github.rezalotfi01.weberpro.Database.DownloadsDB.DownloadDBUtils;
import com.github.rezalotfi01.weberpro.Database.DownloadsDB.DownloadEntity;
import com.github.rezalotfi01.weberpro.R;
import com.github.rezalotfi01.weberpro.Service.DownloaderService;
import com.github.rezalotfi01.weberpro.Utils.BroadcastUtils;
import com.github.rezalotfi01.weberpro.Utils.GeneralUtils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

/**
 * Created  on 10/05/2016.
 */
public class DialogDownloadFragment extends DialogFragment {
    private EditText downloadLinkEditText;
    private EditText downloadFileNameEditText;
    private TextView txtSize;

    private Context context;
    private View rootView;

    private String downloadURL;
    private String contentDisposition;
    private String MIMEType;
    private String fileName;
    private String fileURL;
    private String fileSaveAddress;
    private long fileSize = 0;
    private DownloadEntity similarDownload;
    public DialogDownloadFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = inflater.inflate(R.layout.download_dialog, container, false);
        context = getActivity();

        Bundle args = getArguments();
        if (args != null){
            downloadURL = args.getString("url"," ");
            contentDisposition = args.getString("content_dis");
            MIMEType = args.getString("mime");
        }else {
            downloadURL = "";
            contentDisposition = null;
            MIMEType = null;
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        fileSaveAddress = prefs.getString("file_picker_pref",null);
        if (fileSaveAddress != null){
            fileSaveAddress = fileSaveAddress.replace(":","");
        }

        initComponents();
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    private void initComponents(){
        ImageView btnDownloadSetting = rootView.findViewById(R.id.img_dialog_setting);
        btnDownloadSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DownloadSettingsActivity.class);
                startActivity(intent);
            }
        });

        txtSize = rootView.findViewById(R.id.txtDownloadSizeInDialog);
        txtSize.setText(R.string.download_dialog_zero_size_text);
        downloadFileNameEditText = rootView.findViewById(R.id.edit_download_file_name);

        downloadLinkEditText = rootView.findViewById(R.id.edit_download_link);
        downloadLinkEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence changedText, int start, int before, int count) {

//                downloadFileNameEditText.setText(BrowserUtils.getFileNameFromUrl(String.valueOf(changedText)));
                if (changedText == null){
                    return;
                }
                downloadFileNameEditText.setText(URLUtil.guessFileName(String.valueOf(changedText),contentDisposition,MIMEType));

                try {
/*                    String size = new networkTask().execute(changedText.toString()).get();
                    if (size != ""){
                        txtSize.setText(size);
                    }*/
                    new getNetSizeTask().execute(changedText.toString());
                } catch (Exception e) {
                    Log.e("TAG", "onSetSizeText Exception : "+e.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        downloadLinkEditText.setText(downloadURL);

        ImageView btnAddressChooser = rootView.findViewById(R.id.img_dialog_folder);
        btnAddressChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openFilePickerDialog
                DialogProperties properties= new DialogProperties();
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.DIR_SELECT;
                properties.root = new File(DialogConfigs.DEFAULT_DIR);
                properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);

                FilePickerDialog dialog = new FilePickerDialog(context,properties);
                dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void onSelectedFilePaths(String[] dirs) {
                        //dirs is the array of the paths selected by the Application User.
                        try {
                            if (dirs.length > 0) {
                                fileSaveAddress = dirs[0];
                            }
                        }catch (Exception e){
                            Log.e("Weber TAG", "onSelectedFilePaths Exception : "+e.toString());
                        }
                    }
                });
                dialog.show();
            }
        });

        Button btnStartDownload = rootView.findViewById(R.id.dialog_button_start);
        btnStartDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start download

                fileName = downloadFileNameEditText.getText().toString();
                fileURL = downloadLinkEditText.getText().toString();

                if (fileName.trim() == null || fileName.trim().equals("")){
                    downloadFileNameEditText.setError(getString(R.string.download_dialog_edittext_error));
                    return;
                }
                if (fileURL.trim() == null || fileURL.trim().equals("")){
                    downloadLinkEditText.setError(getString(R.string.download_dialog_edittext_error));
                    return;
                }

                if (isDownloadExist(fileURL))
                {
                    //show message for replace or not
                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setMessage(getString(R.string.download_exist_message))
                            .setPositiveButton(R.string.dialog_button_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    try {
                                        Intent pleasePauseIntent = new Intent(BroadcastUtils.INTENT_FILTER_PLEASE_PAUSE_DOWNLOAD);
                                        pleasePauseIntent.putExtra("URL", fileURL);
                                        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).sendBroadcast(pleasePauseIntent);

                                        DownloadDBUtils.deleteDownloadByURL(context, fileURL);

                                        File dFile = new File(fileSaveAddress + File.separator + similarDownload.getFileName()  + ".temp");
                                        dFile.delete();
                                    }catch (Exception e){
                                        Log.e("Weber TAG", "onDelete Past File : "+e.toString() );
                                    }

                                    Intent intent = new Intent(context, DownloaderService.class);
                                    intent.putExtra("url", fileURL);
                                    intent.putExtra("name", fileName);
                                    intent.putExtra("save_address", fileSaveAddress);
                                    intent.putExtra("size",fileSize);
                                    intent.putExtra("is_for_now", true);
                                    context.startService(intent);
                                    dismiss();
                                }
                            })
                            .setNegativeButton(R.string.dialog_button_negative, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //nothing...
                                }
                            }).create();
                    dialog.show();
                }else {
                    Intent intent = new Intent(context, DownloaderService.class);
                    intent.putExtra("url", fileURL);
                    intent.putExtra("name", fileName);
                    intent.putExtra("save_address", fileSaveAddress);
                    intent.putExtra("is_for_now", true);
                    context.startService(intent);
                    dismiss();
                }
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BroadcastUtils.INTENT_FILTER_DOWNLOAD_PENDING));
            }
        });

        Button btnAddToQueue = rootView.findViewById(R.id.dialog_button_add);
        btnAddToQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add to download queue
                fileName = downloadFileNameEditText.getText().toString().trim();
                fileURL = downloadLinkEditText.getText().toString().trim();

                if (fileName.trim() == null || fileName.trim().equals("")){
                    downloadFileNameEditText.setError(getString(R.string.download_dialog_edittext_error));
                    return;
                }
                if (fileURL.trim() == null || fileURL.trim().equals("")){
                    downloadLinkEditText.setError(getString(R.string.download_dialog_edittext_error));
                    return;
                }

                if (isDownloadExist(fileURL)){
                    //show message for replace or not
                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setMessage(getString(R.string.download_exist_message))
                            .setPositiveButton(R.string.dialog_button_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    try {
                                        Intent pleasePauseIntent = new Intent(BroadcastUtils.INTENT_FILTER_PLEASE_PAUSE_DOWNLOAD);
                                        pleasePauseIntent.putExtra("URL", fileURL);
                                        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).sendBroadcast(pleasePauseIntent);

                                        DownloadDBUtils.deleteDownloadByURL(context, fileURL);

                                        File dFile = new File(fileSaveAddress + File.separator + similarDownload.getFileName() + ".temp");
                                        dFile.delete();
                                    }catch (Exception e){
                                        Log.e("Weber TAG", "onDelete Past File : "+e.toString() );
                                    }

                                    Intent intent = new Intent(context, DownloaderService.class);
                                    intent.putExtra("url", fileURL);
                                    intent.putExtra("name", fileName);
                                    intent.putExtra("save_address", fileSaveAddress);
                                    intent.putExtra("size",fileSize);
                                    intent.putExtra("is_for_now", false);
                                    intent.putExtra("is_resume", false);
                                    context.startService(intent);
                                    dismiss();
                                }
                            })
                            .setNegativeButton(R.string.dialog_button_negative, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //nothing...
                                }
                            }).create();
                    dialog.show();

                }else {
                    Intent intent = new Intent(context, DownloaderService.class);
                    intent.putExtra("url", fileURL);
                    intent.putExtra("name", fileName);
                    intent.putExtra("save_address", fileSaveAddress);
                    intent.putExtra("is_for_now", false);
                    intent.putExtra("is_resume", false);
                    context.startService(intent);
                    dismiss();
                }
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BroadcastUtils.INTENT_FILTER_DOWNLOAD_PENDING));
            }
        });


    }


    private boolean isDownloadExist(String downloadFileURL){
        boolean isExist;
        List<DownloadEntity> similarDownloads = DownloadDBUtils.getDownloadsListByURL(getContext(),downloadFileURL);
        if (similarDownloads.size() > 0){
            isExist = true;
            similarDownload = similarDownloads.get(0);
        }else {
            isExist = false;
        }

        return isExist;
    }

    class getNetSizeTask extends AsyncTask<String,String,String>
    {
        String sizeStr;
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            URL serverAddress = null;
            sizeStr = " ";

            try {
                String sourceFileWebAddress = strings[0];
                serverAddress = new URL(sourceFileWebAddress);
                //set up out communications stuff
                connection = null;

                //Set up the initial connection
                connection = (HttpURLConnection)serverAddress.openConnection();

                //HEAD request will make sure that the contents are not downloaded.
                connection.setRequestMethod("HEAD");

                connection.connect();

                long sizeLng = connection.getContentLength();

                if (sizeLng > 0) {
                    sizeStr = getString(R.string.download_dialog_size_text)+" "+ GeneralUtils.readableFileSize(sizeLng);
                    fileSize = sizeLng;
                }else {
                    sizeStr = getString(R.string.download_dialog_size_unknown_text);
                }

                Log.e("Weber TAG", "getSizeTask doInBackground Size : " + "========"+sizeStr);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
                //close the connection, set all objects to null
            try {
                connection.disconnect();
                connection = null;
            }catch (Exception e){
                Log.e("Weber TAG", "getSizeTask doInBackground 2's try block Exception : "+e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                txtSize.setText(sizeStr);
            }catch (Exception e){
                Log.e("Weber TAG", " onPostExecuteSizeTask set textSize Exception : "+e.toString());
            }
        }
    }

}

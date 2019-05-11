package dianyo.apex;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

/**
 * Created by dianyo on 2017/4/27.
 */

public class CustomDialogFragment extends DialogFragment {
    private String type;
    private AlertDialog.Builder builder;
    private SharedPreferences sharedPreferences;
    static CustomDialogFragment newInstance(String type){
        CustomDialogFragment f = new CustomDialogFragment();

        Bundle args = new Bundle();
        args.putString("Type", type);
        f.setArguments(args);

        return f;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        type = getArguments().getString("Type");
        builder = new AlertDialog.Builder(getActivity());
        switch (type){
            case "noticeSetting":
                Pair<Boolean, Boolean> modes = Pair.create(
                        sharedPreferences.getBoolean("ringMode", false),
                        sharedPreferences.getBoolean("vibrateMode", false));
                buildNoticeSetting(modes);
                break;
            default:
                break;
        }
        return builder.create();
    }

    private void buildNoticeSetting(Pair<Boolean, Boolean> modes){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_notice, null);

        //set the mode status
        final Switch ringMode = (Switch) v.findViewById(R.id.ringModeSwitch);
        final Switch vibrateMode = (Switch) v.findViewById(R.id.vibrateModeSwitch);
        ringMode.setChecked(modes.first);
        vibrateMode.setChecked(modes.second);

        Button OkButton = (Button) v.findViewById(R.id.noticeSettingOk);
        Button cancelButton = (Button) v.findViewById(R.id.noticeSettingCancel);
        OkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pair<Boolean, Boolean> newModes = Pair.create(
                        ringMode.isChecked(), vibrateMode.isChecked());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("ringMode", newModes.first);
                editor.putBoolean("vibrateMode", newModes.second);
                editor.apply();
                CustomDialogFragment.this.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialogFragment.this.dismiss();
            }
        });
        //set builder
        builder.setView(v);
    }
}

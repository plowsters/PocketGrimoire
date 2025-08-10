package com.example.pocketgrimoire.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.DialogFragment;

import com.example.pocketgrimoire.LoginActivity;
import com.example.pocketgrimoire.R;

public class AccountDialogFragment extends DialogFragment {

    public static final String LOGOUT_EXTRA = "LOGOUT_EXTRA";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Account Options")
                .setItems(new CharSequence[]{"Logout"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            logout();
                        }
                    }
                });
        return builder.create();
    }

    private void logout() {
        // Clear the user ID from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(getString(R.string.preference_user_id_key));
        editor.apply();

        // Navigate back to LoginActivity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.putExtra(LOGOUT_EXTRA, true);
        startActivity(intent);
        getActivity().finish();
    }
}
package spr018.tcss450.clientapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import spr018.tcss450.clientapplication.utility.Pages;

public class LoginActivity extends AppCompatActivity
        implements LoginFragment.OnFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        loadFragment(new LoginFragment(), Pages.LOGIN);

    }

    @Override
    public void onLoginInteraction(int type) {
        switch (type) {
            case 0:
                showMainActivity();
                break;
            case 1:
                loadFragment(new RegisterFragment(), Pages.REGISTER);
                break;
            default:
                Log.wtf("Impossible", "How did this happen?");
                break;
        }
    }

    @Override
    public void onRegisterInteraction() {
        loadFragment(new LoginFragment(), Pages.LOGIN);
        Toast.makeText(this, "Registered!", Toast.LENGTH_SHORT).show();
    }


    /* Helpers */
    private void loadFragment(Fragment frag, Pages page) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.loginFragmentContainer, frag);

        if (page == Pages.REGISTER) {
            transaction.addToBackStack(null);
        }

        transaction.commit();

        setTitle(page.toString());
    }

    private void showMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

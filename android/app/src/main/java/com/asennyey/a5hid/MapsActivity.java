/**
 * @author Aramis Sennyey
 * This class is the main activity that handles fragment creation and navbar navigation.
 */

package com.asennyey.a5hid;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.asennyey.a5hid.api.ApiController;
import com.asennyey.a5hid.api.AuthenticationController;
import com.asennyey.a5hid.viewmodels.EventViewModel;
import com.google.android.gms.tasks.CancellationTokenSource;

public class MapsActivity extends AppCompatActivity implements ApiDialogFragment.ApiDialogListener {

    private ApiController controller;
    private final CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();

    private LocationController locationController;
    private AuthenticationController authController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null) {
            this.getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_maps);

        locationController = LocationController.getInstance(this);
        authController = AuthenticationController.getInstance(this);
        controller = ApiController.getInstance(this);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle("5hid");
        setSupportActionBar(myToolbar);

        showMap();
    }

    private void showMap(){
        show(MapFragment.class, "map");
    }

    private void showSettings(){
        show(SettingsFragment.class, "settings");
    }

    private void showHelp(){
        show(HelpFragment.class, "help");
    }

    private void showLeaderboard(){
        show(LeaderboardFragment.class, "leaderboard");
    }

    private void showFriends(){
        show(UserFragment.class, "friends");
    }

    private void showLogin(){
        new LoginFragment().show(getSupportFragmentManager(), "login");
    }

    private void add(Class<? extends Fragment> fragment, String name){
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_view, fragment, null)
                .addToBackStack(name)
                .setReorderingAllowed(true)
                .commit();
    }

    private void show(Class<? extends Fragment> fragment, String name){
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container_view, fragment, null)
            .addToBackStack(name)
            .setReorderingAllowed(true)
            .commit();
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        if (authController.isLoggedIn()) {
            menu.findItem(R.id.login_menu).setVisible(false);
            menu.findItem(R.id.friends_menu).setVisible(true);
        }else{
            menu.findItem(R.id.friends_menu).setVisible(false);
        }
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.login_menu:
                showLogin();
                return true;
            case R.id.map_menu:
                showMap();
                return true;
            case R.id.settings_menu:
                showSettings();
                return true;
            case R.id.help_menu:
                showHelp();
                return true;
            case R.id.leaderboard_menu:
                showLeaderboard();
                return true;
            case R.id.friends_menu:
                showFriends();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // button will be within maps fragment
    public void onNewEvent(View V) {

    }
    //TODO: implement login/credentials feature

    //TODO: implment create_event_page

    public void onLoginClick(){
        com.asennyey.a5hid.api.objects.write.User user = new com.asennyey.a5hid.api.objects.write.User();
        user.username = "asennyey@email.arizona.edu";
        user.password = "Araclasen6893.";
        controller.login(
                user,
                (res)->{
                    System.out.println(res);
                },
                (err)->{
                    System.out.println(err);
                }
        );
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if(dialog instanceof CreateEventFragment) {
            EventViewModel model = new ViewModelProvider(this).get(EventViewModel.class);
            model.getData();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
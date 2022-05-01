package com.asennyey.a5hid.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.asennyey.a5hid.api.ApiController;
import com.asennyey.a5hid.api.objects.read.Event;
import com.asennyey.a5hid.api.objects.read.User;

import java.util.List;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<User> selected = new MutableLiveData<>();
    private MutableLiveData<List<User>> users;
    private ApiController api = ApiController.getInstance();

    public void select(User item) {
        selected.setValue(item);
    }

    public LiveData<User> getSelected() {
        return selected;
    }

    public LiveData<List<User>> getUsers() {
        if (users == null) {
            users = new MutableLiveData<>();
            loadUsers();
        }
        return users;
    }

    private void loadUsers() {
        // Do an asynchronous operation to fetch users.
        api.getUsers((page)->{
            users.setValue(page.result.records);
        }, (err)->{
            System.out.println(err.result);
        });
    }
}

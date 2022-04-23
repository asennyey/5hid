package com.asennyey.a5hid.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.asennyey.a5hid.api.ApiController;
import com.asennyey.a5hid.api.objects.read.Event;

import java.util.List;

public class EventViewModel extends ViewModel {
    private final MutableLiveData<Event> selected = new MutableLiveData<>();
    private MutableLiveData<List<Event>> events;
    private ApiController api = ApiController.getInstance();

    public void select(Event item) {
        selected.setValue(item);
    }

    public LiveData<Event> getSelected() {
        return selected;
    }

    public LiveData<List<Event>> getEvents() {
        if (events == null) {
            events = new MutableLiveData<List<Event>>();
            loadEvents();
        }
        return events;
    }

    private void loadEvents() {
        // Do an asynchronous operation to fetch users.
        api.getEvents((page)->{
            events.setValue(page.result.records);
        }, (err)->{
            System.out.println(err.result);
        });
    }
}
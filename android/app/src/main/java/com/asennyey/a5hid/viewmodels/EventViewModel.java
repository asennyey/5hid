/**
 * @author Aramis Sennyey
 * This class handles the list of users (currently the list of friends), with additional
 *  potential for select functionality that would exist across fragments.
 */
package com.asennyey.a5hid.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.asennyey.a5hid.api.ApiController;
import com.asennyey.a5hid.api.objects.read.Event;

import java.util.List;

public class EventViewModel extends BaseViewModel<Event> {
    /**
     * Load the list of events from the API.
     */
    protected void loadData() {
        // Do an asynchronous operation to fetch users.
        api.getEvents((page)->{
            data.setValue(page.result.records);
        }, (err)->{
            System.out.println(err.result);
        });
    }
}

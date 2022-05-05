/**
 * @author Aramis Sennyey
 * This class is a base class for any view model that adds additional functionality and forces
 *  clients to use a specific interface.
 */
package com.asennyey.a5hid.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.asennyey.a5hid.api.ApiController;
import com.asennyey.a5hid.api.objects.read.Event;

import java.util.List;

public abstract class BaseViewModel<T> extends ViewModel {
    private final MutableLiveData<T> selected = new MutableLiveData<>();
    protected MutableLiveData<List<T>> data;
    protected ApiController api = ApiController.getInstance();

    /**
     * Set the currently selected item.
     * @param item the currently selected item.
     */
    public void select(T item) {
        selected.setValue(item);
    }

    /**
     * Get the currently selected item.
     * @return the currently selected item.
     */
    public LiveData<T> getSelected() {
        return selected;
    }

    public LiveData<List<T>> getData() {
        if (data == null) {
            data = new MutableLiveData<>();
            loadData();
        }
        return data;
    }

    /** This must populate data with some values. */
    protected abstract void loadData();
}

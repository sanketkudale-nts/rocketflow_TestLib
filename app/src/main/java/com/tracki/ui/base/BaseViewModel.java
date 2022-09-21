package com.tracki.ui.base;

import androidx.lifecycle.ViewModel;

import com.tracki.data.DataManager;
import com.tracki.utils.rx.SchedulerProvider;

import java.lang.ref.WeakReference;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by rahul.
 */

public abstract class BaseViewModel<N> extends ViewModel {

    private final DataManager mDataManager;

    private final SchedulerProvider mSchedulerProvider;
    private CompositeDisposable mCompositeDisposable;
    private WeakReference<N> mNavigator;

    public BaseViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        this.mDataManager = dataManager;
        this.mSchedulerProvider = schedulerProvider;
        this.mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onCleared() {
        mCompositeDisposable.dispose();
        super.onCleared();
    }

    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    public DataManager getDataManager() {
        return mDataManager;
    }

    public N getNavigator() {
        return mNavigator.get();
    }

    public void setNavigator(N navigator) {
        this.mNavigator = new WeakReference<>(navigator);
    }

    public SchedulerProvider getSchedulerProvider() {
        return mSchedulerProvider;
    }
}

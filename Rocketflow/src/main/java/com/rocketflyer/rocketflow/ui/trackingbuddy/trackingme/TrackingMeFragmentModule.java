/*
 *  Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://mindorks.com/license/apache-v2
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.rocketflyer.rocketflow.ui.trackingbuddy.trackingme;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tracki.ViewModelProviderFactory;
import com.tracki.data.DataManager;
import com.tracki.utils.rx.SchedulerProvider;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rahul abrol on 05/10/18.
 */
@Module
public class TrackingMeFragmentModule {

    @Provides
    TrackingMeViewModel trackingMeViewModel(DataManager dataManager,
                                            SchedulerProvider schedulerProvider) {
        return new TrackingMeViewModel(dataManager, schedulerProvider);
    }

    @Provides
    TrackingMeAdapter provideTrackingMeAdapter() {
        return new TrackingMeAdapter(new ArrayList<>());
    }

    @Provides
    ViewModelProvider.Factory provideTrackingMeViewModel(TrackingMeViewModel trackingMeViewModel) {
        return new ViewModelProviderFactory<>(trackingMeViewModel);
    }

    @Provides
    LinearLayoutManager provideLinearLayoutManager(TrackingMeFragment fragment) {
        return new LinearLayoutManager(fragment.getActivity());
    }
}

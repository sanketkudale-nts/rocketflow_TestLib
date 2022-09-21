package com.tracki.utils;

public interface NetworkStateReceiverListener {
    default void networkAvailable() {

    }

    default void networkUnavailable() {

    }
}

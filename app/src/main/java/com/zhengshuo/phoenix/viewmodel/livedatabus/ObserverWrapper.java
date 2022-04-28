package com.zhengshuo.phoenix.viewmodel.livedatabus;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

public class ObserverWrapper <T> implements Observer<T>

    {

        private Observer<T> observer;

        public ObserverWrapper(Observer<T> observer) {
        this.observer = observer;
    }

        @Override
        public void onChanged(@Nullable T t) {
        if (observer != null) {
            if (isCallOnObserve()) {
                return;
            }
            observer.onChanged(t);
        }
    }

        private boolean isCallOnObserve() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace != null && stackTrace.length > 0) {
            for (StackTraceElement element : stackTrace) {
                if ("android.arch.lifecycle.LiveData".equals(element.getClassName()) &&
                        "observeForever".equals(element.getMethodName())) {
                    return true;
                }
            }
        }
        return false;
    }
}

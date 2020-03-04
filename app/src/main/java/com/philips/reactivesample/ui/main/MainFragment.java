package com.philips.reactivesample.ui.main;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.reactivesample.R;
import com.philips.reactivesample.ui.main.model.Task;
import com.philips.reactivesample.ui.main.utility.DataSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.internal.observers.FutureObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainFragment extends Fragment {

    private static String TAG = "MainFragment";
    private MainViewModel mViewModel;

    private CompositeDisposable disposables = new CompositeDisposable();

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        Observable<Task> observableTask = Observable.fromIterable(DataSource.getTasks())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<Task>() {
                    @Override
                    public boolean test(Task task) throws Throwable {
                        return true;
                    }
                });

        Observable<Task> observableTaskCreate = Observable.create(new ObservableOnSubscribe<Task>() {
            @Override
            public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Task> emitter) throws Throwable {

                emitter.onNext(new Task("", 8));
            }
        });

        Observable<Task> observableTaskRange = Observable.range(0, 9)
                .map(new Function<Integer, Task>() {
                    @Override
                    public Task apply(Integer integer) throws Throwable {
                        return null;
                    }
                });

        Observable<Task> observableTaskRange1 = Observable.range(0, 9)
                .switchMap(new Function<Integer, ObservableSource<? extends Task>>() {
                    @Override
                    public ObservableSource<? extends Task> apply(Integer integer) throws Throwable {
                        return null;
                    }
                });

        final List<String> race = new ArrayList<>(Arrays.asList("Alan", "Bob", "Cobb", "Dan", "Evan", "Finch"));

        Observable<String> observableStrings = Observable.fromIterable(race).map(new Function<String, String>() {
            @Override
            public String apply(String s) {
                return s + "_mapped";
            }
        });

//        observableStrings.subscribe(new Consumer<String>() {
//            @Override
//            public void accept(String s) throws Throwable {
//
//                Log.d(TAG, "Map accept: " + s);
//            }
//        });

        Observable<String> observableFlatMap = Observable.fromIterable(race)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap(new Function<String, Observable<String>>() {
                    @Override
                    public Observable<String> apply(String s) throws Throwable {

                        Random r = new Random();
                        int rr =  r.nextInt((5 - 2) + 1) + 2;

//                        Thread.sleep(rr * 1000);
                        return Observable.just(s + "_flatMap1", s + "_flatMap2").delay(rr * 1000, TimeUnit.MILLISECONDS);
                    }
                });

//        observableFlatMap.subscribe(new Consumer<String>() {
//            @Override
//            public void accept(String s) throws Throwable {
//
//                Log.d(TAG, "Flat Map accept: " + s);
//            }
//        });
//        observableFlatMap.mergeWith(observableStrings).subscribe(new Consumer<String>() {
//            @Override
//            public void accept(String s) throws Throwable {
//                Log.d(TAG, "Merged Map accept: " + s);
//            }
//        });

        observableFlatMap.zipWith(observableStrings, (f, s) -> f + " " + s).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Throwable {
                Log.d(TAG, "Zip Map accept: " + s);
            }
        });

        Observable.combineLatest(observableFlatMap, observableStrings, (f, s) -> f + " " + s).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Throwable {
                Log.d(TAG, "CombineLatest Map accept: " + s);
            }
        });







//        observableTask.subscribe(new Observer<Task>() {
//            @Override
//            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
//
//                Log.d(TAG, "onSubscribe: ");
//                disposables.add(d);
//            }
//
//            @Override
//            public void onNext(@io.reactivex.rxjava3.annotations.NonNull Task task) {
//
//                Log.d(TAG, "onNext: " + task.toString());
//            }
//
//            @Override
//            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
//                Log.d(TAG, "onError: ");
//            }
//
//            @Override
//            public void onComplete() {
//                Log.d(TAG, "onComplete: ");
//            }
//        });


        disposables.add(observableTask.subscribe(new Consumer<Task>() {
            @Override
            public void accept(Task task) throws Throwable {
                Log.d(TAG, "onNext: " + task.toString());
            }
        }));



    }

    @Override
    public void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }
}

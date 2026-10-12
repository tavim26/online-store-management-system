package io.github.tavim26.onlinestore.ui;

import javafx.concurrent.Task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/** Runs slow work (database, password hashing) off the JavaFX thread, so the window never freezes. */
public final class Background {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(runnable -> {
        Thread thread = new Thread(runnable, "background-worker");
        thread.setDaemon(true); // does not keep the app alive after the window is closed
        return thread;
    });

    private Background() {
    }

    /**
     * Runs the work in the background, then calls onSuccess or onError on the JavaFX thread,
     * where it is safe to update the interface.
     */
    public static <T> void run(Callable<T> work, Consumer<T> onSuccess, Consumer<Throwable> onError) {
        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return work.call();
            }
        };
        task.setOnSucceeded(event -> onSuccess.accept(task.getValue()));
        task.setOnFailed(event -> onError.accept(task.getException()));
        EXECUTOR.execute(task);
    }
}
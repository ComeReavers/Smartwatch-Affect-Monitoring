package app.dev.pre_trialgalaxy.presentation

import android.content.Context
import android.service.autofill.UserData
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import app.dev.pre_trialgalaxy.presentation.database.UserDataStore
import app.dev.pre_trialgalaxy.presentation.database.entities.NotificationData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import java.time.LocalDateTime

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        //TODO try RoutineWorker with flex time for more reliability
        var notificationTimeId: Long
        UserDataStore
            .getUserRepository(applicationContext)
            .insertNotificationTime(CoroutineScope(GlobalScope.coroutineContext),
                NotificationData(0, LocalDateTime.now().toString())) {
                notificationTimeId = it.id
                Log.v("success", "This actually worked with $notificationTimeId")
                NotificationManager(applicationContext).promptNotification(notificationTimeId)
            }
        //TODO start oneTimeWorker to dismiss notification after 3 minutes
        Log.v("NotificationWorker","AH YES, SUCCESS")
        return Result.success()
    }
}
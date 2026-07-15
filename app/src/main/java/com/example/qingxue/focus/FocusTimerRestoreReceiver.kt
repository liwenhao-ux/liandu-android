package com.example.qingxue.focus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.qingxue.QingXueApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FocusTimerRestoreReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val app = context.applicationContext as QingXueApp
                if (app.focusTimerStore.currentTimerState().hasStarted) {
                    FocusTimerService.restore(context)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}

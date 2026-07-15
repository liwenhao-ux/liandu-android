package com.example.qingxue

import android.app.Application
import com.example.qingxue.data.AppDatabase
import com.example.qingxue.data.StudyRepository
import com.example.qingxue.focus.FocusTimerStore
import com.example.qingxue.music.MusicController
import com.example.qingxue.ui.theme.ThemePreferenceStore

class QingXueApp : Application() {
    val themePreferenceStore: ThemePreferenceStore by lazy {
        ThemePreferenceStore(this)
    }

    val focusTimerStore: FocusTimerStore by lazy {
        FocusTimerStore(this)
    }

    val musicController: MusicController by lazy {
        MusicController(this)
    }

    val repository: StudyRepository by lazy {
        StudyRepository(AppDatabase.getInstance(this).studyDao())
    }
}
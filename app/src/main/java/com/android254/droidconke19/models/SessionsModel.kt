package com.android254.droidconke19.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android254.droidconke19.R
import com.android254.droidconke19.repository.FavoritesStore
import com.android254.droidconke19.ui.filters.Filter
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

interface Filterable {
    fun isInFilter(filter: Filter, favoritesStore: FavoritesStore): Boolean
}

@Entity(tableName = "sessionsList")
@Parcelize
data class SessionsModel(
        @PrimaryKey
        var id: Int = 0,
        var speaker_id: ArrayList<Int> = ArrayList(),
        val stage: Stage = Stage.None,
        var room_id: Int = 0,
        var main_tag: String = "",
        var room: String = "",
        var speakers: ArrayList<String> = ArrayList(),
        var starred: String = "",
        var time: String = "",
        var title: String = "",
        var topic: String = "",
        var url: String = "",
        var duration: String = "",
        var description: String = "",
        var session_color: String = "",
        var topic_id: Int = 0,
        var type_id: Int = 0,
        var type: Type = Type.None,
        var documentId: String = "",
        var timestamp: String = "",
        var day_number: String = "",
        var time_in_am: String = "",
        var am_pm_label: String = "",
        var notification_slug: String = "",
        var photoUrl: String = "",
        val session_audience: Level = Level.None,
        var speakerList: ArrayList<SpeakersModel> = ArrayList(),
        val end_time_in_am: String = "",
        val start_status: String = ""

) : Parcelable, Filterable {
    override fun isInFilter(filter: Filter, favoritesStore: FavoritesStore): Boolean {
        return if (filter == Filter.empty()) {
            true
        } else {
            var result = true
            if (filter.isFavorites) {
                result = result && favoritesStore.isFavorite(this)
            }
            if (filter.stages.isNotEmpty()) {
                result = result && filter.stages.contains(stage)
            }
            if (filter.types.isNotEmpty()) {
                result = result && filter.types.contains(type)
            }
            if (filter.levels.isNotEmpty()) {
                result = result && filter.levels.contains(session_audience)
            }
            return result
        }
    }

    fun contains(query: String): Boolean {
        val text = query.toLowerCase(Locale.getDefault())
        var result = false

        result = result || title.toLowerCase(Locale.getDefault()).contains(text)

        if (speakers.isNotEmpty()) {
            val candidates = speakers.map { it.toLowerCase(Locale.getDefault()) }
            result = result || candidates.any { it.contains(text) }
        }

        result = result || description.toLowerCase(Locale.getDefault()).contains(text)

        return result
    }
}

enum class Stage(val value: String) {
    MainHall("Main Hall"),
    Room1("Room 1"),
    Room2("Room 2"),
    Room3("Room 3"),
    None("None")
}

enum class Type(val value: String, val resId: Int) {
    Session("Session", R.drawable.ic_outline_video_label),
    LightningTalk("Lightning talk", R.drawable.ic_outline_flash_on),
    Codelab("Codelab", R.drawable.ic_outline_build),
    PanelDiscussion("Panel discussion", R.drawable.ic_outline_question_answer),
    Keynote("Keynote", R.drawable.ic_outline_video_label),
    None("None", 0)
}

enum class Level(val resId: Int) {
    beginner(R.drawable.ic_outline_signal_cellular_one),
    intermediate(R.drawable.ic_outline_signal_cellular_two),
    advanced(R.drawable.ic_outline_signal_cellular_three),
    general(R.drawable.ic_outline_signal_cellular_three),
    None(0)
}


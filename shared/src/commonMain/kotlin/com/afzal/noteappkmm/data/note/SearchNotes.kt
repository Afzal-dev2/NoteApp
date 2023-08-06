package com.afzal.noteappkmm.data.note

import com.afzal.noteappkmm.domain.note.Note
import com.afzal.noteappkmm.domain.time.DateTimeUtil
import com.squareup.sqldelight.Query

class SearchNotes {
    fun execute(notes:List<Note>, query: String): List<Note>{
        if (query.isBlank()){
            return notes
        }
        return notes.filter {
            it.title.trim().lowercase().contains(query.lowercase())
        }.sortedBy { DateTimeUtil.toEpochMillis(it.created)
        }
    }
}
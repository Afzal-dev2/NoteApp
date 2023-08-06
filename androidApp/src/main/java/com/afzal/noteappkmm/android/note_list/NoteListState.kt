package com.afzal.noteappkmm.android.note_list

import com.afzal.noteappkmm.domain.note.Note

data class NoteListState(
    var notes: List<Note> = emptyList(),
    var searchText: String = "",
    var isSearchActive: Boolean = false
)

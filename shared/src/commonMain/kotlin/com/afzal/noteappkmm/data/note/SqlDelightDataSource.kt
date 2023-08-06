package com.afzal.noteappkmm.data.note

import com.afzal.noteappkmm.database.NoteDatabase
import com.afzal.noteappkmm.domain.note.NoteDataSource
import com.afzal.noteappkmm.domain.note.Note
import com.afzal.noteappkmm.domain.time.DateTimeUtil

class SqlDelightDataSource(db: NoteDatabase): NoteDataSource {
    private val queries = db.noteQueries
    override suspend fun insertNote(note: Note) {
        queries.insertNote(
            id = note.id, title = note.title, content = note.content, colorHex = note.colorHex,
            created = DateTimeUtil.toEpochMillis(note.created)
        )
    }

    override suspend fun getNoteById(id: Long): Note? {
        return queries
            .getNoteById(id)
            .executeAsOneOrNull()
            ?.toNote()
    }

    override suspend fun getAllNotes(): List<Note> {
        return queries
            .getAllNotes()
            .executeAsList()
            .map {it.toNote()}
    }

    override suspend fun deleteNoteById(id: Long) {
        queries.deleteNoteById(id)
    }
}
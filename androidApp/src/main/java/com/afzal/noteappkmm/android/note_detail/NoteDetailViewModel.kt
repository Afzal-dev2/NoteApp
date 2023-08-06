package com.afzal.noteappkmm.android.note_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afzal.noteappkmm.domain.note.Note
import com.afzal.noteappkmm.domain.note.NoteDataSource
import com.afzal.noteappkmm.domain.time.DateTimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val noteDataSource: NoteDataSource,
    private val savedStateHandle: SavedStateHandle
): ViewModel(){

    private val noteTitle = savedStateHandle.getStateFlow("noteTitle", "")
    private val isNoteTitleFocussed = savedStateHandle.getStateFlow("isNoteTitleFocussed", false)
    private val noteContent = savedStateHandle.getStateFlow("noteContent", "")
    private val isNoteContentFocussed = savedStateHandle.getStateFlow("isNoteContentFocussed", false)
    private val noteColor = savedStateHandle.getStateFlow(
        "noteColor",
        Note.generateRandomColor()
    )

    val state = combine(
        noteTitle,
        isNoteTitleFocussed,
        noteContent,
        isNoteContentFocussed,
        noteColor
    ){
        title, isTitleFocussed, content, isContentFocussed, color ->
        NoteDetailState(
            noteTitle = title,
            isNoteTitleHintVisible = title.isEmpty() && !isTitleFocussed,
            noteContent = content,
            isNoteContentHintVisible = content.isEmpty() && !isContentFocussed,
            noteColor = color

        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteDetailState())

    private val _hasNoteBeenSaved = MutableStateFlow(false)
    val hasNoteBeenSaved = _hasNoteBeenSaved.asStateFlow()

    private var existingNoteId : Long ?= null

    init {
        savedStateHandle.get<Long>("noteId")?.let { existingNoteId ->

            if (existingNoteId == -1L){
                return@let
            }
            this.existingNoteId = existingNoteId
            viewModelScope.launch {
                noteDataSource.getNoteById(existingNoteId)?.let { note->
                    savedStateHandle["noteTitle"] = note.title
                    savedStateHandle["noteContent"] = note.content
                    savedStateHandle["noteColor"] = note.colorHex
                }
            }
        }
    }
    fun onNoteTitleChanged(text: String){
        savedStateHandle["noteTitle"] = text
    }
    fun onNoteContentChanged(text: String){
        savedStateHandle["noteContent"] = text
    }
    fun onNoteTitleFocusChanged(isFocussed: Boolean){
        savedStateHandle["isNoteTitleFocussed"] = isFocussed
    }
    fun onNoteContentFocusChanged(isFocussed: Boolean){
        savedStateHandle["isNoteContentFocussed"] = isFocussed
    }

    fun saveNote(){
        viewModelScope.launch {
            noteDataSource.insertNote(
                Note(
                    id = existingNoteId,
                    title = noteTitle.value,
                    content = noteContent.value,
                    colorHex = noteColor.value,
                    created = DateTimeUtil.now()
                )
            )
            _hasNoteBeenSaved.value = true
        }
    }
}
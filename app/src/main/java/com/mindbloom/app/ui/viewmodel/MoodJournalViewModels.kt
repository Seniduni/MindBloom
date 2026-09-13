package com.mindbloom.app.ui.viewmodel

import androidx.compose.foundation.layout.size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindbloom.app.data.Mood
import com.mindbloom.app.data.local.JournalEntity
import com.mindbloom.app.data.repository.JournalRepository
import com.mindbloom.app.data.repository.MoodRepository
import com.mindbloom.app.util.DateUtils
import java.time.YearMonth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/* ================================== Mood ================================= */

data class MoodUiState(
    val selectedMood: Mood? = null,
    val note: String = "",
    val month: YearMonth = YearMonth.now(),
    val monthMoods: Map<Int, Mood> = emptyMap(),
    val saved: Boolean = false,
    val error: String? = null
) {
    val noteLength: Int get() = note.length
}

class MoodViewModel(private val moodRepository: MoodRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(MoodUiState())
    val uiState: StateFlow<MoodUiState> = _uiState.asStateFlow()

    private val _month = MutableStateFlow(YearMonth.now())

    init {
        // Load whatever was already logged for today so the screen reopens
        // in the state the user left it in.
        viewModelScope.launch {
            moodRepository.observeForDate().collect { entry ->
                if (entry != null && _uiState.value.selectedMood == null) {
                    _uiState.update {
                        it.copy(selectedMood = Mood.fromLabel(entry.mood), note = entry.note)
                    }
                }
            }
        }
        viewModelScope.launch {
            @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
            _month.flatMapLatest { month -> moodRepository.observeMonth(month) }
                .collect { moods ->
                    _uiState.update { it.copy(month = _month.value, monthMoods = moods) }
                }
        }
    }

    fun selectMood(mood: Mood) =
        _uiState.update { it.copy(selectedMood = mood, error = null) }

    fun updateNote(value: String) {
        if (value.length <= NOTE_LIMIT) _uiState.update { it.copy(note = value) }
    }

    fun previousMonth() {
        _month.value = _month.value.minusMonths(1)
    }

    fun nextMonth() {
        val next = _month.value.plusMonths(1)
        if (!next.isAfter(YearMonth.now())) _month.value = next
    }

    fun save(onSaved: () -> Unit) {
        val mood = _uiState.value.selectedMood
        if (mood == null) {
            _uiState.update { it.copy(error = "Pick how you are feeling first") }
            return
        }
        viewModelScope.launch {
            moodRepository.saveMood(DateUtils.todayIso(), mood, _uiState.value.note)
            _uiState.update { it.copy(saved = true, error = null) }
            onSaved()
        }
    }

    fun consumeSaved() = _uiState.update { it.copy(saved = false) }

    companion object {
        const val NOTE_LIMIT = 200
    }
}

/* ================================= Journal =============================== */

data class JournalUiState(
    val entryId: Long = 0,
    val content: String = "",
    val mood: Mood = Mood.CALM,
    val photos: List<String> = emptyList(),
    val recentEntries: List<JournalEntity> = emptyList(),
    val saved: Boolean = false,
    val error: String? = null,
    val loading: Boolean = true
) {
    val characterCount: Int get() = content.length
}

class JournalViewModel(private val journalRepository: JournalRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()

    val allEntries: StateFlow<List<JournalEntity>> = journalRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            journalRepository.observeAll().collect { entries ->
                val today = DateUtils.todayIso()
                val todaysEntry = entries.firstOrNull { it.date == today }
                _uiState.update { state ->
                    state.copy(
                        entryId = todaysEntry?.id ?: 0,
                        content = if (state.loading) todaysEntry?.content.orEmpty() else state.content,
                        mood = if (state.loading) {
                            Mood.fromLabel(todaysEntry?.mood) ?: Mood.CALM
                        } else {
                            state.mood
                        },
                        photos = if (state.loading) {
                            todaysEntry?.photos.orEmpty().split(",").filter { it.isNotBlank() }
                        } else {
                            state.photos
                        },
                        recentEntries = entries.filter { it.date != today }.take(5),
                        loading = false
                    )
                }
            }
        }
    }

    fun updateContent(value: String) {
        if (value.length <= CONTENT_LIMIT) {
            _uiState.update { it.copy(content = value, error = null) }
        }
    }

    fun updateMood(mood: Mood) = _uiState.update { it.copy(mood = mood) }

    fun addPhoto(tag: String) = _uiState.update {
        if (it.photos.size >= 6) it else it.copy(photos = it.photos + tag)
    }

    fun removePhoto(index: Int) = _uiState.update {
        it.copy(photos = it.photos.filterIndexed { i, _ -> i != index })
    }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        if (state.content.isBlank()) {
            _uiState.update { it.copy(error = "Write something before saving") }
            return
        }
        viewModelScope.launch {
            journalRepository.save(
                JournalEntity(
                    id = state.entryId,
                    date = DateUtils.todayIso(),
                    content = state.content.trim(),
                    mood = state.mood.label,
                    photos = state.photos.joinToString(",")
                )
            )
            _uiState.update { it.copy(saved = true, error = null) }
            onSaved()
        }
    }

    fun delete(id: Long) = viewModelScope.launch { journalRepository.delete(id) }

    fun consumeSaved() = _uiState.update { it.copy(saved = false) }

    companion object {
        const val CONTENT_LIMIT = 1000
    }
}

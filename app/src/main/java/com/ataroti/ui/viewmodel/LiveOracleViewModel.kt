package com.ataroti.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ataroti.data.db.CardEntity
import com.ataroti.data.db.PlacedCardEntity
import com.ataroti.data.model.Mode
import com.ataroti.data.model.Orientation
import com.ataroti.data.repo.AtarotiRepository
import com.ataroti.domain.SummaryEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LiveOracleViewModel(private val repo: AtarotiRepository) : ViewModel() {
    private var sessionId: Long? = null
    private val _cards = MutableStateFlow<List<CardEntity>>(emptyList())
    val cards: StateFlow<List<CardEntity>> = _cards.asStateFlow()
    private val _placed = MutableStateFlow<List<PlacedCardEntity>>(emptyList())
    val placed: StateFlow<List<PlacedCardEntity>> = _placed.asStateFlow()
    private val _summary = MutableStateFlow<List<String>>(emptyList())
    val summary: StateFlow<List<String>> = _summary.asStateFlow()

    private var allCards: List<CardEntity> = emptyList()

    init {
        viewModelScope.launch {
            sessionId = repo.createSession(Mode.LIVE_ORACLE, "Live Oracle")
            repo.cards().collect {
                allCards = it
                _cards.value = it
            }
        }
        viewModelScope.launch {
            while (sessionId == null) kotlinx.coroutines.delay(30)
            repo.placedCards(sessionId!!).collect {
                _placed.value = it
                _summary.value = SummaryEngine.summarize(it)
            }
        }
    }

    fun setQuery(value: String) {
        _cards.value = if (value.isBlank()) allCards else allCards.filter { it.title.contains(value, true) || it.kind.contains(value, true) }
    }

    fun addCard(card: CardEntity, orientation: Orientation, xNorm: Float, yNorm: Float, parentPlacedId: Long? = null) {
        val sid = sessionId ?: return
        viewModelScope.launch {
            repo.placeCard(PlacedCardEntity(sessionId = sid, cardId = card.cardId, title = card.title, orientation = orientation, xNorm = xNorm, yNorm = yNorm, parentPlacedId = parentPlacedId))
        }
    }

    fun updateCard(card: PlacedCardEntity) = viewModelScope.launch { repo.updatePlacedCard(card) }
}

class LiveOracleViewModelFactory(private val repo: AtarotiRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = LiveOracleViewModel(repo) as T
}

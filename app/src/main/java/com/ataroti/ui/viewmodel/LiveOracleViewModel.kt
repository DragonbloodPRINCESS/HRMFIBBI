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
    private val sessionId = MutableStateFlow<Long?>(null)
    private var query: String = ""
    private var allCards: List<CardEntity> = emptyList()

    private val _cards = MutableStateFlow<List<CardEntity>>(emptyList())
    val cards: StateFlow<List<CardEntity>> = _cards.asStateFlow()

    private val _placed = MutableStateFlow<List<PlacedCardEntity>>(emptyList())
    val placed: StateFlow<List<PlacedCardEntity>> = _placed.asStateFlow()

    private val _summary = MutableStateFlow<List<String>>(emptyList())
    val summary: StateFlow<List<String>> = _summary.asStateFlow()

    init {
        viewModelScope.launch {
            sessionId.value = repo.createSession(Mode.LIVE_ORACLE, "Live Oracle")
        }
        viewModelScope.launch {
            repo.cards().collect {
                allCards = it
                _cards.value = applyFilter(it, query)
            }
        }
        viewModelScope.launch {
            sessionId.collect { id ->
                id ?: return@collect
                repo.placedCards(id).collect {
                    _placed.value = it
                    _summary.value = SummaryEngine.summarize(it)
                }
            }
        }
    }

    fun setQuery(value: String) {
        query = value
        _cards.value = applyFilter(allCards, query)
    }

    private fun applyFilter(source: List<CardEntity>, query: String): List<CardEntity> {
        if (query.isBlank()) return source
        return source.filter {
            it.title.contains(query, true) ||
                it.kind.contains(query, true) ||
                (it.suit?.contains(query, true) == true)
        }
    }

    fun addCard(card: CardEntity, orientation: Orientation, xNorm: Float, yNorm: Float, parentPlacedId: Long? = null) {
        val sid = sessionId.value ?: return
        viewModelScope.launch {
            repo.placeCard(
                PlacedCardEntity(
                    sessionId = sid,
                    cardId = card.cardId,
                    title = card.title,
                    orientation = orientation,
                    xNorm = xNorm.coerceIn(0f, 1f),
                    yNorm = yNorm.coerceIn(0f, 1f),
                    parentPlacedId = parentPlacedId
                )
            )
        }
    }

    fun updateCard(card: PlacedCardEntity) = viewModelScope.launch { repo.updatePlacedCard(card) }
}

class LiveOracleViewModelFactory(private val repo: AtarotiRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = LiveOracleViewModel(repo) as T
}

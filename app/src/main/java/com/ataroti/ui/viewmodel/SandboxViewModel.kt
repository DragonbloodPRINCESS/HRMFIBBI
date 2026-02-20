package com.ataroti.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ataroti.data.db.CardEntity
import com.ataroti.data.db.PlacedCardEntity
import com.ataroti.data.db.SessionEventEntity
import com.ataroti.data.model.ChaosLevel
import com.ataroti.data.model.EventType
import com.ataroti.data.model.Mode
import com.ataroti.data.model.Orientation
import com.ataroti.data.repo.AtarotiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class SandboxViewModel(private val repo: AtarotiRepository) : ViewModel() {
    private val sessionId = MutableStateFlow<Long?>(null)
    val chaos = MutableStateFlow(ChaosLevel.MINIMAL)

    val deck = repo.cards().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val _canvas = MutableStateFlow<List<PlacedCardEntity>>(emptyList())
    val canvas: StateFlow<List<PlacedCardEntity>> = _canvas

    init { viewModelScope.launch { sessionId.value = repo.createSession(Mode.SANDBOX, "Sandbox") } }

    fun shuffle() { _canvas.value = _canvas.value.shuffled() }
    fun reshuffle() { _canvas.value = emptyList() }

    fun drawTop() = drawRandom("top")
    fun drawMiddle() = drawRandom("middle")

    private fun drawRandom(from: String) {
        val sid = sessionId.value ?: return
        val source = deck.value
        if (source.isEmpty()) return
        viewModelScope.launch {
            val card = source.random()
            addCardToCanvas(sid, card, Random.nextFloat(), Random.nextFloat())
            maybeChaosEvent(sid)
        }
    }

    private suspend fun addCardToCanvas(sid: Long, card: CardEntity, x: Float, y: Float) {
        val placed = PlacedCardEntity(sessionId = sid, cardId = card.cardId, title = card.title, orientation = Orientation.UPRIGHT, xNorm = x, yNorm = y)
        val id = repo.placeCard(placed)
        _canvas.value = _canvas.value + placed.copy(id = id)
    }

    private suspend fun maybeChaosEvent(sid: Long) {
        val p = when (chaos.value) { ChaosLevel.MINIMAL -> 0; ChaosLevel.MODERATE -> 20; ChaosLevel.WILD -> 45 }
        if (Random.nextInt(100) > p) return
        val event = listOf(EventType.JUMP_OUT_PAIR, EventType.DOUBLE_PULL, EventType.SHADOW_UNDER).random()
        repo.addEvent(SessionEventEntity(sessionId = sid, eventType = event, payload = "{}"))
    }
}

class SandboxViewModelFactory(private val repo: AtarotiRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = SandboxViewModel(repo) as T
}

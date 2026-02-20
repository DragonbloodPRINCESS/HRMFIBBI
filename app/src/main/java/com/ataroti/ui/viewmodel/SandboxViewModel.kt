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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class SandboxViewModel(private val repo: AtarotiRepository) : ViewModel() {
    private var sessionId: Long? = null
    val chaos = MutableStateFlow(ChaosLevel.MINIMAL)

    val deck = repo.cards().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val _canvas = MutableStateFlow<List<PlacedCardEntity>>(emptyList())
    val canvas: StateFlow<List<PlacedCardEntity>> = _canvas.asStateFlow()

    init {
        viewModelScope.launch {
            sessionId = repo.createSession(Mode.SANDBOX, "Sandbox")
        }
    }

    fun shuffle() {
        _canvas.value = _canvas.value.shuffled()
    }

    fun reshuffle() {
        _canvas.value = emptyList()
    }

    fun returnCard(placedCardId: Long) {
        _canvas.value = _canvas.value.filterNot { it.id == placedCardId }
    }

    fun drawTop() = draw(source = "top", preferredIndex = 0)
    fun drawMiddle() = draw(source = "middle", preferredIndex = deck.value.size / 2)
    fun browseDeckPick(card: CardEntity) = placeCard(card)

    private fun draw(source: String, preferredIndex: Int) {
        val cards = deck.value
        if (cards.isEmpty()) return

        val seedCard = cards[preferredIndex.coerceIn(0, cards.lastIndex)]
        placeCard(seedCard)
        handleChaos(source)
    }

    private fun placeCard(card: CardEntity, parentPlacedId: Long? = null) {
        val sid = sessionId ?: return
        viewModelScope.launch {
            val x = Random.nextFloat().coerceIn(0.08f, 0.92f)
            val y = Random.nextFloat().coerceIn(0.10f, 0.88f)
            val placed = PlacedCardEntity(
                sessionId = sid,
                cardId = card.cardId,
                title = card.title,
                orientation = Orientation.UPRIGHT,
                xNorm = x,
                yNorm = y,
                parentPlacedId = parentPlacedId
            )
            val id = repo.placeCard(placed)
            _canvas.value = _canvas.value + placed.copy(id = id)
        }
    }

    private fun handleChaos(source: String) {
        val sid = sessionId ?: return
        val chance = when (chaos.value) {
            ChaosLevel.MINIMAL -> 0
            ChaosLevel.MODERATE -> 20
            ChaosLevel.WILD -> 45
        }
        if (Random.nextInt(100) >= chance) return

        val cards = deck.value
        if (cards.isEmpty()) return

        val event = listOf(EventType.JUMP_OUT_PAIR, EventType.DOUBLE_PULL, EventType.SHADOW_UNDER).random()
        viewModelScope.launch {
            repo.addEvent(SessionEventEntity(sessionId = sid, eventType = event, payload = "{\"source\":\"$source\"}"))
        }

        when (event) {
            EventType.JUMP_OUT_PAIR -> {
                placeCard(cards.random())
                placeCard(cards.random())
            }
            EventType.DOUBLE_PULL -> placeCard(cards.random())
            EventType.SHADOW_UNDER -> {
                val parent = _canvas.value.lastOrNull() ?: return
                placeCard(cards.random(), parentPlacedId = parent.id)
            }
        }
    }
}

class SandboxViewModelFactory(private val repo: AtarotiRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = SandboxViewModel(repo) as T
}

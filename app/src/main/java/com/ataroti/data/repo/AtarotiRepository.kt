package com.ataroti.data.repo

import com.ataroti.data.db.AppDatabase
import com.ataroti.data.db.CardEntity
import com.ataroti.data.db.NoteEntity
import com.ataroti.data.db.PlacedCardEntity
import com.ataroti.data.db.RwsSeed
import com.ataroti.data.db.SessionEntity
import com.ataroti.data.db.SessionEventEntity
import com.ataroti.data.model.Mode
import kotlinx.coroutines.flow.Flow

class AtarotiRepository(private val db: AppDatabase) {
    private val deckDao = db.deckDao()
    private val sessionDao = db.sessionDao()

    suspend fun ensureSeeded() {
        if (deckDao.countCards() == 0) {
            deckDao.insertDecks(listOf(RwsSeed.deck()))
            deckDao.insertCards(RwsSeed.cards())
        }
    }

    fun cards(deckId: String = "rws"): Flow<List<CardEntity>> = deckDao.cardsByDeck(deckId)
    fun searchCards(query: String, deckId: String = "rws") = deckDao.searchCards(deckId, query)

    suspend fun createSession(mode: Mode, title: String): Long = sessionDao.insertSession(SessionEntity(mode = mode, title = title))

    fun sessions() = sessionDao.sessions()
    fun placedCards(sessionId: Long) = sessionDao.placedCards(sessionId)

    suspend fun placeCard(card: PlacedCardEntity): Long = sessionDao.insertPlacedCard(card)
    suspend fun updatePlacedCard(card: PlacedCardEntity) = sessionDao.updatePlacedCard(card)
    suspend fun addNote(sessionId: Long, placedCardId: Long?, text: String) = sessionDao.insertNote(NoteEntity(sessionId = sessionId, placedCardId = placedCardId, text = text))
    suspend fun addEvent(event: SessionEventEntity) = sessionDao.insertEvent(event)
}

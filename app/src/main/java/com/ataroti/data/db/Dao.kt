package com.ataroti.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDecks(decks: List<DeckEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<CardEntity>)

    @Query("SELECT * FROM cards WHERE deckId = :deckId ORDER BY cardId")
    fun cardsByDeck(deckId: String): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE deckId = :deckId AND (title LIKE '%' || :query || '%' OR kind LIKE '%' || :query || '%') ORDER BY cardId")
    fun searchCards(deckId: String, query: String): Flow<List<CardEntity>>

    @Query("SELECT COUNT(*) FROM cards")
    suspend fun countCards(): Int
}

@Dao
interface SessionDao {
    @Insert suspend fun insertSession(session: SessionEntity): Long
    @Update suspend fun updateSession(session: SessionEntity)
    @Insert suspend fun insertPlacedCard(card: PlacedCardEntity): Long
    @Update suspend fun updatePlacedCard(card: PlacedCardEntity)
    @Insert suspend fun insertNote(note: NoteEntity)
    @Insert suspend fun insertEvent(event: SessionEventEntity)

    @Query("SELECT * FROM sessions ORDER BY updatedAt DESC")
    fun sessions(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM placed_cards WHERE sessionId = :sessionId ORDER BY createdAt")
    fun placedCards(sessionId: Long): Flow<List<PlacedCardEntity>>
}

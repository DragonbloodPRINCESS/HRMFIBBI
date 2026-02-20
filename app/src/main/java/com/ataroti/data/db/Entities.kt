package com.ataroti.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ataroti.data.model.EventType
import com.ataroti.data.model.Mode
import com.ataroti.data.model.Orientation

@Entity(tableName = "decks")
data class DeckEntity(
    @PrimaryKey val id: String,
    val name: String,
    val isPremiumSkin: Boolean = false,
    val baseDeckId: String? = null
)

@Entity(tableName = "cards", indices = [Index("deckId"), Index("cardId")])
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deckId: String,
    val cardId: Int,
    val title: String,
    val kind: String,
    val suit: String?,
    val rank: String?,
    val imageAsset: String
)

@Entity(
    tableName = "deck_assets",
    foreignKeys = [ForeignKey(entity = DeckEntity::class, parentColumns = ["id"], childColumns = ["deckId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("deckId")]
)
data class DeckAssetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deckId: String,
    val cardId: Int,
    val imageAsset: String
)

@Entity(tableName = "sessions", indices = [Index("createdAt")])
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mode: Mode,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "placed_cards",
    foreignKeys = [ForeignKey(entity = SessionEntity::class, parentColumns = ["id"], childColumns = ["sessionId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("sessionId"), Index("parentPlacedId"), Index("createdAt")]
)
data class PlacedCardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val cardId: Int,
    val title: String,
    val orientation: Orientation,
    val rotationDeg: Float = 0f,
    val scale: Float = 1f,
    val xNorm: Float,
    val yNorm: Float,
    val parentPlacedId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "notes",
    indices = [Index("sessionId"), Index("placedCardId")]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val placedCardId: Long? = null,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "session_events", indices = [Index("sessionId")])
data class SessionEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val eventType: EventType,
    val payload: String,
    val createdAt: Long = System.currentTimeMillis()
)

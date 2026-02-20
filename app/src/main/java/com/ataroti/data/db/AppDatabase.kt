package com.ataroti.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.ataroti.data.model.EventType
import com.ataroti.data.model.Mode
import com.ataroti.data.model.Orientation

class AppConverters {
    @TypeConverter fun modeToString(value: Mode) = value.name
    @TypeConverter fun stringToMode(value: String) = Mode.valueOf(value)
    @TypeConverter fun orientationToString(value: Orientation) = value.name
    @TypeConverter fun stringToOrientation(value: String) = Orientation.valueOf(value)
    @TypeConverter fun eventTypeToString(value: EventType) = value.name
    @TypeConverter fun stringToEventType(value: String) = EventType.valueOf(value)
}

@Database(
    entities = [DeckEntity::class, CardEntity::class, DeckAssetEntity::class, SessionEntity::class, PlacedCardEntity::class, NoteEntity::class, SessionEventEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(AppConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(context, AppDatabase::class.java, "ataroti.db").build()
                INSTANCE = db
                db
            }
        }
    }
}

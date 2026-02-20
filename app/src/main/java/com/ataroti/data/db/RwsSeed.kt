package com.ataroti.data.db

object RwsSeed {
    fun deck() = DeckEntity(id = "rws", name = "Rider–Waite–Smith")

    fun cards(): List<CardEntity> {
        val majors = listOf(
            "The Fool", "The Magician", "The High Priestess", "The Empress", "The Emperor", "The Hierophant", "The Lovers", "The Chariot", "Strength", "The Hermit", "Wheel of Fortune", "Justice", "The Hanged Man", "Death", "Temperance", "The Devil", "The Tower", "The Star", "The Moon", "The Sun", "Judgement", "The World"
        ).mapIndexed { idx, title -> CardEntity(deckId = "rws", cardId = idx, title = title, kind = "MAJOR", suit = null, rank = idx.toString(), imageAsset = "rws_${idx}.webp") }

        val suits = listOf("WANDS", "CUPS", "SWORDS", "PENTACLES")
        val ranks = listOf("Ace", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Page", "Knight", "Queen", "King")
        var cardId = 22
        val minors = buildList {
            suits.forEach { suit ->
                ranks.forEach { rank ->
                    add(CardEntity(deckId = "rws", cardId = cardId++, title = "$rank of ${suit.lowercase().replaceFirstChar { it.uppercase() }}", kind = suit, suit = suit, rank = rank, imageAsset = "rws_${cardId}.webp"))
                }
            }
        }
        return majors + minors
    }
}

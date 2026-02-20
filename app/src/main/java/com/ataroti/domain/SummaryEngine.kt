package com.ataroti.domain

import com.ataroti.data.db.PlacedCardEntity

object SummaryEngine {
    fun summarize(cards: List<PlacedCardEntity>): List<String> {
        if (cards.isEmpty()) return listOf("No cards yet. Add cards to generate an objective summary.")
        val majors = cards.count { it.cardId in 0..21 }
        val suits = cards.groupingBy { suitFromCardId(it.cardId) }.eachCount().filterKeys { it != null }
        val dominantSuit = suits.maxByOrNull { it.value }?.key

        val bullets = mutableListOf<String>()
        bullets += "Majors: $majors/${cards.size} (${(majors * 100 / cards.size)}%)"
        dominantSuit?.let { bullets += "Dominant suit: $it (${suits[it]} cards)" }
        val pairs = iconicPairs(cards.map { it.cardId }.toSet())
        if (pairs.isNotEmpty()) bullets += "Notable pair(s): ${pairs.joinToString()}"
        return bullets.take(3)
    }

    private fun suitFromCardId(cardId: Int): String? = when (cardId) {
        in 22..35 -> "WANDS"
        in 36..49 -> "CUPS"
        in 50..63 -> "SWORDS"
        in 64..77 -> "PENTACLES"
        else -> null
    }

    private fun iconicPairs(cardIds: Set<Int>): List<String> {
        val pairs = mutableListOf<String>()
        if (cardIds.containsAll(setOf(16, 17))) pairs += "Tower + Star"
        if (cardIds.containsAll(setOf(18, 19))) pairs += "Moon + Sun"
        if (cardIds.containsAll(setOf(1, 2))) pairs += "Magician + High Priestess"
        return pairs
    }
}

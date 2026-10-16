package io.github.supermonster003.autojs6.plugin.batch.rename.engine.pinyin

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PinyinCase
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PinyinStyle
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PinyinTone
import java.text.Normalizer

/**
 * Chinese characters to pinyin (roadmap D35). Readings come from the longest phrase that starts at a character, looked up
 * with traditional characters simplified, and otherwise from the character's customary reading; text without a reading
 * stays as it is. The separator goes only between consecutive syllables.
 * zh-CN: 汉字转拼音 (路线图 D35). 读音取自从该字开始的最长词组 (查找时繁体先转简体), 否则取该字的常用读音; 没有读音的文本保持原样.
 * 分隔符只放在相邻音节之间.
 */
internal class PinyinConverter(private val dictionary: PinyinDictionary) {

    fun convert(text: String, style: PinyinStyle): String {
        val codePoints = text.codePoints().toArray()
        val simplified = IntArray(codePoints.size) { dictionary.simplify(codePoints[it]) }
        val result = StringBuilder(text.length * 3)
        var afterSyllable = false
        var index = 0
        while (index < codePoints.size) {
            val reading = dictionary.reading(codePoints[index]) ?: dictionary.reading(simplified[index])
            if (reading == null) {
                result.appendCodePoint(codePoints[index])
                afterSyllable = false
                index++
                continue
            }
            val phrase = longestPhrase(codePoints, simplified, index)
            val syllables = phrase ?: listOf(reading)
            syllables.forEach { syllable ->
                if (afterSyllable) result.append(style.separator.text)
                result.append(format(syllable, style))
                afterSyllable = true
            }
            index += syllables.size
        }
        return result.toString()
    }

    private fun longestPhrase(codePoints: IntArray, simplified: IntArray, start: Int): List<String>? {
        var end = start
        while (end < codePoints.size && end - start < dictionary.maxPhraseLength &&
            (dictionary.reading(codePoints[end]) != null || dictionary.reading(simplified[end]) != null)
        ) end++
        for (length in end - start downTo 2) {
            val key = StringBuilder(length)
            for (offset in 0 until length) key.appendCodePoint(simplified[start + offset])
            dictionary.phrase(key.toString())?.let { return it }
        }
        return null
    }

    companion object {

        val default: PinyinConverter by lazy { PinyinConverter(PinyinDictionary.default) }

        /** Combining macron, acute, caron and grave: tones 1 to 4. zh-CN: 组合用长音符, 锐音符, 抑扬符与重音符: 第 1 至 4 声. */
        private val COMBINING_TONES = intArrayOf(0x0304, 0x0301, 0x030c, 0x0300)
        private const val COMBINING_DIAERESIS = 0x0308

        /**
         * One syllable of the dictionary (tone marks, `ü`, `ê`, `ń`, `ḿ`) in [style]. Without tone marks `ü` is written `v`
         * and `ê` is written `e`, so names stay ASCII; the neutral tone has no number.
         * zh-CN: 按 [style] 书写词典中的一个音节 (声调符号, `ü`, `ê`, `ń`, `ḿ`). 不带声调符号时 `ü` 写作 `v`, `ê` 写作 `e`,
         * 使名称保持 ASCII; 轻声不加数字.
         */
        fun format(syllable: String, style: PinyinStyle): String {
            val plain = StringBuilder(syllable.length + 1)
            var tone = 0
            Normalizer.normalize(syllable, Normalizer.Form.NFD).forEach { char ->
                val combining = COMBINING_TONES.indexOf(char.code)
                when {
                    combining >= 0 -> tone = combining + 1
                    char.code == COMBINING_DIAERESIS -> if (plain.endsWith("u")) plain.setCharAt(plain.length - 1, 'v')
                    Character.getType(char) == Character.NON_SPACING_MARK.toInt() -> Unit
                    else -> plain.append(char)
                }
            }
            val ascii = plain.toString()
            val written = when {
                style.initials -> ascii.take(1)
                style.tone == PinyinTone.MARK -> syllable
                style.tone == PinyinTone.NUMBER && tone > 0 -> "$ascii$tone"
                else -> ascii
            }
            return if (style.case == PinyinCase.CAPITALIZE) written.replaceFirstChar(Char::uppercaseChar) else written
        }
    }
}

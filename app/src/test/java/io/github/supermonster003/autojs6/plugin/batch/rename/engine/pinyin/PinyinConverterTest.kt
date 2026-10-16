package io.github.supermonster003.autojs6.plugin.batch.rename.engine.pinyin

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PinyinCase
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PinyinSeparator
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PinyinStyle
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PinyinTone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The built-in dictionary and the pinyin styles (roadmap D35 / P6.1): polyphonic characters by phrase, traditional
 * characters, every style option and text without a reading.
 * zh-CN: 内置词典与拼音样式 (路线图 D35 / P6.1): 多音字按词组判断, 繁体字, 各样式选项以及没有读音的文本.
 */
class PinyinConverterTest {

    private val converter = PinyinConverter.default

    private fun convert(text: String, style: PinyinStyle = SPACED) = converter.convert(text, style)

    @Test
    fun `polyphonic characters follow their phrase`() {
        mapOf(
            "重庆" to "chong qing", "重要" to "zhong yao", "重新" to "chong xin", "银行" to "yin hang", "行长" to "hang zhang",
            "行走" to "xing zou", "长大" to "zhang da", "长城" to "chang cheng", "音乐" to "yin yue", "快乐" to "kuai le",
            "还有" to "hai you", "还款" to "huan kuan", "会计" to "kuai ji", "目的" to "mu di", "的确" to "di que",
            "银行卡" to "yin hang ka", "董事长" to "dong shi zhang", "我的照片" to "wo de zhao pian", "相片" to "xiang pian",
        ).forEach { (text, expected) -> assertEquals(text, expected, convert(text)) }
    }

    @Test
    fun `traditional characters are converted like simplified ones`() {
        mapOf("重慶" to "chong qing", "銀行" to "yin hang", "音樂" to "yin yue", "長城" to "chang cheng", "會計" to "kuai ji", "乾隆" to "qian long")
            .forEach { (text, expected) -> assertEquals(text, expected, convert(text)) }
    }

    @Test
    fun `the default style is lowercase without tones or separators`() {
        assertEquals("chongqing", convert("重庆", PinyinStyle()))
        assertEquals("nver", convert("女儿", PinyinStyle()))
        assertEquals("lvse", convert("绿色", PinyinStyle()))
    }

    @Test
    fun `tones are written as marks or numbers`() {
        assertEquals("chóng qìng", convert("重庆", SPACED.copy(tone = PinyinTone.MARK)))
        assertEquals("lǜ sè", convert("绿色", SPACED.copy(tone = PinyinTone.MARK)))
        assertEquals("chong2_qing4", convert("重庆", PinyinStyle(PinyinTone.NUMBER, PinyinSeparator.UNDERSCORE)))
        assertEquals("lve4", convert("略", PinyinStyle(PinyinTone.NUMBER)))
        // The neutral tone has no number. zh-CN: 轻声不加数字.
        assertEquals("wo3-de", convert("我的", PinyinStyle(PinyinTone.NUMBER, PinyinSeparator.HYPHEN)))
        assertEquals(listOf("n", "ń", "n2"), PinyinTone.entries.map { convert("嗯", PinyinStyle(it)) })
    }

    @Test
    fun `separators, case and initials`() {
        assertEquals("zhang-san", convert("张三", PinyinStyle(separator = PinyinSeparator.HYPHEN)))
        assertEquals("Zhang San", convert("张三", SPACED.copy(case = PinyinCase.CAPITALIZE)))
        assertEquals("Ōu_Yáng", convert("欧阳", PinyinStyle(PinyinTone.MARK, PinyinSeparator.UNDERSCORE, PinyinCase.CAPITALIZE)))
        assertEquals("yhk", convert("银行卡", PinyinStyle(initials = true)))
        assertEquals("Y H K", convert("银行卡", PinyinStyle(PinyinTone.MARK, PinyinSeparator.SPACE, PinyinCase.CAPITALIZE, initials = true)))
    }

    @Test
    fun `text without a reading stays and separates nothing`() {
        val style = PinyinStyle(separator = PinyinSeparator.UNDERSCORE)
        assertEquals("IMG_zhao_pian2024 (1).jpg", convert("IMG_照片2024 (1).jpg", style))
        assertEquals("zhao_pian shi_pin", convert("照片 视频", style))
        val emoji = String(Character.toChars(0x1f600))
        assertEquals("ni_hao, World! $emoji", convert("你好, World! $emoji", style))
        assertEquals("", convert("", style))
    }

    @Test
    fun `characters outside the basic plane are converted`() {
        // U+20000 (hē). zh-CN: 扩展 B 区的 U+20000 (hē).
        assertEquals("he a", convert(String(Character.toChars(0x20000)) + "啊"))
    }

    @Test
    fun `the dictionary carries phrases of several lengths`() {
        assertTrue(PinyinDictionary.default.maxPhraseLength in 4..16)
    }

    private companion object {
        val SPACED = PinyinStyle(separator = PinyinSeparator.SPACE)
    }
}

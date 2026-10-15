package io.github.supermonster003.autojs6.plugin.batch.rename.engine.validate

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.PlanReason
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * The name rules of roadmap D19 and the UTF-8 byte budget of ext4 / f2fs.
 * zh-CN: 路线图 D19 的名称规则与 ext4 / f2fs 的 UTF-8 字节预算.
 */
class NameValidatorTest {

    @Test
    fun `every rule has its reason`() {
        assertEquals(PlanReason.EMPTY, NameValidator.validate("", false))
        assertEquals(PlanReason.DOT_NAME, NameValidator.validate(".", false))
        assertEquals(PlanReason.DOT_NAME, NameValidator.validate("..", false))
        assertEquals(PlanReason.SEPARATOR, NameValidator.validate("a/b", false))
        assertEquals(PlanReason.SEPARATOR, NameValidator.validate("a\u0000b", false))
        assertEquals(PlanReason.TOO_LONG, NameValidator.validate("a".repeat(256), false))
        assertEquals(PlanReason.RESTRICTED_CHARACTER, NameValidator.validate("a?b", true))
        assertEquals(PlanReason.RESTRICTED_CHARACTER, NameValidator.validate("a\tb", true))
        assertNull(NameValidator.validate("a?b", false))
        assertNull(NameValidator.validate("...", false))
        assertNull(NameValidator.validate(".hidden", true))
        assertNull(NameValidator.validate("a".repeat(255), false))
    }

    @Test
    fun `utf-8 length counts code points like the kernel`() {
        assertEquals(1, NameValidator.utf8Length("a"))
        assertEquals(2, NameValidator.utf8Length("\u00e9"))
        assertEquals(3, NameValidator.utf8Length("\u7167"))
        assertEquals(4, NameValidator.utf8Length("\ud83d\ude00"))
        assertEquals(3, NameValidator.utf8Length("\ud83d"))
        assertEquals(255, NameValidator.utf8Length("\u7167".repeat(85)))
        assertNull(NameValidator.validate("\ud83d\ude00".repeat(63) + "abc", false))
        assertEquals(PlanReason.TOO_LONG, NameValidator.validate("\ud83d\ude00".repeat(64), false))
    }
}

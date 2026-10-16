package io.github.supermonster003.autojs6.plugin.batch.rename.engine.variables

import io.github.supermonster003.autojs6.plugin.batch.rename.engine.EngineFixtures
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameOptions
import io.github.supermonster003.autojs6.plugin.batch.rename.engine.RenameTarget
import org.autojs.plugin.batchrename.api.BatchRenameValues
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

/**
 * The photo metadata variables (roadmap D34, D47-D50): parsing before the date variables, the formats, the missing
 * flag and the date taken fallback.
 * zh-CN: 照片元数据变量 (路线图 D34, D47-D50): 先于日期变量解析, 各种格式, 缺失标记与拍摄时间的回退.
 */
class MetadataVariablesTest {

    private val modified = LocalDateTime.of(2020, 11, 2, 13, 5, 9)

    private val full = mapOf<String, Any>(
        "cameraMake" to "Canon", "cameraModel" to "EOS R5/C", "lens" to "RF24-105mm", "iso" to 400, "fNumber" to 2.8,
        "exposureTime" to 0.004, "focalLength" to 50.0, "flash" to 16, "width" to 8192, "height" to 5464, "author" to "Ann",
        "copyright" to "(c) Ann", "latitude" to 31.2304164, "longitude" to -121.4737011, "altitude" to 119.6,
        "dateTaken" to "2026-10-15T08:30:59", "exposureBias" to 0.333, "orientation" to 7, "colorSpace" to "AdobeRGB",
    )

    private fun format(field: MetadataField, metadata: Map<String, Any>? = full) = MetadataFormatter.format(field, metadata, modified)

    @Test
    fun `every variable has its file-name safe format`() {
        assertEquals(25, MetadataField.entries.size)
        assertEquals(BatchRenameValues.METADATA_VARIABLES.toSet(), MetadataField.entries.map { it.token }.toSet())
        val expected = mapOf(
            MetadataField.CAMERA_MAKE to "Canon", MetadataField.CAMERA_MODEL to "EOS R5-C", MetadataField.LENS to "RF24-105mm",
            MetadataField.ISO to "400", MetadataField.APERTURE to "f2.8", MetadataField.SHUTTER to "1-250s", MetadataField.FOCAL to "50mm",
            MetadataField.FLASH to "Off", MetadataField.WIDTH to "8192", MetadataField.HEIGHT to "5464", MetadataField.AUTHOR to "Ann",
            MetadataField.COPYRIGHT to "(c) Ann", MetadataField.LATITUDE to "31.230416", MetadataField.LONGITUDE to "-121.473701",
            MetadataField.ALTITUDE to "120m", MetadataField.DATE_TAKEN_YEAR to "2026", MetadataField.DATE_TAKEN_YEAR_TWO_DIGITS to "26",
            MetadataField.DATE_TAKEN_MONTH to "10", MetadataField.DATE_TAKEN_DAY to "15", MetadataField.DATE_TAKEN_HOUR to "08",
            MetadataField.DATE_TAKEN_MINUTE to "30", MetadataField.DATE_TAKEN_SECOND to "59", MetadataField.EXPOSURE_BIAS to "+0.3EV",
            MetadataField.ORIENTATION to "90M", MetadataField.COLOR_SPACE to "AdobeRGB",
        )
        expected.forEach { (field, value) -> assertEquals(field.token, value, format(field)) }
    }

    @Test
    fun `numbers round and keep their units`() {
        fun with(key: String, value: Any) = full + (key to value)
        assertEquals("f11", format(MetadataField.APERTURE, with("fNumber", 11.0)))
        assertEquals("f1.8", format(MetadataField.APERTURE, with("fNumber", 1.79)))
        assertEquals("2s", format(MetadataField.SHUTTER, with("exposureTime", 2.0)))
        assertEquals("1.5s", format(MetadataField.SHUTTER, with("exposureTime", 1.5)))
        assertEquals("1-60s", format(MetadataField.SHUTTER, with("exposureTime", 1.0 / 60)))
        assertEquals("4.7mm", format(MetadataField.FOCAL, with("focalLength", 4.71)))
        assertEquals("On", format(MetadataField.FLASH, with("flash", 25)))
        assertEquals("-1EV", format(MetadataField.EXPOSURE_BIAS, with("exposureBias", -1.0)))
        assertEquals("0EV", format(MetadataField.EXPOSURE_BIAS, with("exposureBias", 0.02)))
        assertEquals("-5m", format(MetadataField.ALTITUDE, with("altitude", -4.6)))
        assertEquals(listOf("0", "0M", "180", "180M", "270M", "90", "90M", "270"), (1..8).map { format(MetadataField.ORIENTATION, with("orientation", it)) })
        assertEquals(null, format(MetadataField.ORIENTATION, with("orientation", 9)))
        assertEquals(null, format(MetadataField.COLOR_SPACE, with("colorSpace", "P3")))
    }

    @Test
    fun `missing values are empty and the date taken falls back to the modification time`() {
        assertEquals(null, format(MetadataField.CAMERA_MAKE, emptyMap()))
        assertEquals(null, format(MetadataField.ISO, mapOf("iso" to "400")))
        assertEquals("2020", format(MetadataField.DATE_TAKEN_YEAR, null))
        assertEquals("13", format(MetadataField.DATE_TAKEN_HOUR, mapOf("dateTaken" to "not a date")))
    }

    @Test
    fun `metadata variables come before the date variables and stay literal while off`() {
        val on = ReplacementTemplate.parse("\$DATE_TAKEN_YYYY-\$D-\$ISOx", enumerate = false, random = false, dateTime = true, metadata = true).parts
        assertEquals(
            listOf(
                TemplatePart.Metadata(MetadataField.DATE_TAKEN_YEAR), TemplatePart.Literal("-"), TemplatePart.Date(DateField.DAY),
                TemplatePart.Literal("-"), TemplatePart.Metadata(MetadataField.ISO), TemplatePart.Literal("x"),
            ),
            on,
        )
        val off = ReplacementTemplate.parse("\$DATE_TAKEN_mm \$D", enumerate = false, random = false, dateTime = true, metadata = false).parts
        assertEquals(listOf(TemplatePart.Literal("\$DATE_TAKEN_mm "), TemplatePart.Date(DateField.DAY)), off)
        assertEquals(listOf(TemplatePart.Dollar, TemplatePart.Literal("ISO")), ReplacementTemplate.parse("\$\$ISO", false, false, false, metadata = true).parts)
    }

    @Test
    fun `the engine expands metadata per item and reports empty values`() {
        val options = RenameOptions(search = "^", regex = true, replace = "\$CAMERA_MODEL_\$DATE_TAKEN_YYYY_", target = RenameTarget.NAME, metadata = true)
        val request = EngineFixtures.request(options, "a.jpg", "b.jpg", "c.txt").let { request ->
            request.copy(items = request.items.mapIndexed { index, item -> if (index == 0) item.copy(metadata = full) else item })
        }
        val plan = EngineFixtures.engine.plan(request)
        assertEquals(listOf("EOS R5-C_2026_a.jpg", "_2020_b.jpg", "_2020_c.txt"), plan.items.map { it.newName })
        assertEquals(listOf(false, true, true), plan.items.map { it.metadataMissing })
        val dateOnly = EngineFixtures.engine.plan(EngineFixtures.request(options.copy(replace = "\$DATE_TAKEN_YYYY_"), "b.jpg"))
        assertFalse("the date taken falls back instead of going missing", dateOnly.items.single().metadataMissing)
        val off = EngineFixtures.engine.plan(EngineFixtures.request(options.copy(metadata = false), "a.jpg"))
        assertEquals("\$CAMERA_MODEL_\$DATE_TAKEN_YYYY_a.jpg", off.items.single().newName)
        assertTrue(off.items.none { it.metadataMissing })
    }
}

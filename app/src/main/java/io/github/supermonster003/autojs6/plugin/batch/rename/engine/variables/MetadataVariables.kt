package io.github.supermonster003.autojs6.plugin.batch.rename.engine.variables

import org.autojs.plugin.batchrename.api.BatchRenameMetadataKeys
import org.autojs.plugin.batchrename.api.BatchRenameValues
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToLong

/**
 * The 25 photo metadata variables of PowerRename (roadmap D34, appendix A), matched before the date variables with the
 * longest name first (`$DATE_TAKEN_YYYY` is not `$D`), case sensitive (`$DATE_TAKEN_MM` is the month, `$DATE_TAKEN_mm`
 * the minute).
 * zh-CN: PowerRename 的 25 个照片元数据变量 (路线图 D34, 附录 A), 先于日期变量匹配, 最长名称优先 (`$DATE_TAKEN_YYYY` 不是 `$D`),
 * 区分大小写 (`$DATE_TAKEN_MM` 为月, `$DATE_TAKEN_mm` 为分).
 */
internal enum class MetadataField(val token: String) {
    CAMERA_MAKE("CAMERA_MAKE"), CAMERA_MODEL("CAMERA_MODEL"), LENS("LENS"), ISO("ISO"), APERTURE("APERTURE"),
    SHUTTER("SHUTTER"), FOCAL("FOCAL"), FLASH("FLASH"), WIDTH("WIDTH"), HEIGHT("HEIGHT"), AUTHOR("AUTHOR"),
    COPYRIGHT("COPYRIGHT"), LATITUDE("LATITUDE"), LONGITUDE("LONGITUDE"), ALTITUDE("ALTITUDE"),
    DATE_TAKEN_YEAR("DATE_TAKEN_YYYY"), DATE_TAKEN_YEAR_TWO_DIGITS("DATE_TAKEN_YY"), DATE_TAKEN_MONTH("DATE_TAKEN_MM"),
    DATE_TAKEN_DAY("DATE_TAKEN_DD"), DATE_TAKEN_HOUR("DATE_TAKEN_HH"), DATE_TAKEN_MINUTE("DATE_TAKEN_mm"),
    DATE_TAKEN_SECOND("DATE_TAKEN_SS"), EXPOSURE_BIAS("EXPOSURE_BIAS"), ORIENTATION("ORIENTATION"), COLOR_SPACE("COLOR_SPACE");

    val dateTaken: Boolean get() = token.startsWith("DATE_TAKEN_")

    companion object {
        private val BY_LENGTH = entries.sortedByDescending { it.token.length }

        fun matchAt(text: String, index: Int): MetadataField? = BY_LENGTH.firstOrNull { text.startsWith(it.token, index) }
    }
}

/**
 * Writes the raw metadata of an item ([BatchRenameMetadataKeys]) as variable values, file-name safe and the same in
 * every locale (roadmap D47-D50): `f2.8`, `1-250s` (`2s` from one second on), `50mm`, `+0.3EV`, `120m`, `On` / `Off`,
 * `0` / `90` / `180` / `270` with `M` when mirrored, `sRGB` / `AdobeRGB` / `Uncalibrated`, degrees with six decimals.
 * `$DATE_TAKEN_*` use the recorded local time and fall back to the modification time. Text values replace `/` with `-`.
 * zh-CN: 把条目的原始元数据写成变量值, 文件名安全且与语言无关 (路线图 D47-D50). `$DATE_TAKEN_*` 使用记录的当地时间, 缺失时回退到
 * 修改时间. 文本值中的 `/` 替换为 `-`.
 */
internal object MetadataFormatter {

    /** The value of [field], or null when [metadata] lacks it (never for the date taken). zh-CN: [field] 的值; 缺失时为 null. */
    fun format(field: MetadataField, metadata: Map<String, Any>?, modified: LocalDateTime): String? {
        val values = metadata.orEmpty()
        if (field.dateTaken) {
            val taken = (values[BatchRenameMetadataKeys.DATE_TAKEN] as? String)?.let(::localDateTime) ?: modified
            return when (field) {
                MetadataField.DATE_TAKEN_YEAR -> taken.year.toString().padStart(4, '0')
                MetadataField.DATE_TAKEN_YEAR_TWO_DIGITS -> Math.floorMod(taken.year, 100).toString().padStart(2, '0')
                MetadataField.DATE_TAKEN_MONTH -> two(taken.monthValue)
                MetadataField.DATE_TAKEN_DAY -> two(taken.dayOfMonth)
                MetadataField.DATE_TAKEN_HOUR -> two(taken.hour)
                MetadataField.DATE_TAKEN_MINUTE -> two(taken.minute)
                else -> two(taken.second)
            }
        }
        fun text(key: String) = (values[key] as? String)?.replace('/', '-')?.takeIf { it.isNotEmpty() }
        fun number(key: String) = (values[key] as? Number)?.toDouble()?.takeIf(Double::isFinite)
        fun integer(key: String) = (values[key] as? Number)?.toLong()
        return when (field) {
            MetadataField.CAMERA_MAKE -> text(BatchRenameMetadataKeys.CAMERA_MAKE)
            MetadataField.CAMERA_MODEL -> text(BatchRenameMetadataKeys.CAMERA_MODEL)
            MetadataField.LENS -> text(BatchRenameMetadataKeys.LENS)
            MetadataField.AUTHOR -> text(BatchRenameMetadataKeys.AUTHOR)
            MetadataField.COPYRIGHT -> text(BatchRenameMetadataKeys.COPYRIGHT)
            MetadataField.ISO -> integer(BatchRenameMetadataKeys.ISO)?.toString()
            MetadataField.APERTURE -> number(BatchRenameMetadataKeys.F_NUMBER)?.let { "f" + decimal(it) }
            MetadataField.SHUTTER -> number(BatchRenameMetadataKeys.EXPOSURE_TIME)?.takeIf { it > 0 }?.let(::shutter)
            MetadataField.FOCAL -> number(BatchRenameMetadataKeys.FOCAL_LENGTH)?.let { decimal(it) + "mm" }
            MetadataField.FLASH -> integer(BatchRenameMetadataKeys.FLASH)?.let { if (it and 1L == 1L) "On" else "Off" }
            MetadataField.WIDTH -> integer(BatchRenameMetadataKeys.WIDTH)?.toString()
            MetadataField.HEIGHT -> integer(BatchRenameMetadataKeys.HEIGHT)?.toString()
            MetadataField.LATITUDE -> number(BatchRenameMetadataKeys.LATITUDE)?.let(::degrees)
            MetadataField.LONGITUDE -> number(BatchRenameMetadataKeys.LONGITUDE)?.let(::degrees)
            MetadataField.ALTITUDE -> number(BatchRenameMetadataKeys.ALTITUDE)?.let { "${it.roundToLong()}m" }
            MetadataField.EXPOSURE_BIAS -> number(BatchRenameMetadataKeys.EXPOSURE_BIAS)?.let(::exposureBias)
            MetadataField.ORIENTATION -> integer(BatchRenameMetadataKeys.ORIENTATION)?.let(::orientation)
            MetadataField.COLOR_SPACE -> (values[BatchRenameMetadataKeys.COLOR_SPACE] as? String)?.takeIf { it in BatchRenameValues.COLOR_SPACES }
            else -> null
        }
    }

    /** `yyyy-MM-ddTHH:mm:ss` as sent by the host, or null. zh-CN: 宿主发送的本地日期时间, 或 null. */
    private fun localDateTime(text: String): LocalDateTime? = try {
        LocalDateTime.parse(text)
    } catch (_: DateTimeParseException) {
        null
    }

    private fun two(value: Int) = value.toString().padStart(2, '0')

    /** One decimal at most, without a trailing `.0`: 2.8, 11, 4.7. zh-CN: 至多一位小数, 不保留结尾的 `.0`. */
    private fun decimal(value: Double): String {
        val text = String.format(Locale.ROOT, "%.1f", value)
        return text.removeSuffix(".0")
    }

    /** `1-250s` below one second (denominator rounded), `2s` / `1.5s` from one second on. zh-CN: 快门速度. */
    private fun shutter(seconds: Double): String =
        if (seconds < 1.0) "1-${(1.0 / seconds).roundToLong()}s" else decimal(seconds) + "s"

    private fun exposureBias(value: Double): String {
        val text = decimal(abs(value))
        return when {
            text == "0" -> "0EV"
            value > 0 -> "+${text}EV"
            else -> "-${text}EV"
        }
    }

    private fun degrees(value: Double): String = String.format(Locale.ROOT, "%.6f", value)

    /** EXIF orientation 1-8 as the rotation in degrees, `M` when mirrored. zh-CN: EXIF 方向 1-8 表示的旋转角度, 镜像时加 `M`. */
    private fun orientation(value: Long): String? = when (value) {
        1L -> "0"
        2L -> "0M"
        3L -> "180"
        4L -> "180M"
        5L -> "270M"
        6L -> "90"
        7L -> "90M"
        8L -> "270"
        else -> null
    }
}

package com.daniel_espinoza.inline_error.settings

import java.awt.Color
import scala.language.implicitConversions

object SettingUtil {
  implicit def color2Hex(color: Color): String = f"${color.getRGB}%x"

  implicit def hex2Color(hexStr: String): Color = new Color(Integer.parseInt(hexStr, 16))
}

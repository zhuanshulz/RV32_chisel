package eh1

object DefaultSettings {
  def apply() = Map(
    "ResetVector" -> 0x00000000L,
    "IsRV32"      -> true
  )
}

object Settings {
  var settings: Map[String, AnyVal] = DefaultSettings()
  def get(field: String) = {
    settings(field).asInstanceOf[Boolean]
  }
  def getLong(field: String) = {
    settings(field).asInstanceOf[Long]
  }
  def getInt(field: String) = {
    settings(field).asInstanceOf[Int]
  }
}

trait CoreParameter {
  // General Parameter for NutShell
  val XLEN = if (Settings.get("IsRV32")) 32 else 64
  val AddrBits = 32 // AddrBits is used in some cases
  val DataBits = XLEN
  val DataBytes = DataBits / 8
}
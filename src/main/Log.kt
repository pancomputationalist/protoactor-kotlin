package protoactor

enum class LogLevel {
  None,
  Error,
  Debug,
  Verbose
}

var logLevel = LogLevel.Debug

fun logDebug(s: Any) {
  if (logLevel >= LogLevel.Debug)
    println("[Debug] $s")
}

fun logVerbose(s: Any) {
  if (logLevel >= LogLevel.Verbose)
    println("[Verbose] $s")
}

fun logError(s: Any) {
  if (logLevel >= LogLevel.Error)
    println("[Error] $s")
}

fun logError(s: Any, ex: Exception) {
  if (logLevel >= LogLevel.Error)
    println("[Error] $s ($ex)")
}
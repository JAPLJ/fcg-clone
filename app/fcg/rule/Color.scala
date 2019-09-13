package fcg.rule

sealed trait Color

object Color {
  case object White extends Color
  case object Red extends Color
  case object Blue extends Color
  case object Green extends Color
  case object Yellow extends Color

  val Colors: Seq[Color] = Seq(White, Red, Blue, Green, Yellow)
}

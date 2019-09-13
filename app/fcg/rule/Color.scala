package fcg.rule

sealed case class Color(name: String)

object Color {
  case object White extends Color("白")
  case object Red extends Color("赤")
  case object Blue extends Color("青")
  case object Green extends Color("緑")
  case object Yellow extends Color("黄")

  val Colors: Seq[Color] = Seq(White, Red, Blue, Green, Yellow)
}

package fcg.rule

sealed case class Color(name: String)

object Color {
  object White extends Color("白")
  object Red extends Color("赤")
  object Blue extends Color("青")
  object Green extends Color("緑")
  object Yellow extends Color("黄")

  val Colors: Seq[Color] = Seq(White, Red, Blue, Green, Yellow)
}

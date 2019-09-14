package fcg.rule

sealed case class Color(name: String, englishName: String)

object Color {
  object White extends Color("白", "white")
  object Red extends Color("赤", "red")
  object Blue extends Color("青", "blue")
  object Green extends Color("緑", "green")
  object Yellow extends Color("黄", "yellow")

  val Colors: Seq[Color] = Seq(White, Red, Blue, Green, Yellow)
}

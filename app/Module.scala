import com.google.inject.{AbstractModule, Singleton}
import fcg.rule.cards.{MonsterCards, SpellCards}

class Module extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[InitializeCards]).asEagerSingleton()
  }
}

@Singleton
class InitializeCards {
  MonsterCards
  SpellCards
}

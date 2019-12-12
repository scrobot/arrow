package arrow.core

import arrow.Kind
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.id.applicative.applicative
import arrow.core.extensions.id.bimonad.bimonad
import arrow.core.extensions.id.comonad.comonad
import arrow.core.extensions.id.eq.eq
import arrow.core.extensions.id.eqK.eqK
import arrow.core.extensions.id.foldable.foldable
import arrow.core.extensions.id.hash.hash
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.id.monoid.monoid
import arrow.core.extensions.id.repeat.repeat
import arrow.core.extensions.id.semialign.semialign
import arrow.core.extensions.id.semigroup.semigroup
import arrow.core.extensions.id.show.show
import arrow.core.extensions.id.traverse.traverse
import arrow.core.extensions.id.unzip.unzip
import arrow.core.extensions.monoid
import arrow.core.extensions.semigroup
import arrow.test.UnitSpec
import arrow.test.generators.genK
import arrow.test.laws.BimonadLaws
import arrow.test.laws.EqKLaws
import arrow.test.laws.HashLaws
import arrow.test.laws.MonoidLaws
import arrow.test.laws.RepeatLaws
import arrow.test.laws.SemialignLaws
import arrow.test.laws.ShowLaws
import arrow.test.laws.TraverseLaws
import arrow.test.laws.UnzipLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class IdTest : UnitSpec() {
  val EQ: Eq<Kind<ForId, Kind<ForId, Int>>> = Eq { a, b ->
    a.value().value() == b.value().value()
  }

  init {
    testLaws(
      MonoidLaws.laws(Id.monoid(Int.monoid()), Gen.constant(Id(1)), Id.eq(Int.eq())),
      ShowLaws.laws(Id.show(), Eq.any()) { Id(it) },
      TraverseLaws.laws(Id.traverse(), Id.applicative(), ::Id, Eq.any()),
      BimonadLaws.laws(Id.bimonad(), Id.monad(), Id.comonad(), ::Id, Eq.any(), EQ, Eq.any()),
      HashLaws.laws(Id.hash(Int.hash()), Id.eq(Int.eq())) { Id(it) },
      EqKLaws.laws(
        Id.eqK(),
        Id.genK()
      ),
      SemialignLaws.laws(Id.semialign(),
        Id.genK(),
        Id.eqK(),
        Id.foldable()
      ),
      RepeatLaws.laws(Id.repeat(),
        Id.genK(),
        Id.eqK(),
        Id.foldable()
      ),
      UnzipLaws.laws(Id.unzip(),
        Id.genK(),
        Id.eqK(),
        Id.foldable()
      )
    )

    "Semigroup of Id<A> is Id<Semigroup<A>>" {
      forAll { a: Int ->
        val left = Id.semigroup(Int.semigroup()).run {
          Id(a).combine(Id(a))
        }

        val right = Id(Int.monoid().run { a.combine(a) })

        Id.eq(Int.eq()).run { left.eqv(right) }
      }
    }

    "Id<A>.empty() is Id{A.empty()}" {
      forAll { a: Int, b: Int ->
        val left = Id.monoid(Int.monoid()).run { empty() }
        val right = Id(Int.monoid().run { empty() })
        Id.eq(Int.eq()).run { left.eqv(right) }
      }
    }
  }
}

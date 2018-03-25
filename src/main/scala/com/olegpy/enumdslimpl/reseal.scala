package com.olegpy.enumdslimpl
import scala.meta._


object reseal {
  def apply(mods: Seq[Mod]): Seq[Mod] = {
    if (mods.exists(_.is[Mod.Final])) {
      abort("Enum base class cannot be declared final")
    }

    val addons = Seq(mod"sealed", mod"abstract")
      .filterNot(leaf => mods.exists(_.getClass == leaf.getClass))

    addons ++ mods
  }
}

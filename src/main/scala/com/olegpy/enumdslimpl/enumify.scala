package com.olegpy.enumdslimpl

import scala.meta._


object enumify {
  def apply(toc: Either[Defn.Class, Defn.Trait]) = toc match {
    case Left(q"""
    ..$mods class $name[..$tp] ..$ctorMods (
      ...$paramss
    ) extends $cls with ..$things {
    ..$stats
    }
    """) => Left {
      q"""
         ..${reseal(mods)} class $name[..$tp] ..$ctorMods (
          ...$paramss
         ) extends $cls with _root_.enumeratum.EnumEntry with ..$things {
          ..$stats
         }
       """
    }

    case Left(q"""
    ..$mods class $name[..$tp] ..$ctorMods (
      ...$paramss
    ) {
    ..$stats
    }
    """) => Left {
      q"""
         ..${reseal(mods)} class $name[..$tp] ..$ctorMods (
          ...$paramss
         ) extends _root_.enumeratum.EnumEntry {
          ..$stats
         }
       """
    }

    case Right(q"""
       ..$mods trait $name extends ..$others {
        ..$stats
      }
    """) => Right {
      q"""
         ..${reseal(mods)} trait $name extends _root_.enumeratum.EnumEntry with ..$others {
          ..$stats
         }
       """
    }
  }
}

package com.cecere.examples.scala.squeryl

import org.squeryl.Schema
import com.cecere.examples.scala.domain.DemoObject

object SchemaLibrary extends Schema {
  val demoObjects = table[DemoObject]
}

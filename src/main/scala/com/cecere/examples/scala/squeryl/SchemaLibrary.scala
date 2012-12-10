package com.cecere.examples.scala.squeryl

import org.squeryl.Schema
import com.cecere.examples.scala.domain.DemoObject
/**
 * Simple Singleton object that defines our example database schema. 
 *
 */
object SchemaLibrary extends Schema {
  val demoObjects = table[DemoObject]
}

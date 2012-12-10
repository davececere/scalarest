package com.cecere.examples.scala.plan

import unfiltered.filter.Plan
import javax.servlet.ServletRequest
import org.clapper.avsl.Logger
import unfiltered.request._
import unfiltered.response._
import com.cecere.examples.scala.service.DemoObjectServiceComponent
import com.cecere.examples.scala.service.DefaultDemoObjectServiceComponent
import net.liftweb.json._
import com.cecere.examples.scala.domain.DemoObject
import com.cecere.examples.scala.service.DbDemoObjectServiceComponent

class DemoObjectPlan extends BaseDemoObjectPlan with DbDemoObjectServiceComponent {
}

//A Plan that has a dependency on members of DemoObjectServiceComponent
class BaseDemoObjectPlan extends Plan {
  this: DemoObjectServiceComponent =>

  implicit val formats=Serialization.formats(ShortTypeHints(List(classOf[DemoObject])))
  val logger = Logger(classOf[BaseDemoObjectPlan])
  
  def intent ={
    
     case req @ Path(Seg("demoObjects" :: Nil)) => req match {
       case req @ GET(_) =>
       	 logger.debug("GET /demoObjects/")
       	 val demoObjects = demoObjectService.findAll
       	 JsonContent ~> Ok ~> ResponseString(Serialization.write(demoObjects))
       case req @ POST(_) => 
         logger.debug("POST /demoObjects/")
         val demoObject = Serialization.read[DemoObject](Body.reader(req))
         JsonContent ~> Created ~> ResponseString(Serialization.write(demoObjectService.create(demoObject)))
       case _ => Pass //not interested. let a different plan deal with this
    }
     
    case req @ Path(Seg("demoObjects" :: id :: Nil)) => req match {
       case req @ GET(_) =>
       	 logger.debug("GET /demoObjects/"+id)
       	 val demoObject = demoObjectService.find(id toLong)
       	 demoObject match {
       	   case None => NotFound
       	   case Some(dObj) => JsonContent ~> Ok ~> ResponseString(Serialization.write(dObj))
       	 }
       case req @ PUT(_) => 
         logger.debug("PUT /demoObjects/"+id)
         val demoObject = Serialization.read[DemoObject](Body.reader(req))
         JsonContent ~> Ok ~> ResponseString(Serialization.write(demoObjectService.update((id toLong),demoObject)))
       case req @ DELETE(_) => 
         logger.debug("DELETE /demoObjects/"+id)
         demoObjectService.delete(id toLong)
         NoContent
       case _ => Pass //not interested. let a different plan deal with this
    }
  }
}


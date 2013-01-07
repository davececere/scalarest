package com.cecere.examples.scala.plan

import org.junit.Test
import org.junit.Assert._
import org.easymock.EasyMock._
import com.cecere.examples.scala.service.DemoObjectServiceComponent
import org.junit.Test
import com.cecere.examples.scala.service.DemoObjectService

class DemoObjectPlanTest {
  
  def mockDemoObjectService:DemoObjectService = createMock(classOf[DemoObjectService])
  
  trait TestDemoObjectServiceComponent extends DemoObjectServiceComponent {
	def demoObjectService = mockDemoObjectService
  }
  
  class TestDemoObjectPlan extends BaseDemoObjectPlan with TestDemoObjectServiceComponent
  

  def run(mocks : AnyRef*)(block : => Unit){
	  replay(mocks : _*)
	  block
	  verify(mocks : _*)
	  reset(mocks : _*)
  }
}
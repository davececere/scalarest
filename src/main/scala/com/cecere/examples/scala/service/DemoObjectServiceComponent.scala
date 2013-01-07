package com.cecere.examples.scala.service

import com.cecere.examples.scala.domain.DemoObject
import org.squeryl.dsl.ast.LogicalBoolean

/**
 * See the Cake Pattern for a description of why the code looks like this
 */

//Trait defining an service component for DemoObjects to be used by other components
trait DemoObjectServiceComponent {
  //internal demo service accessible by this component
  //allows for DI of the actual service implementation into the component
  def demoObjectService: DemoObjectService
}
  //definition of what the internal service does
  trait DemoObjectService {
    def findAll: List[DemoObject]
    def find(id:Long): Option[DemoObject]
    def create(newObj:DemoObject):DemoObject
    def update(id:Long,newObj:DemoObject):DemoObject
    def delete(id:Long):Unit
    def deleteAll():Unit
  }

//Implementation trait using squeryl dsl
trait DbDemoObjectServiceComponent extends DemoObjectServiceComponent {

  import org.squeryl.SessionFactory
  import org.squeryl.Session
  import org.squeryl.adapters.MySQLAdapter
  
  def demoObjectService = new DbDemoObjectService
  
  //runs just during initialization of instance. Typical squeryl initialization code
  Class.forName("com.mysql.jdbc.Driver");
  SessionFactory.concreteFactory = Some( ()=>  
  	Session.create(java.sql.DriverManager.getConnection("jdbc:mysql://localhost:3306/scalarest","root",""),new MySQLAdapter)
  )
  /*
   * Actual implementation of the service that will be injected via mixing in the parent component
   */
  class DbDemoObjectService extends DemoObjectService {
    import com.cecere.examples.scala.squeryl.SchemaLibrary._
    import org.squeryl.PrimitiveTypeMode._
    
    def findAll:List[DemoObject] = {
      //will create or join current transaction
      inTransaction {
        //the squeryl dsl using scala paradigms to create database queries
        //this is a typical way to apply an anonymous function to a collection
        from(demoObjects)(s => select(s)).toList
      }
    }
    def find(id:Long):Option[DemoObject] = {
	  inTransaction {
	    val q = demoObjects.where(dObj => dObj.id === id)
	    if (q.isEmpty) None else Some(q.single)
	  }
    }
    def create(newObj:DemoObject):DemoObject = {
      inTransaction {
        demoObjects.insert(newObj)
      }
    }
    def update(id:Long,newObj:DemoObject) = {
      inTransaction {
    	  org.squeryl.PrimitiveTypeMode.update(demoObjects)(dObj => 
    	    where(dObj.id === id)
    	    set(dObj.name := newObj.name)
    	  )
    	  //get updated result
    	  find(id).get
      }
    }
    def delete(id:Long) = {
      inTransaction {
    	  demoObjects.deleteWhere(dObj => dObj.id === id)
      }
    }
    def deleteAll() = {
      inTransaction {
        //not possible equality. ids start at 1. this was the easiest way i could find to do this
        demoObjects.deleteWhere(dObj => dObj.id <> -1)
      }
    }
  }
}
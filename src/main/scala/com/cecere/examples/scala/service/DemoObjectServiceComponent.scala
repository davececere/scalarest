package com.cecere.examples.scala.service

import com.cecere.examples.scala.domain.DemoObject
import org.squeryl.dsl.ast.LogicalBoolean

/**
 * See the Cake Pattern for a description of why the code looks like this
 */

//Trait defining an service component for DemoObjects to be used by other objects
trait DemoObjectServiceComponent {
  //internal demo service accessible by this component
  //allows for DI of the actual service implementation into the component
  def demoObjectService: DemoObjectService
  //definition of what the internal service does
  trait DemoObjectService {
    def findAll: List[DemoObject]
    def find(id:Long): Option[DemoObject]
    def create(newObj:DemoObject):DemoObject
    def update(id:Long,newObj:DemoObject):DemoObject
    def delete(id:Long):Unit
    def deleteAll():Unit
  }
}

//Implementation trait.
trait DefaultDemoObjectServiceComponent extends DemoObjectServiceComponent {

  def demoObjectService = new DefaultDemoObjectService

  class DefaultDemoObjectService extends DemoObjectService {
    def findAll:List[DemoObject] = {
      val demoObject = new DemoObject(1L,"demoObject1")
      demoObject :: Nil
    }
    def find(id:Long):Option[DemoObject] =  Some(new DemoObject(id,"demoObject"+id))
    def create(newObj:DemoObject):DemoObject = newObj
    def update(id:Long,newObj:DemoObject) = newObj
    def delete(id:Long) ={}
    def deleteAll() = {}
  }
}

//Implementation trait.
trait DbDemoObjectServiceComponent extends DemoObjectServiceComponent {

  import org.squeryl.SessionFactory
  import org.squeryl.Session
  import org.squeryl.adapters.MySQLAdapter
  
  def demoObjectService = new DbDemoObjectService
  
  Class.forName("com.mysql.jdbc.Driver");
  SessionFactory.concreteFactory = Some( ()=>  
  	Session.create(java.sql.DriverManager.getConnection("jdbc:mysql://localhost:3306/scalarest","root",""),new MySQLAdapter)
  )

  class DbDemoObjectService extends DemoObjectService {
    import com.cecere.examples.scala.squeryl.SchemaLibrary._
    import org.squeryl.PrimitiveTypeMode._
    
    def findAll:List[DemoObject] = {
      inTransaction {
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
        //not possible equality. ids start at 1
        demoObjects.deleteWhere(dObj => dObj.id <> -1)
      }
    }
  }
}